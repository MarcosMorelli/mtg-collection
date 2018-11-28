package mtg.collection.collection;

public class CollectionEntry {
	
	public String quantity;
	public String enName;
	public String edition;
	
	public CollectionEntry() {
	}
	
	public CollectionEntry(final String quantity, final String enName, final String edition) {
		this.quantity = quantity;
		this.enName = enName;
		this.edition = edition;
	}
	
	@Override
	public String toString() {
		return enName + edition;
	}

}
