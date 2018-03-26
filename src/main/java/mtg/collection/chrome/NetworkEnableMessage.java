package mtg.collection.chrome;

public class NetworkEnableMessage {
	
	public class Params {
		public long maxTotalBufferSize = 10000000;
		public long maxResourceBufferSize = 5000000;
	};

	public int id = 2;
	public String method = "Network.enable";
	public Params params = new Params();

}
