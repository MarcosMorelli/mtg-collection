package mtg.collection.cardinfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.MagicCard;
import mtg.collection.editions.Editions;

public class CollectionReader {

	public static MagicCard[] readCollection(Editions edition) {
		final String fileName = "editions/" + edition.name().replaceAll("_", "");
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(new File(fileName), Charset.defaultCharset()).getBytes("UTF-8")),
					MagicCard[].class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void writeCollection(final MagicCard[] list, final Editions edition) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
		FileUtils.write(new File("editions/" + edition.name().replaceAll("_", "")), json, Charset.defaultCharset());
	}
}
