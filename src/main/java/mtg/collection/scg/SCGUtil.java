package mtg.collection.scg;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
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
import mtg.collection.editions.MagicCard;
import ru.yandex.qatools.ashot.AShot;

public class SCGUtil {

	private static final String PARENTHESES = " (";
	private static final String FLIP_NAME_DIVIDER = " | ";
	private static final ConcurrentHashMap<String, ArrayList<BufferedImage>> IMGS_MAP = new ConcurrentHashMap<String, ArrayList<BufferedImage>>();

	private final File logFile;
	private WebSocket ws;

	public static final List<String> BASIC_LANDS = new ArrayList<String>(
			Arrays.asList("Island", "Swamp", "Mountain", "Plains", "Forest",
					"Island (FOIL)", "Swamp (FOIL)", "Mountain (FOIL)", "Plains (FOIL)", "Forest (FOIL)"));

	public SCGUtil() {
		this(Editions.rix);
	}

	public SCGUtil(Editions edition) {
		logFile = new File(System.getProperty("user.dir") + "/target/chromedriver_" + edition.name() + ".log");
		logFile.deleteOnExit();

		if (IMGS_MAP.isEmpty()) {
			try {
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
			} catch (Exception e) {
			}
		}
	}

	public ChromeDriver getChromeDriver() throws IOException, WebSocketException, InterruptedException {
		final ChromeDriverService service = new ChromeDriverService.Builder().withLogFile(logFile).usingAnyFreePort()
				.withVerbose(true).build();
		service.start();

		final ChromeOptions options = new ChromeOptions();
		//options.addArguments(Arrays.asList("headless", "window-size=1920x1080"));

		final ChromeDriver driver = new ChromeDriver(service, options);

		final String wsURL = getWebSocketDebuggerUrl();
		sendWSMessage(wsURL, blockUrls());
		sendWSMessage(wsURL, activeNetworkMessage());

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		return driver;
	}

	public List<WebElement> getLinesFromTable(final WebDriver driver, final Editions edition) {
		try {
			// TODO MELHORAR ESSE PAGE LOAD
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final WebElement productsListingContainer = driver.findElement(By.id("product-listing-container"));
		final WebElement articleElement = productsListingContainer.findElement(By.className("productList"));
		final WebElement tableElement = articleElement.findElement(By.tagName("table"));

		final List<WebElement> lines = tableElement.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

		final ArrayList<WebElement> toRemoveLines = new ArrayList<WebElement>();

		lines.forEach(line -> {
			List<WebElement> columns = line.findElements(By.tagName("td"));
			String name = columns.get(0).getText().replaceAll("\n", "");
			System.out.println(name);
			if (columns.size() < 6) {
				System.out.println("removing " + name + " due size");
				toRemoveLines.add(line);
				return;
			}

			if (!columns.get(4).getText().equalsIgnoreCase("Near Mint")) {
				System.out.println("removing " +name + " due nm");

				toRemoveLines.add(line);
				return;
			}
			
			if (columns.get(3).getText().equalsIgnoreCase("Basic Land")) {
				System.out.println("removing " + name + " due basic land");

				toRemoveLines.add(line);
				return;
			}
			
			if (columns.get(3).getText().equalsIgnoreCase("Token")) {
				System.out.println("removing " + name + " due token");

				toRemoveLines.add(line);
				return;
			}
		});

		lines.removeAll(toRemoveLines);
		
		return lines;
	}

	public boolean isNextPage(final WebDriver driver) {
		return getNextPageElement(driver) != null;
	}

	public void clickAtNextPage(final WebDriver driver) {
		final WebElement e = getNextPageElement(driver);
		if (e != null) {
			e.click();
		}
	}

	public String getCardName(final String name) {
		if (name.contains(SCGUtil.FLIP_NAME_DIVIDER)) {
			return name.substring(0, name.indexOf(SCGUtil.FLIP_NAME_DIVIDER));
		} else if (name.contains("//")) {
			final String[] tokens = name.split(" // ");
			return tokens[0] + " (" + tokens[0] + "/" + tokens[1] + ")";
		} else if (name.contains(SCGUtil.PARENTHESES)) {
			return name.substring(0, name.indexOf(SCGUtil.PARENTHESES));
		} else {
			return name;
		}
	}

	public boolean isFoil(final String name, final String category) {
		if (name.contains(MagicCard.FOIL_STRING)) {
			return true;
		}

		return category.contains("(Foil)");
	}

	public String getManaCost(final WebDriver driver, final WebElement element) {
		final StringBuffer cost = new StringBuffer();
		element.findElements(By.tagName("i")).forEach(i -> {
			String classAttr = i.getAttribute("class");
			classAttr = classAttr.substring(classAttr.indexOf(" ") + 1);
			classAttr = classAttr.substring(0, classAttr.indexOf(" "));
			switch (classAttr) {
			case "ms-r":
				cost.append("R");
				break;
			case "ms-g":
				cost.append("G");
				break;
			case "ms-u":
				cost.append("U");
				break;
			case "ms-b":
				cost.append("B");
				break;
			case "ms-w":
				cost.append("W");
				break;
			default:
				cost.append(classAttr.substring(classAttr.indexOf("-") + 1));
				break;
			}
		});
		return cost.toString();
	}

	public String getPrice(final WebDriver driver, final WebElement element) throws IOException {
		final JavascriptExecutor jse = (JavascriptExecutor) driver;
		final String val = jse.executeScript("return arguments[0].childElementCount;", element).toString();

		if (val.equals("1")) {
			StringBuffer buffer = new StringBuffer();
			final List<WebElement> priceDivs = element.findElements(By.tagName("div"));
			for (final WebElement priceImg : priceDivs) {
				if (priceImg.getAttribute("style").contains("width:45px") || priceImg.getText().contains("$")) {
					continue;
				}
				buffer.append(resolveImage(captureElementImage(driver, priceImg)));
			}

			return buffer.toString();
		} else {
			final String price = element.getText().trim();
			if (price.matches("\\$[0-9]+\\.[0-9]{2}")) {
				return price.substring(1);
			} else {
				return price.substring(1, price.lastIndexOf("$") - 1);
			}
		}
	}

	public String getRarity(final String text) {
		switch (text) {
		case "C":
			return "Common";
		case "U":
			return "Uncommon";
		case "R":
			return "Rare";
		case "M":
			return "Mythic Rare";
		default:
			return "Promotional";
		}
	}

	public String getType(final String text, final String text2) {
		final StringBuffer buffer = new StringBuffer(text);
		if (text2.isEmpty()) {
			return buffer.toString();
		}

		buffer.append(" ").append(text2);
		return buffer.toString();
	}

	private String getWebSocketDebuggerUrl() throws IOException {
		String webSocketDebuggerUrl = "";
		try {
			final Scanner sc = new Scanner(logFile);
			String urlString = "";
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("DevTools HTTP Request: http://localhost")) {
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
		msg.params.urls.add("*.gif");
		msg.params.urls.add("*.jpg");
		msg.params.urls.add("*.png");
		return new ObjectMapper().writeValueAsString(msg);
	}

	private String activeNetworkMessage() throws JsonProcessingException {
		final NetworkEnableMessage msg = new NetworkEnableMessage();
		return new ObjectMapper().writeValueAsString(msg);
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

	private WebElement getNextPageElement(final WebDriver driver) {
		try {
			final List<WebElement> links = driver.findElements(By.className("pagination-item--next"));
			if (links.isEmpty()) {
				return null;
			}
			return links.get(0);
		} catch (final NoSuchElementException e) {
			return null;
		}
	}

}
