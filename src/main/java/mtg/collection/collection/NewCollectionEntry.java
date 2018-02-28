package mtg.collection.collection;

public class NewCollectionEntry {
	
	public String quantity;
	public String enName;
	public String edition;
	
	public NewCollectionEntry() {
	}
	
	public NewCollectionEntry(final String quantity, final String enName, final String edition) {
		this.quantity = quantity;
		this.enName = enName;
		this.edition = edition;
	}
	
	@Override
	public String toString() {
		return enName + edition;
	}

}
