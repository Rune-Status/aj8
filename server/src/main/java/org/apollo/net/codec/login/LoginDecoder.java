package org.apollo.net.codec.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.List;

import net.burtleburtle.bob.rand.IsaacAlgorithm;

import org.apollo.security.IsaacRandomPair;
import org.apollo.security.PlayerCredentials;
import org.apollo.util.ByteBufUtil;

/**
 * A {@link ByteToMessageDecoder} which decodes the login request frames.
 *
 * @author Graham
 */
public final class LoginDecoder extends ByteToMessageDecoder {

    /**
     * The secure random number generator.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * The login packet length.
     */
    private int loginLength;

    /**
     * The reconnecting flag.
     */
    private boolean reconnecting;

    /**
     * The server-side session key.
     */
    private long serverSeed;

    /**
     * The username hash.
     */
    private int usernameHash;

    /**
     * The current login decoder state
     */
    private LoginDecoderState state = LoginDecoderState.LOGIN_HANDSHAKE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
	switch (state) {
	case LOGIN_HANDSHAKE:
	    decodeHandshake(ctx, in);
	    break;
	case LOGIN_HEADER:
	    decodeHeader(in);
	    break;
	case LOGIN_PAYLOAD:
	    decodePayload(ctx, in, out);
	    break;
	default:
	    throw new IllegalStateException("Invalid login decoder state");
	}
    }

    /**
     * Decodes the handshake state.
     *
     * @param ctx The channels context.
     * @param in The input buffer.
     */
    private void decodeHandshake(ChannelHandlerContext ctx, ByteBuf in) {
	if (!in.isReadable()) {
	    return;
	}

	usernameHash = in.readUnsignedByte();
	serverSeed = RANDOM.nextLong();

	ByteBuf resp = ctx.alloc().buffer(17);
	resp.writeByte(LoginConstants.STATUS_EXCHANGE_DATA);
	resp.writeLong(0);
	resp.writeLong(serverSeed);
	ctx.channel().writeAndFlush(resp);

	state = LoginDecoderState.LOGIN_HEADER;
    }

    /**
     * Decodes the header state.
     *
     * @param in The input buffer.
     */
    private void decodeHeader(ByteBuf in) {
	if (in.readableBytes() < 2) {
	    return;
	}

	int loginType = in.readUnsignedByte();

	if (loginType != LoginConstants.TYPE_STANDARD && loginType != LoginConstants.TYPE_RECONNECTION) {
	    throw new IllegalStateException("Invalid login type");
	}

	reconnecting = loginType == LoginConstants.TYPE_RECONNECTION;
	loginLength = in.readUnsignedByte();

	state = LoginDecoderState.LOGIN_PAYLOAD;
    }

    /**
     * Decodes the payload state.
     *
     * @param ctx The channels context.
     * @param in The input buffer.
     * @param out The {@link List} to which written data should be added.
     */
    private void decodePayload(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
	if (in.readableBytes() < loginLength) {
	    return;
	}

	ByteBuf payload = in.readBytes(loginLength);
	if (payload.readUnsignedByte() != 0xFF) {
	    throw new IllegalStateException("Invalid magic id");
	}

	int clientVersion = payload.readUnsignedShort();
	if (clientVersion != 317) {
	    throw new IllegalStateException("Invalid client version");
	}

	int lowMemoryFlag = payload.readUnsignedByte();
	if (lowMemoryFlag != 0 && lowMemoryFlag != 1) {
	    throw new IllegalStateException("Invalid value for low memory flag");
	}

	boolean lowMemory = lowMemoryFlag == 1;

	int[] archiveCrcs = new int[9];
	for (int i = 0; i < archiveCrcs.length; i++) {
	    archiveCrcs[i] = payload.readInt();
	}

	int securePayloadLength = payload.readUnsignedByte();
	if (securePayloadLength != loginLength - 41) {
	    throw new IllegalStateException("Secure payload length mismatch");
	}

	ByteBuf securePayload = payload.readBytes(securePayloadLength);

	int secureId = securePayload.readUnsignedByte();
	if (secureId != 10) {
	    throw new IllegalStateException("Invalid secure payload id");
	}

	long clientSeed = securePayload.readLong();
	long reportedServerSeed = securePayload.readLong();
	if (reportedServerSeed != serverSeed) {
	    throw new IllegalStateException("Server seed mismatch");
	}

	int uid = securePayload.readInt();

	String username = ByteBufUtil.readString(securePayload);
	String password = ByteBufUtil.readString(securePayload);

	InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
	String address = socketAddress.getHostName();

	if (username.length() > 12 || password.length() > 20) {
	    throw new IllegalStateException("Username or password too long.");
	}

	int[] seed = new int[4];
	seed[0] = (int) (clientSeed >> 32);
	seed[1] = (int) clientSeed;
	seed[2] = (int) (serverSeed >> 32);
	seed[3] = (int) serverSeed;

	IsaacAlgorithm decodingRandom = new IsaacAlgorithm(seed);
	for (int i = 0; i < seed.length; i++) {
	    seed[i] += 50;
	}
	IsaacAlgorithm encodingRandom = new IsaacAlgorithm(seed);

	PlayerCredentials credentials = new PlayerCredentials(username, password, usernameHash, uid, address);
	IsaacRandomPair randomPair = new IsaacRandomPair(encodingRandom, decodingRandom);

	LoginRequest request = new LoginRequest(credentials, randomPair, reconnecting, lowMemory, clientVersion, archiveCrcs);

	out.add(request);

	// TODO: Necessary?
	if (in.isReadable()) {
	    out.add(in.readBytes(in.readableBytes()));
	}
    }

}
