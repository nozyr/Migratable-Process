import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class SlaveNode {

	private static final int PORT_NUMBER = 1441;
	private static HashMap<Integer, MigratableProcess> TaskMap;

	public static void main(String[] args) {
		ServerSocket listen_Socket = null;
		TaskMap = new HashMap<Integer, MigratableProcess>();

		try {

			listen_Socket = new ServerSocket(Integer.parseInt(args[0]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.printf("Cannot bind to Port Number %d",
					Integer.parseInt(args[0]));
			e.printStackTrace();
			System.exit(-1);
		}

		outer: while (true) {
			Socket Accept_Socket = null;
			ObjectInputStream ois = null;
			try {
				Accept_Socket = listen_Socket.accept();
				System.out.println("Accpeted a command");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				ois = new ObjectInputStream(Accept_Socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Object Rec_Obj = null;
			try {
				Rec_Obj = ois.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ProcessMessage task_Message = (ProcessMessage) Rec_Obj;
			switch (((ProcessMessage) Rec_Obj).getMessage()) {
			case LAUNCH:
				Thread task_th = new Thread(task_Message.getTask());
				task_th.start();
				System.out.println("Task started");
				TaskMap.put(task_Message.getpId(), task_Message.getTask());
				break;
			case SUSPEND:
				if (TaskMap.containsKey(task_Message.getpId())) {
					MigratableProcess task = TaskMap.get(task_Message.getpId());
					task.suspend();
				}
				break;
			case RESTART:
				break;
			case STOP:
				break outer;
			case MIGRATE:
				if (TaskMap.containsKey(task_Message.getpId())) {
					MigratableProcess task = TaskMap.get(task_Message.getpId());
					task.suspend();
					ObjectOutputStream oos = null;
					try {
						oos = new ObjectOutputStream(
								Accept_Socket.getOutputStream());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						oos.writeObject(task);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					TaskMap.remove(task_Message.getpId());
				}
				break;
			default:
				break;

			}

		}

	}
}
