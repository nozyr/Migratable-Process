import java.io.Serializable;

public interface MigratableProcess extends Runnable, Serializable {

	public void suspend(Thread thd);
	
	public void run();
}
