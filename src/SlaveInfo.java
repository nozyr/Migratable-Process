public class SlaveInfo {

	private final String HOST_NAME = "localhost";
	private int port;

	public SlaveInfo(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public String getHostName() {
		return this.HOST_NAME;
	}

}
