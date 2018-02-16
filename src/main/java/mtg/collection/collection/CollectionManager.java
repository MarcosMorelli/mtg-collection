package mtg.collection.collection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.MagicCard;

public class CollectionManager {

	public static final HashMap<String, NewCollectionEntry> newCollectionMap = new HashMap<String, NewCollectionEntry>();
	private static final File NEW_COLLECTION_FILE = new File("newCollection.json");
	
	public static void readCollection() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			NewCollectionEntry[] collection = mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(NEW_COLLECTION_FILE, Charset.defaultCharset()).getBytes("UTF-8")),
					NewCollectionEntry[].class);

			for (NewCollectionEntry entry : collection) {
				newCollectionMap.put(entry.toString(), entry);
			}
		} catch (FileNotFoundException ignored) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeCollection() {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newCollectionMap.values());
			FileUtils.write(NEW_COLLECTION_FILE, json, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addCard(final NewCollectionEntry entry) {
		if (newCollectionMap.containsKey(entry.toString())) {
			int actualQuantity = Integer.parseInt(newCollectionMap.get(entry.toString()).quantity);
			if (actualQuantity < 4) {
				newCollectionMap.get(entry.toString()).quantity = "" + ++actualQuantity;
			}
			return;
		}

		newCollectionMap.put(entry.toString(), entry);
	}

	public static void removeCard(final NewCollectionEntry entry) {
		if (newCollectionMap.containsKey(entry.toString())) {
			int actualQuantity = Integer.parseInt(newCollectionMap.get(entry.toString()).quantity);
			if (actualQuantity > 0) {
				newCollectionMap.get(entry.toString()).quantity = "" + --actualQuantity;
			}
		}
	}

	public static String getQuantity(final String enName) {
		Iterator<String> iter = newCollectionMap.keySet().iterator();
		int quantity = 0;
		while (iter.hasNext()) {
			String key = iter.next();
			if (enName.contains("FOIL")) {
				if (key.startsWith(enName) && key.contains("FOIL")) {
					quantity += Integer.parseInt(newCollectionMap.get(key).quantity);
				}
			} else if (key.startsWith(enName) && !key.contains("FOIL")) {
				quantity += Integer.parseInt(newCollectionMap.get(key).quantity);
			}
		}

		return "" + quantity;
	}

	public static String getQuantity(final String enName, final String edition) {
		return newCollectionMap.containsKey(enName + edition) ? newCollectionMap.get(enName + edition).quantity : "0";
	}

	public static String getQuantity(final MagicCard card) {
		if (!newCollectionMap.containsKey(card.toString())) {
			return "0";
		}
		
		return newCollectionMap.get(card.toString()).quantity;
	}

}
