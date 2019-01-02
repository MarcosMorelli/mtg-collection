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

	public static final HashMap<String, ArrayList<CollectionEntry>> collectionMap = new HashMap<String, ArrayList<CollectionEntry>>();

	public static void readCollection() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final CollectionEntry[] collection = mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(COLLECTION_FILE, Charset.defaultCharset()).getBytes("UTF-8")),
					CollectionEntry[].class);

			for (final CollectionEntry entry : collection) {
				final String key = entry.enName.replace(MagicCard.FOIL_STRING, "");
				if (collectionMap.containsKey(key)) {
					collectionMap.get(key).add(entry);
				} else {
					ArrayList<CollectionEntry> list = new ArrayList<>(Arrays.asList(entry));
					collectionMap.put(key, list);
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
			final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getIndividualEntrys());
			FileUtils.write(COLLECTION_FILE, json, Charset.defaultCharset());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<CollectionEntry> getIndividualEntrys() {
		final ArrayList<CollectionEntry> list = new ArrayList<CollectionEntry>();
		CollectionController.collectionMap.values().forEach(entrysLists -> {
			entrysLists.forEach(entry -> {
				list.add(entry);
			});
		});
		
		return list;
	}
	
	public static void addCard(final CollectionEntry entry) {
		final String key = entry.enName.replace(MagicCard.FOIL_STRING, "");
		if (!collectionMap.containsKey(key)) {
			ArrayList<CollectionEntry> list = new ArrayList<>(Arrays.asList(entry));
			collectionMap.put(key, list);
		}

		final ArrayList<CollectionEntry> entrysList = collectionMap.get(key);
		for (int i = 0; i < entrysList.size(); i++) {
			CollectionEntry listEntry = entrysList.get(i);
			if (listEntry.equals(entry)) {
				int actualQuantity = listEntry.quantity;
				if (actualQuantity < 4) {
					listEntry.quantity = ++actualQuantity;
				}
				return;
			}
		}
		
		entry.quantity = 1;
		entrysList.add(entry);
	}

	public static void removeCard(final CollectionEntry entry) {
		final String key = entry.enName.replace(MagicCard.FOIL_STRING, "");
		if (!collectionMap.containsKey(key)) {
			return;
		}

		ArrayList<CollectionEntry> entrysList = collectionMap.get(key);
		for (int i = 0; i < entrysList.size(); i++) {
			CollectionEntry listEntry = entrysList.get(i);
			if (listEntry.equals(entry)) {
				int actualQuantity = listEntry.quantity;
				actualQuantity--;
				if (actualQuantity == 0) {
					collectionMap.get(key).remove(listEntry);
				} else if (actualQuantity > 0) {
					listEntry.quantity = actualQuantity;
				}
			}
		}
	}

	public static int getQuantity(final String enName, boolean differFoil) {
		final String key = enName.replace(MagicCard.FOIL_STRING, "");
		if (!collectionMap.containsKey(key)) {
			return 0;
		}

		int quantity = 0;
		final String name = differFoil ? enName : key;
		for (CollectionEntry entry : collectionMap.get(key)) {
			String entryName = differFoil ? entry.enName : key;
			if (name.equals(entryName)) {
				quantity += Integer.valueOf(entry.quantity);
			}
		}

		return quantity;
	}

	public static int getQuantityConsiderEdition(final String enName, final String edition) {
		final String key = enName.replace(MagicCard.FOIL_STRING, "");
		if (!collectionMap.containsKey(key)) {
			return 0;
		}

		final CollectionEntry inputEntry = new CollectionEntry(0, enName, edition);
		for (CollectionEntry entry : collectionMap.get(key)) {
			if (inputEntry.equals(entry)) {
				return entry.quantity;
			}
		}

		return 0;
	}

	public static int getQuantityConsiderEdition(final MagicCard card) {
		return getQuantityConsiderEdition(card.getEnName(), card.getEdition());
	}

}
