import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Test {
	private static final String HOST = "localhost";
	private static final int PORT = 440;

	public static void main(String args[]) {
		try {
			Socket connection = new Socket(HOST, PORT);

			ProcessMessage launchGrep = new ProcessMessage();
			String[] arg1 = new String[3];

			launchGrep.setClassName("GrepProcess");
			launchGrep.setArgs(arg1);
			launchGrep.setMessage(Message.LAUNCH);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
