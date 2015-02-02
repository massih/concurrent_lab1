import TSim.*;

import java.util.concurrent.Semaphore;

public class Lab1 {
	static Semaphore sem1;
	final int defaultSimSpeed = 100;
	final int maxSpeed = 15;
	int simSpeed,tr1Speed,tr2Speed;
	trainController tr1,tr2;
	
	public static void main(String[] args) {
		sem1 = new Semaphore(1);
		new Lab1(args);
	}

	public Lab1(String[] args) {
		checkArgs(args);
		
//		try {
//			tsi.setSpeed(1, 50);
			tr1 = new trainController(1, tr1Speed);
			tr2 = new trainController(2, 50);
			tr1.start();
			tr2.start();
//		} catch (CommandException e) {
//			e.printStackTrace(); // or only e.getMessage() for the error
//			System.exit(1);
//		}
	}
	
	void checkArgs(String args[]){
		switch (args.length) {
		case 1:
			tr1Speed = Integer.parseInt(args[0]);
			tr2Speed = maxSpeed;
			simSpeed = defaultSimSpeed;
			break;
		case 2:
			tr1Speed = Integer.parseInt(args[0]);
			tr2Speed = Integer.parseInt(args[1]);
			simSpeed = defaultSimSpeed;
			break;
		case 3:
			tr1Speed = Integer.parseInt(args[0]);
			tr2Speed = Integer.parseInt(args[1]);
			simSpeed = Integer.parseInt(args[2]);
			break;
		default:
			tr1Speed = maxSpeed;
			tr2Speed = maxSpeed;
			simSpeed = defaultSimSpeed;
			break;
		}
	}
}

class trainController extends Thread {
	private final int trSpeed;
	private final int trId;
	private long startTime;
	private TSimInterface tsi;
	private boolean direction;
	
	AddingArrayList<Integer[][]> trackList;
	AddingArrayList<SensorEvent> sensorsList;
	
	public trainController(int id, int speed){
		trSpeed = speed;
		trId = id;
		tsi = TSimInterface.getInstance();
		tsi.setDebug(true);
		trackList = new AddingArrayList<>();
		sensorsList = new AddingArrayList<>();
	}
	
    public void run() {
    	int index;
 		try {
			tsi.setSpeed(trId, trSpeed);
			while (true) {
				startTime = System.currentTimeMillis()/1000;
		    	index = 0;
				SensorEvent currentSensor = tsi.getSensor(trId);
				
				sensorsList.set(index, currentSensor);
				
				
			}
//		   	System.err.println("Hello from a thread !"+ trId+ " start at" + startTime);
		} catch (CommandException | InterruptedException e) {
//			System.err.println(e.getMessage());
			System.exit(1);
		}
     }
	
}
