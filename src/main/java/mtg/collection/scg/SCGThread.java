package mtg.collection.scg;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import mtg.collection.chrome.NetworkEnableMessage;
import mtg.collection.chrome.SetBlockedUrlsMessage;
import mtg.collection.editions.Editions;
import mtg.collection.editions.EditionsController;
import ru.yandex.qatools.ashot.AShot;

public class SCGThread implements Runnable {

	private static final String PARENTHESES = " (";
	private static final String FLIP_NAME_DIVIDER = " | ";

	private static final ConcurrentHashMap<String, ArrayList<BufferedImage>> IMGS_MAP = new ConcurrentHashMap<String, ArrayList<BufferedImage>>();

	private final Editions edition;
	private final File logFile;
	private WebSocket ws;

	private boolean running = false;
	private boolean finished = false;
	private int pageNumber = 1;

	public SCGThread(final Editions edition) {
		this.edition = edition;
		logFile = new File(System.getProperty("user.dir") + "/target/chromedriver_" + edition.name() + ".log");
		FileUtils.deleteQuietly(logFile);

		try {
			readImgs();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void readImgs() throws IOException {
		if (!IMGS_MAP.isEmpty()) {
			return;
		}

		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();

		list.add(ImageIO.read(new File("imgs/point_0.png")));
		list.add(ImageIO.read(new File("imgs/point_1.png")));
		IMGS_MAP.put(".", list);

		for (int i = 0; i < 10; i++) {
			list = new ArrayList<BufferedImage>();
			list.add(ImageIO.read(new File("imgs/" + i + "_0.png")));
			list.add(ImageIO.read(new File("imgs/" + i + "_1.png")));
			IMGS_MAP.put("" + i, list);
		}

		IMGS_MAP.get("9").add(ImageIO.read(new File("imgs/9_2.png")));
	}

	public void run() {
		if (edition.getScgLink().isEmpty()) {
			return;
		}

		running = true;
		WebDriver driver = null;
		try {
			driver = getChromeDriver();
			driver.get(edition.getScgLink());
			final ArrayList<SCGCard> cardsList = new ArrayList<SCGCard>();

			while (isNextPage(driver)) {
				cardsList.addAll(readSCGEditionPage(driver));
				clickAtNextPage(driver);
			}
			cardsList.addAll(readSCGEditionPage(driver));

			updateCollectionPrices(cardsList);

			EditionsController.getInstance().writeEditions();
			updateEditionPriceDate();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (driver != null) {
				driver.quit();
			}
			FileUtils.deleteQuietly(logFile);
			finished = true;
		}
	}

	public Editions getEdition() {
		return edition;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public boolean isrRunning() {
		return running;
	}

	public boolean isFinished() {
		return finished;
	}

	private ChromeDriver getChromeDriver() throws IOException, WebSocketException, InterruptedException {
		final ChromeDriverService service = new ChromeDriverService.Builder().withLogFile(logFile).usingAnyFreePort()
				.withVerbose(true).build();
		service.start();

		final ChromeOptions options = new ChromeOptions();
		options.addArguments(Arrays.asList("headless", "window-size=1920x1080"));

		final ChromeDriver driver = new ChromeDriver(service, options);

		final String wsURL = getWebSocketDebuggerUrl();
		sendWSMessage(wsURL, blockUrls());
		sendWSMessage(wsURL, activeNetworkMessage());

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		return driver;
	}

	private String getWebSocketDebuggerUrl() throws IOException {
		String webSocketDebuggerUrl = "";
		try {
			final Scanner sc = new Scanner(logFile);
			String urlString = "";
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("DevTools request: http://localhost")) {
					urlString = line.substring(line.indexOf("http"), line.length()).replace("/version", "");
					break;
				}
			}
			sc.close();

			final URL url = new URL(urlString);
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final String json = IOUtils.toString(reader);
			final JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject.getString("type").equals("page")) {
					webSocketDebuggerUrl = jsonObject.getString("webSocketDebuggerUrl");
					break;
				}
			}
		} catch (final FileNotFoundException e) {
			throw e;
		}

