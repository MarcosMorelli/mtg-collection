package mtg.collection.editions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.collection.CollectionController;

public class Edition {

	private Editions edition;
	private String name;

	private HashMap<MagicCard, Integer> cardsMap = new HashMap<>();
	private HashSet<String> ownedSingles = new HashSet<>();
	private HashSet<String> missingSingles = new HashSet<>();

	private int totalOfDifferentCards;

	public Edition(final Editions edition) {
		this.edition = edition;
		this.name = edition.getName();

		readEdition();
	}

	public String getName() {
		return name;
	}

	public int getTotalOfDifferentCards() {
		return totalOfDifferentCards;
	}

	public int getCountOfDifferentCards() {
		return ownedSingles.size();
	}

	public HashSet<String> getMissingSinglesSet() {
		return missingSingles;
	}

	private void readEdition() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final MagicCard[] editionCards = mapper.readValue(new ByteArrayInputStream(FileUtils
					.readFileToString(new File(edition.getFileName()), Charset.defaultCharset()).getBytes("UTF-8")),
					MagicCard[].class);

			for (int i = 0; i < editionCards.length; i++) {
				MagicCard card = editionCards[i];

				if (!card.getEnName().contains(MagicCard.FOIL_STRING)) {
					totalOfDifferentCards++;
				}

				cardsMap.put(card, CollectionController.getQuantity(card.getEnName(), true));
				if (cardsMap.get(card) > 0) {
					ownedSingles.add(card.getEnNameWithoutFoil());
				} else if (CollectionController.getQuantity(card.getEnName(), false) == 0) {
					missingSingles.add(card.getEnNameWithoutFoil());
				}
			}
		} catch (final FileNotFoundException ignored) {
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
