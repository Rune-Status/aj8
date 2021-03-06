package org.apollo.game.model.obj;

import static org.apollo.game.model.obj.GameObjectOrientation.NORTH;
import static org.apollo.game.model.obj.GameObjectOrientation.SOUTH;

import org.apollo.game.model.Entity;
import org.apollo.game.model.Position;
import org.apollo.game.model.World;
import org.apollo.game.model.def.GameObjectDefinition;

/**
 * Represents a game object within the world.
 *
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class GameObject extends Entity {

	/**
	 * Represents the id of this object.
	 */
	private final int id;

	/**
	 * Represents the type of this object.
	 */
	private final GameObjectType type;

	/**
	 * Represents the orientation of this object.
	 */
	private final GameObjectOrientation orientation;

	/**
	 * Constructs a new {@link GameObject} with the specified id and position.
	 * This game object has a default orientation of north and a default type of
	 * general prop.
	 *
	 * @param id The id of this game object.
	 * @param position The position of this game object.
	 * @param world The world this game object is in.
	 */
	public GameObject(int id, Position position, World world) {
		this(id, position, world, NORTH);
	}

	/**
	 * Constructs a new {@link GameObject} with the specified id, position and
	 * orientation. this game object has a default type of general prop.
	 *
	 * @param id The id of this game object.
	 * @param position The position of this game object.
	 * @param world The world this game object is in.
	 * @param orientation The orientation of this object.
	 */
	public GameObject(int id, Position position, World world, GameObjectOrientation orientation) {
		this(id, position, world, GameObjectType.GENERAL_PROP, orientation);
	}

	/**
	 * Constructs a new {@link GameObject} with the specified id, position,
	 * orientation and type.
	 *
	 * @param id The id of this game object.
	 * @param position The position of this game object.
	 * @param world The world this game object is in.
	 * @param type The type of this object.
	 * @param orientation The orientation of this object.
	 */
	public GameObject(int id, Position position, World world, GameObjectType type, GameObjectOrientation orientation) {
		super(position, world);
		this.id = id;
		this.type = type;
		this.orientation = orientation;
	}

	/**
	 * Decodes and returns the id of this object.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the type of this object.
	 */
	public GameObjectType getType() {
		return type;
	}

	/**
	 * Returns the orientation of this object.
	 */
	public GameObjectOrientation getOrientation() {
		return orientation;
	}

	/**
	 * Returns the definition of this object.
	 */
	public GameObjectDefinition getDefinition() {
		return GameObjectDefinition.forId(id);
	}

	/**
	 * Returns the amount of between this game objects position and the
	 * specified position.
	 *
	 * @param position The starting position.
	 * @return The amount of between this game objects position and the
	 *         specified position.
	 */
	public int getTileOffset(Position position) {
		if (getSize() <= 1) {
			return 1;
		}

		GameObjectDefinition def = getDefinition();
		int distanceX = Math.abs(position.getX() - getPosition().getX());
		int distanceY = Math.abs(position.getY() - getPosition().getY());
		int total = distanceX > distanceY ? def.getWidth() : def.getLength();
		return total;
	}

	/**
	 * Calculates the center position of this object.
	 *
	 * @return The center position of this object.
	 */
	public Position getCenterPosition() {
		GameObjectDefinition def = getDefinition();
		int width = def.getWidth();
		int length = def.getLength();
		if (orientation == NORTH || orientation == SOUTH) {
			width = def.getLength();
			length = def.getWidth();
		}
		int centerX = getPosition().getX() + width / 2;
		int centerY = getPosition().getY() + length / 2;
		return new Position(centerX, centerY);
	}

	/**
	 * Calculates the turn to position from the specified position for this game
	 * object.
	 *
	 * @param from The from position.
	 * @return The position to turn to.
	 */
	public Position getTurnToPosition(Position from) {
		GameObjectDefinition def = getDefinition();

		int width = def.getWidth();
		int length = def.getLength();
		if (orientation == NORTH || orientation == SOUTH) {
			width = def.getLength();
			length = def.getWidth();
		}

		int turnToX = from.getX();
		int turnToY = from.getY();

		Position position = getPosition();

		/* Within the width of the object */
		if (from.getX() >= position.getX() && from.getX() < position.getX() + width) {
			turnToY = position.getY();
		}

		/* Within the length of the object */
		if (from.getY() >= position.getY() && from.getY() < position.getY() + width) {
			turnToX = position.getX();
		}

		/* Upper left corner */
		if (from.getX() < position.getX() && from.getY() >= position.getY() + length) {
			turnToX = position.getX();
			turnToY = position.getY() + length - 1;
		}

		/* Upper right corner */
		if (from.getX() >= position.getX() + width && from.getY() >= position.getY() + length) {
			turnToX = position.getX() + width - 1;
			turnToY = position.getY() + length - 1;
		}

		/* Lower left corner */
		if (from.getX() < position.getX() + width && from.getY() < position.getY()) {
			turnToX = position.getX();
			turnToY = position.getY();
		}

		/* Lower right corner */
		if (from.getX() >= position.getX() + width && from.getY() < position.getY()) {
			turnToX = position.getX() + width - 1;
			turnToY = position.getY();
		}

		return new Position(turnToX, turnToY);
	}

	@Override
	public int hashCode() {
		return type.getId() << 2 | orientation.getId() & 0x3F;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GameObject) {
			GameObject other = (GameObject) obj;
			return other.position.equals(position) && other.id == id && other.orientation == orientation && other.type == type;
		}

		return false;
	}

	@Override
	public int getSize() {
		return getDefinition().getSize();
	}

	@Override
	public EntityCategory getCategory() {
		return EntityCategory.GAME_OBJECT;
	}

}