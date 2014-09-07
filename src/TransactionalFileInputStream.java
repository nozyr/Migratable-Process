import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * 
 * @author weisiyu
 * 
 */
public class TransactionalFileInputStream extends InputStream implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7417509595514385431L;
	private long offset;
	private File file;

	public TransactionalFileInputStream(File file) {
		this.file = file;
	}

	public TransactionalFileInputStream(String file) {
		this.file = new File(file);
	}

	@Override
	public int read() throws IOException {

		FileInputStream stream = new FileInputStream(file);
		stream.skip(offset);

		int data = stream.read();
		if (data != -1) {
			offset++;
		}

		stream.close();
		return data;
	}

	@Override
	public int read(byte[] b) {
		return 0;

	}

}
