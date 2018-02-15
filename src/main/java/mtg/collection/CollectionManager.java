package mtg.collection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CollectionManager {

	public static final HashMap<String, CollectionEntry> collectionMap = new HashMap<String, CollectionEntry>();
	public static final HashMap<String, NewCollectionEntry> newCollectionMap = new HashMap<String, NewCollectionEntry>();

	private static final File COLLECTION_FILE = new File("collection.json");
	private static final File NEW_COLLECTION_FILE = new File("newCollection.json");

	public static void migrateCollection()
			throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final CollectionEntry[] collection = mapper.readValue(
				new ByteArrayInputStream(
						FileUtils.readFileToString(COLLECTION_FILE, Charset.defaultCharset()).getBytes("UTF-8")),
				CollectionEntry[].class);

		for (final CollectionEntry entry : collection) {
			final NewCollectionEntry nce = new NewCollectionEntry();
			nce.quantity = entry.quantity;
			nce.enName = entry.enName;
			nce.edition = entry.edition;
			
			newCollectionMap.put(nce.toString(), nce);
		}
	}

	public static void readCollection() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			CollectionEntry[] collection = mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(COLLECTION_FILE, Charset.defaultCharset()).getBytes("UTF-8")),
					CollectionEntry[].class);

			for (CollectionEntry entry : collection) {
				collectionMap.put(entry.toString(), entry);
			}
		} catch (FileNotFoundException ignored) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeCollection() {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionMap.values());
			FileUtils.write(COLLECTION_FILE, json, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeCollection2() {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newCollectionMap.values());
			FileUtils.write(NEW_COLLECTION_FILE, json, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addCard(CollectionEntry entry) {
		if (collectionMap.containsKey(entry.toString())) {
			int actualQuantity = Integer.parseInt(collectionMap.get(entry.toString()).quantity);
			if (actualQuantity < 4) {
				collectionMap.get(entry.toString()).quantity = "" + ++actualQuantity;
			}
			return;
		}

		collectionMap.put(entry.toString(), entry);
	}

	public static void removeCard(final CollectionEntry entry) {
		if (collectionMap.containsKey(entry.toString())) {
			int actualQuantity = Integer.parseInt(collectionMap.get(entry.toString()).quantity);
			if (actualQuantity > 0) {
				collectionMap.get(entry.toString()).quantity = "" + --actualQuantity;
			}
		}
	}

	public static String getQuantity(final String enName) {
		Iterator<String> iter = collectionMap.keySet().iterator();
		int quantity = 0;
		while (iter.hasNext()) {
			String key = iter.next();
			if (enName.contains("FOIL")) {
				if (key.startsWith(enName) && key.contains("FOIL")) {
					quantity += Integer.parseInt(collectionMap.get(key).quantity);
				}
			} else if (key.startsWith(enName) && !key.contains("FOIL")) {
				quantity += Integer.parseInt(collectionMap.get(key).quantity);
			}
		}

		return "" + quantity;
	}

	public static String getQuantity(final String enName, final String edition) {
		return collectionMap.containsKey(enName + edition) ? collectionMap.get(enName + edition).quantity : "0";
	}

	public static void setScgValue(final String enName, final String edition, String value) {
		if (!getQuantity(enName, edition).equals("0")) {
			collectionMap.get(enName + edition).value = value;
		}
	}

	public static String getQuantity(final MagicCard card) {
		if (!newCollectionMap.containsKey(card.toString())) {
			System.out.println("nao encontrou " + card.toString());
			return "0";
		}
		
		return newCollectionMap.get(card.toString()).quantity;
	}

}
