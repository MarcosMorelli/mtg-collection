package mtg.collection.editions;

public class MagicCardKey {

	private final String enName;
	private final String edition;

	public MagicCardKey(final String enName, final String edition) {
		this.enName = enName;
		this.edition = edition;
	}

	public String getEnName() {
		return enName;
	}

	public String getEdition() {
		return edition;
	}

	@Override
	public boolean equals(final Object arg0) {
		final MagicCardKey anotherKey = (MagicCardKey) arg0;
		return enName.equals(anotherKey.getEnName()) && edition.equals(anotherKey.getEdition());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + enName.hashCode() + edition.hashCode();
		return result;
	}

}
