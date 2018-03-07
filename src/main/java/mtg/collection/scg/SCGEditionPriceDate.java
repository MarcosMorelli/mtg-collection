package mtg.collection.scg;

public class SCGEditionPriceDate {

	public String editionName;
	public Long time;

	@Override
	public boolean equals(Object arg0) {
		SCGEditionPriceDate anotherPrice = (SCGEditionPriceDate) arg0;
		return editionName.equals(anotherPrice.editionName);
	}

	@Override
	public int hashCode() {
		return editionName.hashCode();
	}

}