		if (webSocketDebuggerUrl.equals("")) {
			throw new RuntimeException("webSocketDebuggerUrl not found");
		}
		return webSocketDebuggerUrl;
	}

	private void sendWSMessage(final String url, final String message)
			throws IOException, WebSocketException, InterruptedException {
		final JSONObject jsonObject = new JSONObject(message);
		final int messageId = jsonObject.getInt("id");
		if (ws == null) {
			ws = new WebSocketFactory().createSocket(url).addListener(new WebSocketAdapter() {
				@Override
				public void onTextMessage(final WebSocket ws, final String message) {
					if (new JSONObject(message).getInt("id") == messageId) {
					}
				}
			}).connect();
		}
		ws.sendText(message);
	}

	private String blockUrls() throws JsonProcessingException {
		final SetBlockedUrlsMessage msg = new SetBlockedUrlsMessage();
		msg.params.urls.add("apis.google.com");
		msg.params.urls.add("connect.facebook.net");
		msg.params.urls.add("platform.twitter.com");
		msg.params.urls.add("www.googletagservices.com");
		msg.params.urls.add("www.google-analytics.com");
		return new ObjectMapper().writeValueAsString(msg);
	}

	private String activeNetworkMessage() throws JsonProcessingException {
		final NetworkEnableMessage msg = new NetworkEnableMessage();
		return new ObjectMapper().writeValueAsString(msg);
	}

	private boolean isNextPage(final WebDriver driver) {
		final WebElement element = getLastLinkOfResultsTable(driver);
		if (element == null) {
			return false;
		}
		return element.getText().contains("Next");
	}

	private void clickAtNextPage(final WebDriver driver) {
		final WebElement e = getLastLinkOfResultsTable(driver);
		if (e != null) {
			pageNumber++;
			e.click();
		}
	}

	private WebElement getLastLinkOfResultsTable(final WebDriver driver) {
		final List<WebElement> linhas = driver.findElement(By.id("search_results_table"))
				.findElements(By.tagName("tr"));
		final List<WebElement> links = linhas.get(linhas.size() - 1).findElements(By.tagName("a"));
		return links.isEmpty() ? null : links.get(links.size() - 1);
	}

	private ArrayList<SCGCard> readSCGEditionPage(final WebDriver driver) throws IOException, InterruptedException {
		final ArrayList<SCGCard> cardsLists = new ArrayList<SCGCard>();
		final WebElement resultsTable = driver.findElement(By.id("search_results_table"));
		final List<WebElement> linhas = resultsTable.findElements(By.tagName("tr"));

		Predicate<WebElement> predicate = element -> element.getAttribute("class").isEmpty();
		linhas.removeIf(predicate);

		predicate = element -> !element.findElement(By.className("search_results_7")).getText().equals("NM/M");
		linhas.removeIf(predicate);

		for (final WebElement linha : linhas) {
			SCGCard card = new SCGCard();
			card.name = linha.findElement(By.className("search_results_1")).getText();

			if (card.name.startsWith("[") || card.name.contains("(Oversized)")) {
				continue;
			}

			if (card.name.contains(FLIP_NAME_DIVIDER)) {
				card.name = card.name.substring(0, card.name.indexOf(FLIP_NAME_DIVIDER));
			} else if (card.name.contains(PARENTHESES)) {
				if (card.name.contains("(FOIL)")) {
					card.foil = true;
				}
				card.name = card.name.substring(0, card.name.indexOf(PARENTHESES));
			} else if (card.name.contains("//")) {
				final String[] tokens = card.name.split(" // ");
				card.name = tokens[0] + " (" + tokens[0] + "/" + tokens[1] + ")";
			}

			if (!card.foil) {
				card.foil = linha.findElement(By.className("search_results_2")).getText().contains("(Foil)");
			}

			final WebElement priceElement = linha.findElement(By.className("search_results_9"));
			final JavascriptExecutor jse = (JavascriptExecutor) driver;
			final String val = jse.executeScript("return arguments[0].childElementCount;", priceElement).toString();

			if (val.equals("1")) {
				final List<WebElement> priceDivs = priceElement.findElements(By.tagName("div"));
				for (final WebElement priceImg : priceDivs) {
					if (priceImg.getAttribute("style").contains("width:45px") || priceImg.getText().contains("$")) {
						continue;
					}
					card.price += resolveImage(captureElementImage(driver, priceImg));
				}
			} else {
				card.price = priceElement.findElement(By.tagName("span")).getText().trim().substring(1);
			}

			cardsLists.add(card);
		}

		return cardsLists;
	}

	private BufferedImage captureElementImage(final WebDriver driver, final WebElement priceChar) throws IOException {
		return new AShot().takeScreenshot(driver, priceChar).getImage();
	}

	private String resolveImage(final BufferedImage img) throws IOException {
		String key = ".";
		ArrayList<BufferedImage> list = IMGS_MAP.get(key);
		for (BufferedImage mappedImg : list) {
			if (getDifferencePercent(img, mappedImg) < 5) {
				return key;
			}
		}

		for (int i = 0; i < 10; i++) {
			key = "" + i;
			list = IMGS_MAP.get(key);
			for (BufferedImage mappedImg : list) {
				if (getDifferencePercent(img, mappedImg) < 5) {
					return key;
				}
			}
		}

		ImageIO.write(img, "png", new File(Math.random() + ".png"));
		return "";
	}

	private double getDifferencePercent(final BufferedImage img1, final BufferedImage img2) {
		final int width = img1.getWidth();
		final int height = img1.getHeight();
		final int width2 = img2.getWidth();
		final int height2 = img2.getHeight();
		if (width != width2 || height != height2) {
			return 100.0;
		}

		long diff = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
			}
		}
		final long maxDiff = 3L * 255 * width * height;

		return 100.0 * diff / maxDiff;
	}

	private int pixelDiff(final int rgb1, final int rgb2) {
		final int r1 = (rgb1 >> 16) & 0xff;
		final int g1 = (rgb1 >> 8) & 0xff;
		final int b1 = rgb1 & 0xff;
		final int r2 = (rgb2 >> 16) & 0xff;
		final int g2 = (rgb2 >> 8) & 0xff;
		final int b2 = rgb2 & 0xff;
		return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
	}

	private void updateCollectionPrices(final ArrayList<SCGCard> cardsList) {
		for (final SCGCard card : cardsList) {
			EditionsController.getInstance().setScgPrice(edition, card);
		}
	}

	private void updateEditionPriceDate() {
		final ObjectMapper mapper = new ObjectMapper();
		final File editionsPricesFile = new File("editionsPriceDate.json");

		final SCGEditionPriceDate date = new SCGEditionPriceDate();
		date.editionName = edition.getName();
		date.time = new Date().getTime();

		try {
			if (!editionsPricesFile.exists()) {
				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(Arrays.asList(date));
				FileUtils.write(editionsPricesFile, json, Charset.defaultCharset());
				return;
			}

			final ArrayList<SCGEditionPriceDate> list = new ArrayList<SCGEditionPriceDate>();
			list.addAll(Arrays.asList(mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(editionsPricesFile, Charset.defaultCharset()).getBytes("UTF-8")),
					SCGEditionPriceDate[].class)));

			if (list.contains(date)) {
				list.remove(date);
			}

			list.add(date);

			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
			FileUtils.write(editionsPricesFile, json, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
