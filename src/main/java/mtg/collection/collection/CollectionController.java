package mtg.collection.collection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.editions.Editions;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class CollectionController {

	public static final HashMap<String, CollectionEntry> collectionMap = new HashMap<String, CollectionEntry>();
	private static final File COLLECTION_FILE = new File("collection.json");

	public static void readCollection() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final CollectionEntry[] collection = mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(COLLECTION_FILE, Charset.defaultCharset()).getBytes("UTF-8")),
					CollectionEntry[].class);

			for (final CollectionEntry entry : collection) {
				collectionMap.put(entry.toString(), entry);
			}
		} catch (final FileNotFoundException ignored) {
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeCollection() {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionMap.values());
			FileUtils.write(COLLECTION_FILE, json, Charset.defaultCharset());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void addCard(final CollectionEntry entry) {
		if (collectionMap.containsKey(entry.toString())) {
			int actualQuantity = Integer.parseInt(collectionMap.get(entry.toString()).quantity);
			if (actualQuantity < 4) {
				collectionMap.get(entry.toString()).quantity = "" + ++actualQuantity;
			}
			return;
		}

		entry.quantity = "1";
		collectionMap.put(entry.toString(), entry);
	}

	public static void removeCard(final CollectionEntry entry) {
		if (collectionMap.containsKey(entry.toString())) {
			int actualQuantity = Integer.parseInt(collectionMap.get(entry.toString()).quantity);
			actualQuantity--;
			if (actualQuantity == 0) {
				collectionMap.remove(entry.toString());
			} else if (actualQuantity > 0) {
				collectionMap.get(entry.toString()).quantity = "" + actualQuantity;
			}
		}
	}

	public static String getQuantity(final String enName, boolean differFoil) {
		int quantity = 0;

		final String name = differFoil ? enName : enName.replace(" (FOIL)", "");
		for (CollectionEntry entry : collectionMap.values()) {
			String entryName = differFoil ? entry.enName : entry.enName.replace(" (FOIL)", "");
			if (name.equals(entryName)) {
				quantity += Integer.valueOf(entry.quantity);
			}
		}

		return "" + quantity;
	}

	public static String getQuantity(final String enName, final String edition) {
		return collectionMap.containsKey(enName + edition) ? collectionMap.get(enName + edition).quantity : "0";
	}

	public static String getQuantity(final MagicCard card) {
		if (!collectionMap.containsKey(card.toString())) {
			return "0";
		}

		return collectionMap.get(card.toString()).quantity;
	}

	public static void writeHtmlFiles() {
		ArrayList<MagicCard> commons = new ArrayList<>();
		ArrayList<MagicCard> uncommons = new ArrayList<>();
		ArrayList<MagicCard> rares = new ArrayList<>();
		ArrayList<MagicCard> mythics = new ArrayList<>();
		
		Arrays.asList(Editions.values()).forEach(edition -> {
			commons.clear();
			uncommons.clear();
			rares.clear();
			mythics.clear();
			
			EditionsController.getInstance().getEditionCards(edition).forEach(card -> {
				if (card.isFoil()) {
					return;
				}

				int quantity = Integer.parseInt(getQuantity(card.getEnName(), false));
				if (quantity > 3) {
					return;
				}
				
				switch (card.getRarity()) {
				case "Common":
					commons.add(card);
					break;
				case "Uncommon":
					uncommons.add(card);
					break;
				case "Rare":
					rares.add(card);
					break;
				default:
					mythics.add(card);
					break;
				}				
			});
			
			if (commons.isEmpty() && uncommons.isEmpty() && rares.isEmpty() && mythics.isEmpty()) {
				return;
			}
			
			System.out.println("===============================================");
			System.out.println(edition.getName());
			System.out.println("===============================================");
			
			commons.forEach(card -> {
				System.out.println(card.getEnName() + ": " + getQuantity(card.getEnName(), false));
			});
			
			uncommons.forEach(card -> {
				System.out.println(card.getEnName() + ": " + getQuantity(card.getEnName(), false));
			});
			
			rares.forEach(card -> {
				System.out.println(card.getEnName() + ": " + getQuantity(card.getEnName(), false));
			});
			
			mythics.forEach(card -> {
				System.out.println(card.getEnName() + ": " + getQuantity(card.getEnName(), false));
			});
		});
	}

}
