import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3557579291611635226L;

	private String file_name;
	
	private Boolean append;
	
//	private int offset;
	
	private Boolean firstime;
	
	public TransactionalFileOutputStream(String file_name, Boolean Append){
		this.file_name = file_name;
		this.append = Append;
//		this.offset = 0;
		this.firstime = true;
	}
	
	@Override
	public void write(int b) throws IOException {
		FileOutputStream output;
		if (this.firstime) {
			 output = new FileOutputStream(this.file_name, this.append);
		}
		else{
			 output = new FileOutputStream(this.file_name, true);
		}
		output.write(b);
		output.flush();
		output.close();
//		this.offset += 1;
		this.firstime = false;
	}
	
	@Override
	public void write(byte[] b) throws IOException{
		FileOutputStream output;
		if (this.firstime) {
			 output = new FileOutputStream(this.file_name, this.append);
		}
		else{
			 output = new FileOutputStream(this.file_name, true);
			 
		}
		output.write(b);
		output.flush();
		output.close();
//		this.offset += b.length;
		this.firstime = false;
	}
	@Override
	public void write(byte[] b, int off, int len) throws IOException{
		FileOutputStream output;
		if (this.firstime) {
			output = new FileOutputStream(this.file_name, this.append);
		}
		else{
			output = new FileOutputStream(this.file_name, this.append);
		}
		output.write(b, off, len);
		output.flush();
		output.close();
//		this.offset += off;
		this.firstime = false;
	}
	
	public void flush() throws IOException {
		return;
	}

	public void close() throws IOException {
		return;
	}


}
