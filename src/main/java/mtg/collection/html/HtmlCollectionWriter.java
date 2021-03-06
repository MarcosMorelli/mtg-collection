package mtg.collection.html;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static void writeFiles() {
		writeCollection("html/base_collection.html", "docs/collection.html");
		writeStatistics("html/base_statistics.html", "docs/statistics.html");
		writeEditions("html/base_editions.html", "docs/editions/@.html");
	}

	private static void sortList(ArrayList<CollectionEntry> list) {
		Collections.sort(list, new Comparator<CollectionEntry>() {
			@Override
			public int compare(final CollectionEntry arg0, final CollectionEntry arg1) {
				final MagicCard card0 = EditionsController.getInstance().getCard(arg0.enName, arg0.edition);
				final MagicCard card1 = EditionsController.getInstance().getCard(arg1.enName, arg1.edition);
				final int compareValue = Float.valueOf(card0.getPrice()).compareTo(Float.valueOf(card1.getPrice()))
						* -1;
				if (compareValue == 0) {
					return card0.getEnName().compareTo(card1.getEnName());
				}

				return compareValue;
			}
		});
	}

	private static void writeCollection(final String baseHtmlPath, final String targetHtmlPath) {
		final StringBuilder sb = new StringBuilder();
		final ArrayList<CollectionEntry> list = CollectionController.getIndividualEntrys();
		//sortList(list);

		list.forEach(entry -> {
			try {
				sb.append(tr);

				final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);
				sb.append(td).append(card.getEnName()).append(tdEnd);
				sb.append(td).append(card.getPtName()).append(tdEnd);
				sb.append(td).append(card.getEdition()).append(tdEnd);
				sb.append(td).append(card.getPrice()).append(tdEnd);
				sb.append(td).append(CollectionController.getQuantityConsiderEdition(card)).append(tdEnd);

				sb.append(trEnd);
			} catch (NullPointerException e) {
				System.err.println(entry.enName);
				System.err.println(entry.edition);
			}
			
		});

		final String baseHtml = readBaseHtmlFile(baseHtmlPath);
		writeHtml(baseHtml.replace("@CONTENT", sb.toString()), targetHtmlPath);
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
			sb.append(td).append("<a href=\"editions\\").append(edition.getHtmlFileName()).append(".html\">")
					.append(edition.getName()).append(tdEnd);
			sb.append(td).append(edition.getCountOfDifferentCards()).append("/")
					.append(edition.getTotalOfDifferentCards()).append(tdEnd);
			sb.append(td).append(edition.getPercentualOfDifferentCards()).append(tdEnd);
			sb.append(td).append(edition.getCountOfOwnedCards()).append("/")
					.append(edition.getTotalOfDifferentCards() * 4).append(tdEnd);
			sb.append(td).append(edition.getPercentualOfOwnedCards()).append(tdEnd);
			sb.append(trEnd);
		});

		writeHtmlFile(baseHtml, targetHtml, sb, "@SUMMARY_CONTENT");
	}

	private static void writeEditions(String baseHtml, String targetHtml) {
		final String missingCardsTable = "<h2 class=\"article-title\">Missing Cards:</h2>"
				+ "<table class=\"table\"><tr><th>#</th><th>Card Name</th></tr>@MISSINGTABLE</table>";

		final String missingPlayset = "<div class=\"col-md-6\">\n"
				+ "<h2 class=\"article-title\">Missing Playset:</h2>";

		final String missingTable = "<table class=\"table\"><tr><th>EN Card Name</th><th>PT Card Name</th><th>Count</th></tr>";

		final String hoverInit = "<div class=\"hover_img\"><a href=\"#\">";
		final String hoverSource = "<span><img src=\"";
		final String hoverEnd = "\"/></span></a></div>";

		EditionsController.getInstance().getEditionsList().values().forEach(edition -> {
			final StringBuilder sb = new StringBuilder(missingPlayset);

			final ArrayList<String> sortedList = new ArrayList<>();
			edition.getCardsQuantityMap().keySet().forEach(card -> {
				final String cardName = card.getEnNameWithoutFoil();
				if (!sortedList.contains(cardName)) {
					sortedList.add(cardName);
				}
			});
			Collections.sort(sortedList);

			Arrays.asList("Mythic Rare", "Rare", "Uncommon", "Common").forEach(rarity -> {
				final StringBuffer b = new StringBuffer();
				sortedList.forEach(cardName -> {
					final MagicCard card = edition.getCards().get(cardName);
					if (card.getRarity().equals(rarity)) {
						final int ownedCopies = edition.getCardsQuantityMap().get(card);
						if (ownedCopies < 4) {
							b.append(tr);
							b.append(td).append(hoverInit).append(card.getEnName()).append(hoverSource)
									.append(card.getCardImageHRef()).append(hoverEnd).append(tdEnd);
							b.append(td).append(hoverInit).append(card.getPtName()).append(hoverSource)
									.append(card.getCardImageHRef()).append(hoverEnd).append(tdEnd);
							b.append(td).append(ownedCopies).append(tdEnd);
							b.append(trEnd);
						}
					}
				});
				if (b.length() > 0) {
					sb.append("<h3>").append(rarity).append("</h3>").append(missingTable).append(b).append("</table>");
				}
			});
			sb.append("</div>\n");

			writeHtmlFile(baseHtml, targetHtml.replace("@", edition.getHtmlFileName()), sb, "@SUMMARY_CONTENT");
		});
	}

	private static String readBaseHtmlFile(final String baseHtmlPath) {
		final StringBuilder sb = new StringBuilder();
		try {
			sb.append(FileUtils.readFileToString(new File(baseHtmlPath), Charset.forName("UTF-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private static void writeHtml(final String html, final String filePath) {
		try {
			FileUtils.writeStringToFile(new File(filePath), html, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
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
