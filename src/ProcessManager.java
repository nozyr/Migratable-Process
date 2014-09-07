import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;

public class ProcessManager {

	private static final int SERVER_PORT = 888;
	private List<SlaveNode> salves;
	private Map<Integer, MigratableProcess> idToProcess; // mapping from process
															// id to process

	public static void main(String[] args) {
		try {
			ServerSocket master = new ServerSocket(8080);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {

		}
	}

	// public void lauch(String className) {
	// try {
	// Class<?> task = Class.forName(className);
	// Constructor<?> c = task.getDeclaredConstructor(String[].class);
	// Object o = c.newInstance(String[].class);
	// MigratableProcess process = (MigratableProcess) o;
	// process.run();
	// } catch (ClassNotFoundException | InstantiationException
	// | IllegalAccessException | IllegalArgumentException
	// | InvocationTargetException | NoSuchMethodException
	// | SecurityException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
