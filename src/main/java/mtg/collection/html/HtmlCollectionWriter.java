package mtg.collection.html;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;

import mtg.collection.collection.CollectionController;
import mtg.collection.collection.NewCollectionEntry;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class HtmlCollectionWriter {

	public static void write() {
		ArrayList<NewCollectionEntry> list = new ArrayList<NewCollectionEntry>(
				CollectionController.collectionMap.values());

		sortList(list);
		writeIndex(list, "html/base_collection.html", "docs/index.html");
		writeMobile(list, "html/base_mobile.html", "docs/mobile.html");
	}

	private static void sortList(ArrayList<NewCollectionEntry> list) {
		Collections.sort(list, new Comparator<NewCollectionEntry>() {
			@Override
			public int compare(NewCollectionEntry arg0, NewCollectionEntry arg1) {
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

	private static void writeIndex(ArrayList<NewCollectionEntry> list, String baseHtml, String targetHtml) {
		final StringBuilder sb = new StringBuilder();
		list.forEach(entry -> {
			sb.append("<article class=\"row ");

			final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);
			sb.append("mlb");
			sb.append("\">").append("<ul>\n");

			sb.append("<li><a name=\"card\" href=\"#\">").append(card.getEnName()).append("</a></li>\n");
			sb.append("<li>").append(card.getPtName()).append("</li>\n");
			sb.append("<li>").append(card.getEdition()).append("</li>\n");
			sb.append("<li>").append(card.getPrice()).append("</li>\n");
			sb.append("<li>").append(CollectionController.getQuantity(card)).append("</li>\n");

			sb.append("</ul>").append("</article>\n");
		});

		writeHtmlFile(baseHtml, targetHtml, sb);
	}

	private static void writeMobile(ArrayList<NewCollectionEntry> list, String baseHtml, String targetHtml) {
		final StringBuilder sb = new StringBuilder();
		list.forEach(entry -> {
			final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);
			if (card.getPrice() <= Float.valueOf("0.25")) {
				return;
			}

			sb.append("<article class=\"row ");
			switch (card.getRarity()) {
			case "Rare":
				sb.append("mlb");
				break;
			case "Mythic Rare":
				sb.append("pga");
				break;
			case "Promotional":
				sb.append("nfl");
				break;
			default:
				sb.append("nhl");
				break;
			}
			sb.append("\">").append("<ul>\n");

			sb.append("<li><a name=\"card\" href=\"#\">").append(card.getEnName()).append("</a></li>\n");
			sb.append("<li>").append(card.getEdition()).append("</li>\n");
			sb.append("<li>").append(CollectionController.getQuantity(card)).append("</li>\n");

			sb.append("</ul>").append("</article>\n");
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
