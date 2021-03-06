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
		offset = 0;
	}

	public TransactionalFileInputStream(String file) {
		this.file = new File(file);
		offset = 0;
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
	public int read(byte[] b) throws IOException {

		FileInputStream stream = new FileInputStream(file);
		stream.skip(offset);
		int num = stream.read(b);
		stream.close();
		if (num != -1) {
			offset += num;
		}

		return num;

	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		stream.skip(offset);
		int num = stream.read(b, off, len);
		stream.close();
		if (num != -1) {
			offset += num;
		}

		return num;

	}

}
