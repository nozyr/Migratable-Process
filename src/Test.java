import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
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
			arg1[0] = ".";
			arg1[1] = "in.txt";
			arg1[2] = "out.txt";

			launchGrep.setClassName("GrepProcess");
			launchGrep.setArgs(arg1);
			launchGrep.setMessage(Message.LAUNCH);

			ObjectOutputStream out = new ObjectOutputStream(
					connection.getOutputStream());
			out.writeObject(launchGrep);
			InputStream in = connection.getInputStream();
			int id = in.read();

			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("WTF");

			connection.close();

			connection = new Socket(HOST, PORT);
			ProcessMessage migrate = new ProcessMessage();
			migrate.setMessage(Message.MIGRATE);
			migrate.setpId(id);

			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(migrate);

			connection.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
