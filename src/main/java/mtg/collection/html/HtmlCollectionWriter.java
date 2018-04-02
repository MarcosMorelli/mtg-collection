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
		final StringBuilder builder = new StringBuilder();
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
			builder.append("<article class=\"row ");

			final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);
			switch (card.getRarity()) {
			case "Rare":
				builder.append("mlb");
				break;
			case "Mythic Rare":
				builder.append("pga");
				break;
			case "Promotional":
				builder.append("nfl");
				break;
			default:
				builder.append("nhl");
				break;
			}
			builder.append("\">").append("<ul>\n");

			builder.append("<li><a href=\"#\">").append(card.getEnName())
					.append("</a></li>\n");
			builder.append("<li>").append(card.getPtName()).append("</li>\n");
			builder.append("<li>");
			switch (card.getRarity()) {
			case "Promotional":
				builder.append("P");
				break;
			case "Mythic Rare":
				builder.append("M");
				break;
			case "Rare":
				builder.append("R");
				break;
			case "Uncommon":
				builder.append("U");
				break;
			default:
				builder.append("C");
				break;
			}			
			builder.append("</li>\n");
			builder.append("<li>").append(card.getEdition()).append("</li>\n");
			builder.append("<li>").append(card.getPrice()).append("</li>\n");
			builder.append("<li>").append(CollectionController.getQuantity(card)).append("</li>\n");
			builder.append("</ul>").append("</article>\n");
		});

		String base = FileUtils.readFileToString(new File("html/base_collection.html"), Charset.forName("UTF-8"));
		FileUtils.writeStringToFile(new File("docs/index.html"), base.replace("@CONTENT", builder.toString()),
				Charset.forName("UTF-8"));
	}

}
