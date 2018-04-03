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

	public static void write() throws IOException {
		final StringBuilder index = new StringBuilder();
		final StringBuilder mobile = new StringBuilder();
		ArrayList<NewCollectionEntry> list = new ArrayList<NewCollectionEntry>(
				CollectionController.newCollectionMap.values());

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

		list.forEach(entry -> {
			index.append("<article class=\"row ");
			mobile.append("<article class=\"row ");

			final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);
			switch (card.getRarity()) {
			case "Rare":
				index.append("mlb");
				mobile.append("mlb");
				break;
			case "Mythic Rare":
				index.append("pga");
				mobile.append("pga");
				break;
			case "Promotional":
				index.append("nfl");
				mobile.append("nfl");
				break;
			default:
				index.append("nhl");
				mobile.append("nhl");
				break;
			}
			index.append("\">").append("<ul>\n");
			mobile.append("\">").append("<ul>\n");

			index.append("<li><a name=\"card\" href=\"#\">").append(card.getEnName()).append("</a></li>\n");
			mobile.append("<li><a href=\"#\">").append(card.getEnName()).append("</a></li>\n");

			index.append("<li>").append(card.getPtName()).append("</li>\n");
			index.append("<li>");
			switch (card.getRarity()) {
			case "Promotional":
				index.append("P");
				break;
			case "Mythic Rare":
				index.append("M");
				break;
			case "Rare":
				index.append("R");
				break;
			case "Uncommon":
				index.append("U");
				break;
			default:
				index.append("C");
				break;
			}
			index.append("</li>\n");

			index.append("<li>").append(card.getEdition()).append("</li>\n");
			mobile.append("<li>").append(card.getEdition()).append("</li>\n");

			index.append("<li>").append(card.getPrice()).append("</li>\n");

			index.append("<li>").append(CollectionController.getQuantity(card)).append("</li>\n");
			mobile.append("<li>").append(CollectionController.getQuantity(card)).append("</li>\n");

			index.append("</ul>").append("</article>\n");
			mobile.append("</ul>").append("</article>\n");
		});

		String baseIndex = FileUtils.readFileToString(new File("html/base_collection.html"), Charset.forName("UTF-8"));
		FileUtils.writeStringToFile(new File("docs/index.html"), baseIndex.replace("@CONTENT", index.toString()),
				Charset.forName("UTF-8"));

		String baseMobile = FileUtils.readFileToString(new File("html/base_mobile.html"), Charset.forName("UTF-8"));
		FileUtils.writeStringToFile(new File("docs/mobile.html"), baseMobile.replace("@CONTENT", mobile.toString()),
				Charset.forName("UTF-8"));
	}

}
