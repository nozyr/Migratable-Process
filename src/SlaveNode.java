import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/*
 * Created by yuruiz,
 * this class acts as a slave server which devote to task running.
 *
 */
public class SlaveNode {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("usage: SlaveNode <portNum> <nodeID>");
            throw new Exception("Invalid Arguments");
        }
        ServerSocket listen_Socket = null;
        HashMap<Integer, MigratableProcess> TaskMap = new HashMap<>();
        HashMap<Integer, Thread> ThdMap = new HashMap<>();

        try {
            /* Initialize the Server Socket on port provided by args */
            listen_Socket = new ServerSocket(Integer.parseInt(args[0]));
        } catch (IOException e) {
            System.out.printf("Cannot bind to Port Number %d", Integer.parseInt(args[0]));
            e.printStackTrace();
            System.exit(-1);
        }

        while (true) {
            Socket Accept_Socket = null;
            ObjectInputStream ois = null;
            try {
                /* Accept Connection from Process Manager */
                Accept_Socket = listen_Socket.accept();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                /* Receive The input Stream */
                ois = new ObjectInputStream(Accept_Socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

			/* Read the input stream and convert it to object */

            Object Rec_Obj = null;
            try {
                Rec_Obj = ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

			/* Cast the object as ProcessMessage Object */
            ProcessMessage task_Message = (ProcessMessage) Rec_Obj;

			/* Check out the command contained in ProcessMessage Object */
            switch (((ProcessMessage) Rec_Obj).getMessage()) {
                case LAUNCH:
                /* Launch the task contained in ProcessMessage Object */
                    System.out.println("Command " + Message.LAUNCH.name() + " Received at Node " + args[1]);
                    Thread task_th = new Thread(task_Message.getTask());
                    task_th.start();
                    TaskMap.put(task_Message.getpId(), task_Message.getTask());
                    ThdMap.put(task_Message.getpId(), task_th);
                    break;
                case SUSPEND:
				/* Suspend the task currently running in this node */
                    System.out.println("Command " + Message.SUSPEND.name() + " Received at Node " + args[1]);
                    if (TaskMap.containsKey(task_Message.getpId())) {
                        MigratableProcess task = TaskMap.get(task_Message.getpId());
                        Thread thread = ThdMap.remove(task_Message.getpId());
                        if (thread.getState() == Thread.State.TERMINATED) {
                            taskFinished(Accept_Socket, task_Message.getpId());
                        } else {
                            task.suspend(thread);
                        }
                    }
                    break;
                case RESTART:
                    System.out.println("Command " + Message.RESTART.name() + " Received at Node " + args[1]);
                    if (TaskMap.containsKey(task_Message.getpId())) {
                        if (ThdMap.containsKey(task_Message.getpId())) {
                            System.out.printf("Task %d already running", task_Message.getpId());
                            break;
                        }
                        MigratableProcess task = TaskMap.get(task_Message.getpId());
                        Thread task_rs = new Thread(task);
                        ThdMap.put(task_Message.getpId(), task_rs);
                        task_rs.start();
                    }
                    break;
                case MIGRATE:
				/*
				 * Migrate a task that currently running in this node to the
				 * Process Manager Node
				 */
                    System.out.println("Command " + Message.MIGRATE.name() + " Received at Node " + args[1]);
                    if (TaskMap.containsKey(task_Message.getpId())) {
                        MigratableProcess task = TaskMap.get(task_Message.getpId());
                        Thread thread = ThdMap.remove(task_Message.getpId());
                        if (thread.getState() == Thread.State.TERMINATED) {
                            TaskMap.remove(task_Message.getpId());
                            taskFinished(Accept_Socket, task_Message.getpId());
                            break;
                        } else {
                            task.suspend(thread);
                        }
                        ObjectOutputStream oos = null;
                        try {
                            oos = new ObjectOutputStream(Accept_Socket.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            oos.writeObject(task);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        TaskMap.remove(task_Message.getpId());
                    }
                    break;
                default:
                    System.out.println("Unknown Message Received!!");
                    break;

            }

        }

    }

    private static void taskFinished(Socket socket, int id)
    {
        ObjectOutputStream oos = null;
        ProcessMessage back_message = new ProcessMessage(id, null, Message.FINISHED);
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos.writeObject(back_message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
