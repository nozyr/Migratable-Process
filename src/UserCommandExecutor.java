import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This thread takes user command from command line and then send it to master
 * 
 * @author weisiyu
 * 
 */
public class UserCommandExecutor implements Runnable {

	private static final String SERVER_HOST = "localhost";
	private static final String STOP_SIGN = "Stop";
	private static final int SERVER_PORT = 15440;
	private static final int NUM_ARGS = 2;
	private static Map<String, Message> mapping = new HashMap<String, Message>();
	private Scanner scan;

	public UserCommandExecutor(Scanner scan) {
		this.scan = scan;
	}

	@Override
	public void run() {

		setUp();

		try {

			while (true) {
				String line = scan.nextLine();
				Socket connection;
				ObjectOutputStream out;
				String[] arguments = line.split("\\W");
				if (arguments[0].equalsIgnoreCase(STOP_SIGN)) {
					connection = new Socket(SERVER_HOST, SERVER_PORT);
					ProcessMessage migrate = new ProcessMessage();
					migrate.setMessage(Message.STOP);
					out = new ObjectOutputStream(connection.getOutputStream());
					out.writeObject(migrate);
					connection.close();
					break;
				}

				if (arguments.length != NUM_ARGS) {
					printUsage();
				} else {
					Message message = mapping.get(arguments[0].toLowerCase());
					ProcessMessage task = new ProcessMessage();
					if (message == null) {
						printUsage();
						continue;
					}
					switch (message) {
					/*
					 * launch a process
					 */
					case LAUNCH:
						connection = new Socket(SERVER_HOST, SERVER_PORT);
						task.setMessage(Message.LAUNCH);
						task.setClassName(arguments[1]);
						task.setArgs(getArguments(task, scan));
						out = new ObjectOutputStream(
								connection.getOutputStream());
						out.writeObject(task);
						InputStream in = connection.getInputStream();
						int id = in.read();
						System.out
								.println("The assigned id for this process is: "
										+ id);
						connection.close();
						break;
					/*
					 * Migrate a process
					 */
					case MIGRATE:
						connection = new Socket(SERVER_HOST, SERVER_PORT);
						ProcessMessage migrate = new ProcessMessage();
						migrate.setMessage(Message.MIGRATE);
						migrate.setpId(Integer.parseInt(arguments[1]));
						out = new ObjectOutputStream(
								connection.getOutputStream());
						out.writeObject(migrate);

						connection.close();
						break;
					case SUSPEND:
						connection = new Socket(SERVER_HOST, SERVER_PORT);
						ProcessMessage suspend = new ProcessMessage();
						suspend.setMessage(Message.SUSPEND);
						suspend.setpId(Integer.parseInt(arguments[1]));
						out = new ObjectOutputStream(
								connection.getOutputStream());
						out.writeObject(suspend);
						connection.close();
						break;
					case RESTART:
						connection = new Socket(SERVER_HOST, SERVER_PORT);
						ProcessMessage restart = new ProcessMessage();
						restart.setMessage(Message.RESTART);
						restart.setpId(Integer.parseInt(arguments[1]));
						out = new ObjectOutputStream(
								connection.getOutputStream());
						out.writeObject(restart);
						connection.close();
						break;
					default:
						break;
					}
				}

			}
			scan.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void printUsage() {
		System.out
				.println("*** Usage <Operation> <Process Name/Process Id> ***");
		System.out.println("*** Launch Process Name / Migrate Process Id***");
		System.out
				.println("*** Sample Usage: ***\n*** launch GrepProcess***\n*** migrate 2 ***\n");
	}

	private static void setUp() {
		mapping.put("launch", Message.LAUNCH);
		mapping.put("migrate", Message.MIGRATE);
		mapping.put("suspend", Message.SUSPEND);
		mapping.put("restart", Message.RESTART);
		printUsage();
	}

	private static String[] getArguments(ProcessMessage task, Scanner scan) {
		System.out.println("Please type in the arguments for launching"
				+ task.getClassName());
		String line = scan.nextLine();
		String args[] = line.split("[ ]");
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		return args;
	}

}
