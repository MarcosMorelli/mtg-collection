package mtg.collection.editions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.collection.CollectionController;

public class Edition {

	private Editions edition;
	private String name;

	private HashMap<MagicCard, Integer> cardsQuantityMap = new HashMap<>();
	private HashMap<String, MagicCard> cards = new HashMap<String, MagicCard>();
	private HashSet<String> singles = new HashSet<String>();
	private HashSet<String> ownedSingles = new HashSet<String>();
	private HashSet<String> missingSingles = new HashSet<String>();
	private int countOfOwnedCards;

	public Edition(final Editions edition) {
		this.edition = edition;
		this.name = edition.getName();

		readEdition();
	}

	public String getName() {
		return name;
	}
	
	public Editions getEditions() {
		return edition;
	}
	
	public HashMap<String, MagicCard> getCards() {
		return cards;
	}
	
	public HashMap<MagicCard, Integer> getCardsQuantityMap() {
		return cardsQuantityMap;
	}
	
	public String getHtmlFileName() {
		return edition.toString().replaceAll("_", "");
	}

	public int getTotalOfDifferentCards() {
		return singles.size();
	}

	public int getCountOfDifferentCards() {
		return ownedSingles.size();
	}
	
	public BigDecimal getPercentualOfDifferentCards() {
		final float x = getCountOfDifferentCards();
		final float y = getTotalOfDifferentCards();
		final float z = (x / y) * 100;
		try {
			final BigDecimal bd = new BigDecimal(Float.toString(z));
			return bd.setScale(2, RoundingMode.HALF_UP);
		} catch (NumberFormatException e) {
			return new BigDecimal(0);
		}		
	}
	
	public long getCountOfOwnedCards() {
		return countOfOwnedCards;
	}
	
	public BigDecimal getPercentualOfOwnedCards() {
		final float x = getCountOfOwnedCards();
		final float y = getTotalOfDifferentCards() * 4;
		final float z = (x / y) * 100;
		try {
			final BigDecimal bd = new BigDecimal(Float.toString(z));
			return bd.setScale(2, RoundingMode.HALF_UP);
		} catch (NumberFormatException e) {
			return new BigDecimal(0);
		}		
	}

	public ArrayList<String> getSortedMissingSingles() {
		final ArrayList<String> sortedList = new ArrayList<>(missingSingles);
		Collections.sort(sortedList);
		return sortedList;
	}

	private void readEdition() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final MagicCard[] editionCards = mapper.readValue(new ByteArrayInputStream(FileUtils
					.readFileToString(new File(edition.getFileName()), Charset.defaultCharset()).getBytes("UTF-8")),
					MagicCard[].class);

			for (int i = 0; i < editionCards.length; i++) {
				final MagicCard card = editionCards[i];
				final int quantity = CollectionController.getQuantity(card.getEnName(), false);
				
				if (!singles.contains(card.getEnNameWithoutFoil())) {
					cards.put(card.getEnNameWithoutFoil(), card);
					
					singles.add(card.getEnNameWithoutFoil());
					countOfOwnedCards += quantity;
				}
				
				cardsQuantityMap.put(card, quantity);
				
				if (cardsQuantityMap.get(card) > 0) {
					ownedSingles.add(card.getEnNameWithoutFoil());
				} else if (!missingSingles.contains(card.getEnNameWithoutFoil())) {
					missingSingles.add(card.getEnName());
				}
			}
		} catch (final FileNotFoundException ignored) {
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
