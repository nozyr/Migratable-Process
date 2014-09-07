public class ProcessMessage {
	private int pId; // process id

	public enum Message {
		LAUNCH, SUSPEND, RESTART, STOP
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

}
