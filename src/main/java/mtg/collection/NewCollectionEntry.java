package mtg.collection;

public class NewCollectionEntry {
	
	public String quantity = "0";
	public String enName;
	public String edition;
	
	@Override
	public String toString() {
		return enName + edition;
	}

}
