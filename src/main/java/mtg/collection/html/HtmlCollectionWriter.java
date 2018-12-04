package mtg.collection.html;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;

import mtg.collection.collection.CollectionController;
import mtg.collection.collection.CollectionEntry;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class HtmlCollectionWriter {

	public static void write() {
		ArrayList<CollectionEntry> list = new ArrayList<CollectionEntry>(
				CollectionController.collectionMap.values());

		sortList(list);
		writeCollection(list, "html/base_collection.html", "docs/collection.html");
	}

	private static void sortList(ArrayList<CollectionEntry> list) {
		Collections.sort(list, new Comparator<CollectionEntry>() {
			@Override
			public int compare(CollectionEntry arg0, CollectionEntry arg1) {
				final MagicCard card0 = EditionsController.getInstance().getCard(arg0.enName, arg0.edition);
				final MagicCard card1 = EditionsController.getInstance().getCard(arg1.enName, arg1.edition);
				int compareValue = Float.valueOf(card0.getPrice()).compareTo(Float.valueOf(card1.getPrice())) * -1;
				if (compareValue == 0) {
					return card0.getEnName().compareTo(card1.getEnName());
				}

				return compareValue;
			}
		});
	}

	private static void writeCollection(ArrayList<CollectionEntry> list, String baseHtml, String targetHtml) {
		final String tabbedTd = new String("\t\t\t\t\t\t<td>");
		final String tdEnd = new String("</td>\n");
		final StringBuilder sb = new StringBuilder();
		list.forEach(entry -> {
			sb.append("\t\t\t\t\t<tr>\n");

			final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);			
			sb.append(tabbedTd).append(card.getEnName()).append(tdEnd);
			sb.append(tabbedTd).append(card.getPtName()).append(tdEnd);
			sb.append(tabbedTd).append(card.getEdition()).append(tdEnd);
			sb.append(tabbedTd).append(card.getPrice()).append(tdEnd);
			sb.append(tabbedTd).append(CollectionController.getQuantity(card)).append(tdEnd);

			sb.append("\t\t\t\t\t</tr>\n");
		});

		writeHtmlFile(baseHtml, targetHtml, sb);
	}

	private static void writeHtmlFile(String baseHtml, String targetHtml, final StringBuilder sb) {
		try {
			String html = FileUtils.readFileToString(new File(baseHtml), Charset.forName("UTF-8"));
			FileUtils.writeStringToFile(new File(targetHtml), html.replace("@CONTENT", sb.toString()),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
