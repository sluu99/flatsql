package flatsql;

import java.util.Random;

final class SimpleIdGenerator implements IdGenerator {

	private final char[] CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j',
			'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private final int LENGTH = CHARS.length;

	private Random rand = null;

	public SimpleIdGenerator() {
		rand = new Random();
	}

	/**
	 * Returns an ID of the specified length
	 * 
	 * @param length
	 *            A length of 2 or greater
	 * @return a new ID
	 */
	private String newId(int length) {
		if (length < 2)
			length = 2;

		char[] id = new char[length];

		for (int i = 0; i < length; i++) {
			id[i] = CHARS[rand.nextInt(LENGTH)];
		}

		return String.valueOf(id);
	}

	// =================================================================================
	// IdGenerator implementation
	// =================================================================================

	@Override
	public String newId() {
		return newId(5);
	}

	@Override
	public String newId(String mustBeDiff) {
		if (mustBeDiff == null || mustBeDiff.isEmpty()) {
			return newId();
		} else {
			return newId(mustBeDiff.length() + 1);
		}
	}

}
