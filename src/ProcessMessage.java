import java.io.Serializable;

public class ProcessMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3467973832854667253L;

	private int pId; // process id
	private MigratableProcess task;
	private Message message;
	private String className;
	private String[] args;

	public ProcessMessage(int id, MigratableProcess task, Message message) {
		this.pId = id;
		this.task = task;
		this.setMessage(message);
	}

	public ProcessMessage() {

	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

	public MigratableProcess getTask() {
		return task;
	}

	public void setTask(MigratableProcess task) {
		this.task = task;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
