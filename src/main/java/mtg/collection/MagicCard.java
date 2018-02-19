package mtg.collection;

import java.util.List;

public class MagicCard implements Comparable<MagicCard> {

	private static final String FOIL_STRING = " (FOIL)";

	private String number;
	private String enName;
	private String ptName;
	private String type;
	private String mana;
	private String rarity;
	private String artist;
	private String edition;

	private boolean foil;
	private String magicCardInfoLink;

	private float price;

	public MagicCard() {
	}

	public MagicCard(final List<String> cardInfos) {
		int i = 0;
		setNumber(cardInfos.get(i++));
		setEnName(cardInfos.get(i++));
		setMagicCardInfoLink(cardInfos.get(i++));
		setType(cardInfos.get(i++));
		setMana(cardInfos.get(i++));
		setRarity(cardInfos.get(i++));
		setArtist(cardInfos.get(i++));
		setEdition(cardInfos.get(i++));
	}

	public MagicCard(final List<String> cardInfos, final boolean foilVersion) {
		this(cardInfos);
		setFoil(foilVersion);
	}

	@Override
	public String toString() {
		return enName + edition;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(final String number) {
		this.number = number;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(final String enName) {
		if (isFoil() && !enName.contains(FOIL_STRING)) {
			this.enName = enName + FOIL_STRING;
		}
		this.enName = enName;
	}

	public String getPtName() {
		if (ptName == null || ptName.isEmpty()) {
			return "";
		}

		return ptName;
	}

	public void setPtName(final String ptName) {
		if (ptName == null) {
			this.ptName = "";
			return;
		} else if (isFoil() && !ptName.contains(FOIL_STRING)) {
			this.ptName = removeSpecialChars(ptName) + FOIL_STRING;
		}
		this.ptName = removeSpecialChars(ptName);
	}

	private String removeSpecialChars(final String ptName) {
		return ptName.replaceAll("[àáãäâå]", "a").replaceAll("[èéẽëê]", "e").replaceAll("[ìíĩïî]", "i")
				.replaceAll("[òóõöô]", "o").replaceAll("[ùúüũû]", "u").replaceAll("ç", "c").replaceAll("[ÀÁÄÂÃÅ]", "A")
				.replaceAll("[ÈÉËÊẼ]", "E").replaceAll("[ÌÍÏĨÎ]", "I").replaceAll("[ÒÓÕÖÔ]", "O")
				.replaceAll("[ÙÚÜŨÛ]", "U").replaceAll("Ae", "Ae");
	}
	
	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getMana() {
		return mana;
	}

	public void setMana(final String mana) {
		this.mana = mana;
	}

	public String getRarity() {
		if (rarity == null) {
			return "";
		}
		return rarity;
	}

	public void setRarity(final String rarity) {
		this.rarity = rarity;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(final String artist) {
		this.artist = removeSpecialChars(artist);
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(final String edition) {
		this.edition = edition;
	}

	public boolean isFoil() {
		return foil;
	}

	public void setFoil(final boolean foil) {
		this.foil = foil;
	}

	public String getMagicCardInfoLink() {
		return magicCardInfoLink;
	}

	public void setMagicCardInfoLink(final String magicCardInfoLink) {
		this.magicCardInfoLink = magicCardInfoLink;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(final float price) {
		this.price = price;
	}

	@Override
	public int compareTo(final MagicCard card) {
		return this.getEnName().compareTo(card.getEnName());
	}

}
