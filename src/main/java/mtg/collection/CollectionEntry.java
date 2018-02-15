package mtg.collection;

public class CollectionEntry {

	public String quantity = "1";
	public String value = "0.00";

	public String enName;
	public String ptName;
	public String edition;
	public String rarity;

	public CollectionEntry() {
	}

	public CollectionEntry(final String enName, final String ptName, final String edition, final String rarity) {
		this.enName = enName;
		this.ptName = ptName;
		this.edition = edition;
		this.rarity = rarity;
	}

	public int getQuantity() {
		return Integer.parseInt(quantity);
	}
	
	@Override
	public String toString() {
		return enName + edition;
	}
	
}
