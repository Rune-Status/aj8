package org.apollo.game.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * A {@link GameCharacterRepository} is a repository of {@link GameCharacter}s
 * that are currently active in the game world.
 *
 * @author Graham
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 * @param <T> The type of game character.
 */
public final class GameCharacterRepository<T extends GameCharacter> extends AbstractCollection<T> implements Iterable<T> {

	/**
	 * The array of game characters in this repository.
	 */
	private final GameCharacter[] characters;

	/**
	 * The current size of this repository.
	 */
	private int size = 0;

	/**
	 * Creates a new game character repository with the specified capacity.
	 *
	 * @param capacity The maximum number of game characters that can be present
	 *            in the repository.
	 */
	public GameCharacterRepository(int capacity) {
		characters = new GameCharacter[capacity];
	}

	@Override
	public int size() {
		return size;
	}

	/**
	 * Gets the capacity of this repository.
	 *
	 * @return The maximum size of this repository.
	 */
	public int capacity() {
		return characters.length;
	}

	@Override
	public boolean add(T character) {
		for (int index = 0; index < characters.length; index++) {
			if (characters[index] != null) {
				continue;
			}

			characters[index] = character;
			character.setIndex(index + 1);
			size++;

			return true;
		}

		return false;
	}

	/**
	 * Attempts to remove the specified game character from this collection.
	 *
	 * @param character The character to try to remove.
	 * @return {@code true} if and only if the specified character was removed
	 *         otherwise {@code false}.
	 */
	public boolean remove(T character) {
		int index = character.getIndex();

		GameCharacter other = get(index);
		assert other == character;

		remove(index);
		return true;
	}

	/**
	 * Looks up a game character at the specified index and attempts to remove
	 * it from this collection.
	 *
	 * @param index The index within this collection to remove the specified
	 *            character.
	 * @return {@code true} if and only if the game character for the specified
	 *         index was removed.
	 */
	public boolean remove(int index) {
		GameCharacter character = get(index);
		if (character == null) {
			return false;
		}

		checkArgument(character.getIndex() != index, "Unexpected index: " + index + ", expected: " + character.getIndex());

		characters[index - 1] = null;
		character.resetIndex();
		size--;
		return true;
	}

	/**
	 * Attempts to get the character at the specified index.
	 *
	 * @param index The index of the character to get.
	 * @return The character at the specified index if and only if it exists
	 *         otherwise {@code null} is returned.
	 */
	@SuppressWarnings("unchecked")
	public T get(int index) {
		checkArgument(index < 1 || index >= characters.length + 1);
		return (T) characters[index - 1];
	}

	@Override
	public Iterator<T> iterator() {
		return new GameCharacterRepositoryIterator();
	}

	/**
	 * The {@link Iterator} implementation for the
	 * {@link GameCharacterRepository} class.
	 *
	 * @author Graham
	 * @author Ryley Kimmel <ryley.kimmel@live.com>
	 */
	private final class GameCharacterRepositoryIterator implements Iterator<T> {

		/**
		 * The current index of this iterator.
		 */
		private int currentIndex;

		/**
		 * The amount of indexes found.
		 */
		private int foundIndex;

		@Override
		public boolean hasNext() {
			if (foundIndex == size) {
				return false;
			}

			while (currentIndex < capacity()) {
				if (characters[currentIndex++] != null) {
					foundIndex++;
					return true;
				}
			}
			return false;
		}

		@Override
		public T next() {
			return get(currentIndex);
		}

		@Override
		public void remove() {
			GameCharacterRepository.this.remove(currentIndex + 1);
		}
	}

}