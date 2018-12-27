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

import mtg.collection.editions.MagicCard;

public class CollectionController {

	private static final File COLLECTION_FILE = new File("collection.json");

	public static final HashMap<String, CollectionEntry> collectionMap = new HashMap<String, CollectionEntry>();
	public static final HashMap<String, ArrayList<CollectionEntry>> collectionMap2 = new HashMap<String, ArrayList<CollectionEntry>>();

	public static void readCollection() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final CollectionEntry[] collection = mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(COLLECTION_FILE, Charset.defaultCharset()).getBytes("UTF-8")),
					CollectionEntry[].class);

			for (final CollectionEntry entry : collection) {
				collectionMap.put(entry.toString(), entry);

				final String key = entry.enName.replace(MagicCard.FOIL_STRING, "");
				if (collectionMap2.containsKey(key)) {
					collectionMap2.get(key).add(entry);
				} else {
					ArrayList<CollectionEntry> list = new ArrayList<>(Arrays.asList(entry));
					collectionMap2.put(key, list);
				}
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

	public static int getQuantity(final String enName, boolean differFoil) {
		final String key = enName.replace(MagicCard.FOIL_STRING, "");
		if (!collectionMap2.containsKey(key)) {
			return 0;
		}

		int quantity = 0;
		final String name = differFoil ? enName : key;
		for (CollectionEntry entry : collectionMap2.get(key)) {
			String entryName = differFoil ? entry.enName : key;
			if (name.equals(entryName)) {
				quantity += Integer.valueOf(entry.quantity);
			}
		}

		return quantity;
	}

	public static String getQuantityConsiderEdition(final String enName, final String edition) {
		return collectionMap.containsKey(enName + edition) ? collectionMap.get(enName + edition).quantity : "0";
	}

	public static String getQuantityConsiderEdition(final MagicCard card) {
		if (!collectionMap.containsKey(card.toString())) {
			return "0";
		}

		return collectionMap.get(card.toString()).quantity;
	}

}
