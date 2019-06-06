package mtg.collection.scg;

import mtg.collection.editions.MagicCard;

public class SCGCard {

	public String name = "";
	public String price = "";
	public boolean foil = false;
	
	@Override
	public String toString() {
		return name + (foil ? MagicCard.FOIL_STRING : "");
	}

}
