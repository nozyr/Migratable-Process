import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProcessManager {

	private static final int SERVER_PORT = 440;
	private static final int SLAVE_PORT_1 = 441;
	private static final int SLAVE_PORT_2 = 442;

	private List<SlaveNode> slaves;
	private List<Integer> process;
	private Map<Integer, MigratableProcess> idToProcess; // mapping from process
															// id to process

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

		Class<?> task;
		try {
			task = Class.forName(className);
			Constructor<?> c = task.getDeclaredConstructor(String[].class);
			Object o = c.newInstance((Object[]) args);
			MigratableProcess process = (MigratableProcess) o;

			ProcessMessage p = new ProcessMessage(process, Message.LAUNCH);

		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

	}
}
