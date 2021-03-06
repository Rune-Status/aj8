package org.apollo.game.model.inter.dialog;

import org.apollo.game.model.Mob;
import org.apollo.game.model.Player;
import org.apollo.game.model.inter.InterfaceListener;
import org.apollo.game.msg.Message;

/**
 * An {@link InterfaceListener} which listens for dialogue events.
 *
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public interface DialogueListener extends InterfaceListener {

	/**
	 * Returns the lines of dialogue this dialogue contains.
	 */
	String[] getLines();

	/**
	 * Sends any {@link Message}s for this dialogue listener prior to execution.
	 * Interface models, text and animations should be sent here.
	 *
	 * @param player The player to send the messages for.
	 * @return The id of the finalized dialogue interface.
	 */
	int send(Player player);

	/**
	 * Returns the maximum amount of entries that can be placed on this
	 * dialogue.
	 */
	int getMaximumEntries();

	/**
	 * Returns the minimum amount of entries that can be placed on this
	 * dialogue.
	 */
	default int getMinimumEntries() {
		return 0;
	}

	@Override
	default void close() {
		/* Method intended to be overridden. */
	}

	/**
	 * An event which is intended to be fired after the "Click here to continue"
	 * object is clicked, by default this method has no action.
	 */
	default void continued() {
		/* Method intended to be overridden. */
	}

	/**
	 * Returns the facial expression expressed by either a {@link Player} or
	 * {@link Mob} from within a dialogue, this only applies to the types of
	 * {@link DialogueType#MOB_STATEMENT} and
	 * {@link DialogueType#PLAYER_STATEMENT}. The default expression is
	 * {@link DialogueExpression#NO_EXPRESSION}
	 */
	default DialogueExpression expression() {
		/* Method intended to be overridden. */
		return DialogueExpression.NO_EXPRESSION;
	}

	/**
	 * Returns the next dialogue listener within the chain of dialogue
	 * listeners, if there is no next listener then {@code null} is returned, by
	 * default this method returns {@code null}.
	 */
	default DialogueListener next() {
		/* Method intended to be overridden. */
		return null;
	}

	/**
	 * An event which is intended to be fired after an option was clicked during
	 * the dialogue type of {@link DialogueType#OPTION}.
	 *
	 * @param option The option, only five option dialogues are supported.
	 */
	default void optionClicked(DialogueOption option) {
		/* Method intended to be overridden. */
	}

}