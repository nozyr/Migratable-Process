import java.io.PrintStream;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

public class GrepProcess implements MigratableProcess {
	/**
	 *
	 */
	private static final long serialVersionUID = -2757242538506557482L;
	private TransactionalFileInputStream inFile;
	private TransactionalFileOutputStream outFile;
	private String query;

	private volatile boolean suspending = false;

	public GrepProcess(String[] args) throws Exception {
		if (args.length != 3) {
			System.out
					.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}

		query = args[0];
		inFile = new TransactionalFileInputStream(args[1]);
		outFile = new TransactionalFileOutputStream(args[2], false);
	}

	public void run() {
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		try {
			while (!suspending) {

				String line = in.readLine();
				// System.out.println("read line: " + line);

				if (line == null)
					break;

				if (line.contains(query)) {
					out.println(line);
					System.out.println(line);
				}

				// Make grep take longer so that we don't require extremely
				// large files for interesting results
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore it
				}
			}
		} catch (EOFException e) {
			// End of File
		} catch (IOException e) {
			System.out.println("GrepProcess: Error: " + e);
		}
		suspending = false;
		out.close();
	}

    public void suspend(Thread thd) {
        suspending = true;
        while (thd.getState() != Thread.State.TERMINATED);
    }

}