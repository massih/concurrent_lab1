import TSim.*;
import java.util.concurrent.Semaphore;

public class Lab1 {
	static Semaphore sem1;
	
	
	public static void main(String[] args) {
		sem1 = new Semaphore(1);
		new Lab1(args);
	}

	public Lab1(String[] args) {
		TSimInterface tsi = TSimInterface.getInstance();

		try {
			tsi.setSpeed(1, 50);
		} catch (CommandException e) {
			e.printStackTrace(); // or only e.getMessage() for the error
			System.exit(1);
		}
	}
}
