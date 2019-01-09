package mtg.collection.editions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

	private HashMap<MagicCard, Integer> cardsMap = new HashMap<>();
	private HashSet<String> singles = new HashSet<>();
	private HashSet<String> ownedSingles = new HashSet<>();
	private HashSet<String> missingSingles = new HashSet<>();
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

	public int getTotalOfDifferentCards() {
		return singles.size();
	}

	public int getCountOfDifferentCards() {
		return ownedSingles.size();
	}
	
	public int getCountOfOwnedCards() {
		return countOfOwnedCards;
	}

	public ArrayList<String> getSortedMissingSingles() {
		ArrayList<String> sortedList = new ArrayList<>(missingSingles);
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
					singles.add(card.getEnNameWithoutFoil());
					countOfOwnedCards += quantity;
				}
				
				cardsMap.put(card, quantity);
				
				if (cardsMap.get(card) > 0) {
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
