import TSim.*;
import java.util.concurrent.Semaphore;

public class Lab1 {
	static Semaphore sem1;
	final int default_sim_speed = 100;
	final int t1Speed=0;
	final int t2Speed=0;
	public static void main(String[] args) {
		sem1 = new Semaphore(1);
		new Lab1(args);
	}

	public Lab1(String[] args) {
		if(args.length > 0){
			
		}
		TSimInterface tsi = TSimInterface.getInstance();

		try {
			tsi.setSpeed(1, 50);
		} catch (CommandException e) {
			e.printStackTrace(); // or only e.getMessage() for the error
			System.exit(1);
		}
	}
}

class trainController extends Thread {
	
	public trainController() {
		// TODO Auto-generated constructor stub
	}
	
    public void run(){
        System.out.println("MyThread running");
     }
	
}
