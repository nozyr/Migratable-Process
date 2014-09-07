public class ProcessMessage {

	private int pId; // process id
	private MigratableProcess task;
	private Message message;

	public ProcessMessage(MigratableProcess task, Message message) {
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

}
