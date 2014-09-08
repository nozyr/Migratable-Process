import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProcessManager {

	private static final int SERVER_PORT = 440;
	private static final int SLAVE_PORT_1 = 441;
	private static final int SLAVE_PORT_2 = 442;

	private int givenId;
	private List<SlaveInfo> slaves;
	private List<Integer> processList;
	private Map<Integer, MigratableProcess> idToProcess; // mapping from process
															// id to process

	public ProcessManager() {

	}

	public static void main(String[] args) {
		try {
			ServerSocket master = new ServerSocket(SERVER_PORT);
			while (true) {

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {

		}
	}
	
	
	public void lauch(String className, String args[]) {

		try {
			/*
			 * Instantiate the task from the given name and arguments
			 */
			Class<?> task;
			task = Class.forName(className);
			Constructor<?> c = task.getDeclaredConstructor(String[].class);
			Object o = c.newInstance((Object[]) args);
			MigratableProcess process = (MigratableProcess) o;

			/*
			 * Put the task into the working process list
			 */
			int id = givenId;
			givenId++;
			processList.add(id);
			idToProcess.put(id, process);

			/*
			 * Encapsulate the process with a lauching message
			 */
			ProcessMessage launchingMessage = new ProcessMessage(process,
					Message.LAUNCH);
			Random r = new Random();
			int slaveId = r.nextInt(slaves.size());
			SlaveInfo worker = slaves.get(slaveId);

			/*
			 * Send the task to a slave
			 */
			try {
				Socket s = new Socket(worker.getHostName(), worker.getPort());
				ObjectOutputStream out = new ObjectOutputStream(
						s.getOutputStream());
				out.writeObject(launchingMessage);
				s.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

	}
}
