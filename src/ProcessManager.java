public class ProcessManager {

	public void lauch(String className) {
		try {
			Class<?> task = Class.forName(className);
			Object o = task.newInstance();
			MigratableProcess process = (MigratableProcess) o;
			process.run();
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
