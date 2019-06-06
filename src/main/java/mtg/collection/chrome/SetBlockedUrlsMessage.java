package mtg.collection.chrome;

import java.util.ArrayList;

public class SetBlockedUrlsMessage {
	
	public class Params {
		public ArrayList<String> urls = new ArrayList<String>();
	};
	
	public int id = 1;
	public String method = "Network.setBlockedURLs";
	public Params params = new Params();

}
