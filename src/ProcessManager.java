import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * The ProcessManager class that represents the manager of launching, migrating
 * and suspending process
 * 
 * @author weisiyu
 * 
 */
public class ProcessManager {

	private static final int SERVER_PORT = 15440;

	private int givenId;
	private List<SlaveInfo> slaves;
	private List<Integer> processList;
	private Map<Integer, MigratableProcess> idToProcess; // mapping from process
															// id to process
	private Map<Integer, SlaveInfo> idToSlave;
	private List<Process> runningNodes;

	public ProcessManager() {
		/*
		 * Initialize class members
		 */
		givenId = 0;
		slaves = new LinkedList<SlaveInfo>();
		processList = new LinkedList<Integer>();
		idToProcess = new HashMap<Integer, MigratableProcess>();
		idToSlave = new HashMap<Integer, SlaveInfo>();
		runningNodes = new LinkedList<Process>();

		this.setUp();

	}

	/**
	 * Set up configuration by user inputs
	 */
	private void setUp() {

		Scanner scan = new Scanner(System.in);
		System.out.println("How many slaves will be running?");
		int numSlave = Integer.parseInt(scan.nextLine());
		for (int i = 0; i < numSlave; i++) {
			System.out.println("Type in the assigned port for slave " + (i + 1)
					+ ":");
			int port = Integer.parseInt(scan.nextLine());
			slaves.add(new SlaveInfo(port));
		}
		System.out.println(slaves.size() + "slaves are started");
		// this.startSlaves();
		Thread executor = new Thread(new UserCommandExecutor(scan));
		executor.start();

	}

	private void startSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			ProcessBuilder builder = new ProcessBuilder("java", "SlaveNode",
					String.valueOf(slaves.get(i).getPort()), String.valueOf(i));

			builder.redirectErrorStream(true);
			builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

			try {
				Process node = builder.start();
				runningNodes.add(node);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		ProcessManager manager = new ProcessManager();

		try {
			ServerSocket master = new ServerSocket(SERVER_PORT);

			outer: while (true) {
				/*
				 * The process managers is polling for connections
				 */
				System.out.println("waiting for connection");
				Socket connection = master.accept();
				System.out.println("connected");
				ObjectInputStream in = new ObjectInputStream(
						connection.getInputStream());
				Object o = in.readObject();
				ProcessMessage message = (ProcessMessage) o;
				OutputStream out = connection.getOutputStream();

				/*
				 * After receiving a message, the process manager behave
				 * according to the message
				 */

				switch (message.getMessage()) {
				case LAUNCH:
					/*
					 * The manager launches a process when receiving "launch"
					 * and give back the assigned pId
					 */
					int assignedId = manager.launch(message.getClassName(),
							message.getArgs());
					out.write(assignedId);
					break;
				case MIGRATE:
					/*
					 * The manager migrate the process
					 */
					manager.migrate(message.getpId());
					break;
				case SUSPEND:
					/*
					 * Suspend the process with the given ID
					 */
					manager.sendMessage(message.getpId(), Message.SUSPEND);
				case RESTART:
					/*
					 * Restart the process with the given ID
					 */
					manager.sendMessage(message.getpId(), Message.RESTART);
				case STOP:
					/*
					 * the process manager process stops upon receiving message
					 * /* of "STOP
					 */
					break outer;
				default:
					break;
				}

				connection.close();

			}

			master.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param id
	 */
	public void remove(int id) {
		processList.remove(new Integer(id));
		idToProcess.remove(id);
		idToSlave.remove(id);
	}

	public void sendMessage(int id, Message info) {
		ProcessMessage message = new ProcessMessage();
		message.setpId(id);
		message.setMessage(info);
		SlaveInfo worker = idToSlave.get(id);
		try {
			Socket s = new Socket(worker.getHostName(), worker.getPort());
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(message);
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Migrate a process from one slave to another
	 * 
	 * @param id
	 */
	public void migrate(int id) {
		SlaveInfo worker = idToSlave.get(id);
		try {
			/*
			 * Send message to the slave that is currently executing the task,
			 * let it stop and send back the task.
			 */
			Socket socket = new Socket(worker.getHostName(), worker.getPort());
			ProcessMessage message = new ProcessMessage(id, null,
					Message.MIGRATE);
			ObjectOutputStream out = new ObjectOutputStream(
					socket.getOutputStream());
			out.writeObject(message);
			ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());
			Object o = in.readObject();
			socket.close();

			/*
			 * Migrate the task to a new worker to execute
			 */
			MigratableProcess toMigrate = (MigratableProcess) o;

			/*
			 * Assign the task to the next slave
			 */
			int index = slaves.indexOf(worker);
			index++;
			SlaveInfo newWorker = slaves.get(index % slaves.size());
			idToSlave.put(id, newWorker);

			/*
			 * Send the task to the new slave for executing
			 */
			socket = new Socket(newWorker.getHostName(), newWorker.getPort());
			message = new ProcessMessage(id, toMigrate, Message.LAUNCH);
			System.out.println("send");
			out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(message);
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get a process name and sent it to a slave for launching
	 * 
	 * @param className
	 * @param args
	 */
	public int launch(String className, String[] args) {

		int id = -1;
		try {
			/*
			 * Instantiate the task from the given name and arguments with Java
			 * reflection
			 */
			Class<?> task;
			task = Class.forName(className);
			Constructor<?> c = task.getDeclaredConstructor(String[].class);
			Object o = c.newInstance((Object) args);
			MigratableProcess process = (MigratableProcess) o;

			/*
			 * Put the task into the working process list
			 */
			id = givenId;
			givenId++;
			processList.add(id);
			idToProcess.put(id, process);

			/*
			 * Encapsulate the process with a launching message
			 */
			ProcessMessage launchingMessage = new ProcessMessage(id, process,
					Message.LAUNCH);
			Random r = new Random();
			int slaveId = r.nextInt(slaves.size());
			SlaveInfo worker = slaves.get(slaveId);
			idToSlave.put(id, worker);

			/*
			 * Sends the task with the message to a slave
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

		return id;

	}
}
