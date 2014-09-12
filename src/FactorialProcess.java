import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FactorialProcess implements MigratableProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8499330984331142768L;
	private long result = 1;
	private TransactionalFileInputStream inFile;
	private TransactionalFileOutputStream outFile;
	private volatile boolean suspending;

	public FactorialProcess(String[] args) throws Exception {
		if (args.length != 2) {
			System.out
					.println("usage: FactorialProcess <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}
		inFile = new TransactionalFileInputStream(args[0]);
		outFile = new TransactionalFileOutputStream(args[1], false);
	}

	@Override
	public void suspend() {
		suspending = true;
		while (suspending)
			;
	}

	@Override
	public void run() {
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		while (!suspending) {
			String line;
			try {

				line = in.readLine();
				if (line == null) {
					break;
				}
				int num = Integer.parseInt(line);
				result *= num;
				System.out.println("The factorial of " + num + " is " + result);
				out.println("The factorial of " + num + " is " + result);

			} catch (IOException e) {
				e.printStackTrace();
			}

			// Make Factorial take longer so that we don't require extremely
			// large files for interesting results
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore it
			}
			suspending = false;
			out.close();

		}
	}

}
