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
import mtg.collection.editions.Edition;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class HtmlCollectionWriter {
	
	private final static String td = new String("<td>");
	private final static String tdEnd = new String("</td>\n");
	private final static String tr = new String("<tr>\n");
	private final static String trEnd = new String("</tr>\n");
	
	private static int count;

	public static void write() {
		final ArrayList<CollectionEntry> list = CollectionController.getIndividualEntrys();
		sortList(list);
		writeCollection(list, "html/base_collection.html", "docs/collection.html");
		writeStatistics("html/base_statistics.html", "docs/statistics.html");
		writeEditions("html/base_editions.html", "docs/editions/@.html");
	}

	private static void sortList(ArrayList<CollectionEntry> list) {
		Collections.sort(list, new Comparator<CollectionEntry>() {
			@Override
			public int compare(final CollectionEntry arg0, final CollectionEntry arg1) {
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
		final StringBuilder sb = new StringBuilder();
		list.forEach(entry -> {
			sb.append(tr);

			final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);
			sb.append(td).append(card.getEnName()).append(tdEnd);
			sb.append(td).append(card.getPtName()).append(tdEnd);
			sb.append(td).append(card.getEdition()).append(tdEnd);
			sb.append(td).append(card.getPrice()).append(tdEnd);
			sb.append(td).append(CollectionController.getQuantityConsiderEdition(card)).append(tdEnd);

			sb.append(trEnd);
		});

		writeHtmlFile(baseHtml, targetHtml, sb, "@CONTENT");
	}

	private static void writeStatistics(String baseHtml, String targetHtml) {
		final StringBuilder sb = new StringBuilder();

		ArrayList<Edition> list = new ArrayList<>(EditionsController.getInstance().getEditionsList().values());
		Collections.sort(list, new Comparator<Edition>() {
			@Override
			public int compare(final Edition arg0, final Edition arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});
		
		list.forEach(edition -> {
			sb.append(tr);
			sb.append(td).append("<a href=\"editions\\").append(edition.getEditions().toString()).append(".html\">")
				.append(edition.getName()).append(tdEnd);
			sb.append(td).append(edition.getCountOfDifferentCards()).append("/")
					.append(edition.getTotalOfDifferentCards()).append(tdEnd);
			sb.append(td).append("0/0").append(tdEnd);
			sb.append(trEnd);
		});

		writeHtmlFile(baseHtml, targetHtml, sb, "@SUMMARY_CONTENT");
	}
	
	private static void writeEditions(String baseHtml, String targetHtml) {
		final String hoverInit = new String("<div class=\"hover_img\"><a href=\"#\">");
		final String hoverSource = new String("<span><img src=\"");
		final String hoverEnd = new String("\"/></span></a></div>");
		
		EditionsController.getInstance().getEditionsList().values().forEach(edition -> {
			count = 1;
			final StringBuilder sb = new StringBuilder();
			
			edition.getSortedMissingSingles().forEach(missingName -> {
				final MagicCard card = EditionsController.getInstance().getCard(missingName, edition.getName());
				if (card == null) {
					return;
				}
				sb.append(tr);
				sb.append(td).append(count++).append(tdEnd);
				sb.append(td).append(hoverInit).append(missingName).append(hoverSource)
					.append(card.getCardImageHRef()).append(hoverEnd).append(tdEnd);
				sb.append(trEnd);
			});
			
			writeHtmlFile(baseHtml, targetHtml.replace("@", edition.getEditions().toString()), sb, "@SUMMARY_CONTENT");
		});		
	}

	private static void writeHtmlFile(String baseHtml, String targetHtml, final StringBuilder sb,
			final String replaceHolder) {
		try {
			String html = FileUtils.readFileToString(new File(baseHtml), Charset.forName("UTF-8"));
			FileUtils.writeStringToFile(new File(targetHtml), html.replace(replaceHolder, sb.toString()),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
