package mtg.collection.collection;

public class CollectionEntry {
	
	public int quantity;
	public String enName;
	public String edition;
	
	public CollectionEntry() {
	}
	
	public CollectionEntry(final int quantity, final String enName, final String edition) {
		this.quantity = quantity;
		this.enName = enName;
		this.edition = edition;
	}
	
	@Override
	public String toString() {
		return enName + edition;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

}
