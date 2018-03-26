package mtg.collection.scg;

public class SCGCard {

	public String name = "";
	public String price = "";
	public boolean foil = false;
	
	@Override
	public String toString() {
		return name + (foil ? " (FOIL)" : "");
	}

}
