package org.apollo.game.msg.decoder;

import org.apollo.game.model.inter.Interfaces.InventoryAmountOption;
import org.apollo.game.msg.MessageDecoder;
import org.apollo.game.msg.annotate.DecodesMessage;
import org.apollo.game.msg.impl.ItemActionMessage;
import org.apollo.net.codec.game.DataOrder;
import org.apollo.net.codec.game.DataTransformation;
import org.apollo.net.codec.game.DataType;
import org.apollo.net.codec.game.GamePacket;
import org.apollo.net.codec.game.GamePacketReader;

/**
 * A {@link MessageDecoder} for the {@link ItemActionMessage}.
 *
 * @author Graham
 */
@DecodesMessage(122)
public final class FirstItemActionMessageDecoder implements MessageDecoder<ItemActionMessage> {

	@Override
	public ItemActionMessage decode(GamePacket packet) {
		GamePacketReader reader = new GamePacketReader(packet);
		int interfaceId = (int) reader.getUnsigned(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD);
		int slot = (int) reader.getUnsigned(DataType.SHORT, DataTransformation.ADD);
		int id = (int) reader.getUnsigned(DataType.SHORT, DataOrder.LITTLE);
		return new ItemActionMessage(InventoryAmountOption.OPTION_ONE, interfaceId, id, slot);
	}

}