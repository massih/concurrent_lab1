import TSim.*;

import java.util.concurrent.Semaphore;

public class Lab1 {
	final int defaultSimSpeed = 100;
	final int maxSpeed = 15;
	int simSpeed, tr1Speed, tr2Speed;
	trainController tr1, tr2;

	public static void main(String[] args) {
		new Lab1(args);
	}

	public Lab1(String[] args) {
		checkArgs(args);
		Semaphore[] semaphores = new Semaphore[7];
		for (int i = 0; i < semaphores.length; i++) {
			semaphores[i] = new Semaphore(1);
		}
		tr1 = new trainController(1, tr1Speed, semaphores);
		tr2 = new trainController(2, tr2Speed, semaphores);
		tr1.start();
		tr2.start();
	}

	void checkArgs(String args[]) {
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
	private Semaphore[] semaphores;
	private int holdingSem;
	private int semIndex = 0;
	private int sensorIndex = 0;

	AddingArrayList<Semaphore> semList;
	AddingArrayList<SensorEvent> sensorsList;

	public trainController(int id, int speed, Semaphore[] allSem) {
		trSpeed = speed;
		trId = id;
		tsi = TSimInterface.getInstance();
		tsi.setDebug(true);
		sensorsList = new AddingArrayList<>();
		semList = new AddingArrayList<>();
		semaphores = allSem;
		try {
			if (trId == 1) {
				semaphores[0].acquire();
				holdingSem = 0;
				semList.set(semIndex, semaphores[0]);
			} else {
				semaphores[6].acquire();
				holdingSem = 6;
				semList.set(semIndex, semaphores[6]);
			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		// semIndex++;
	}

	public void run() {
		try {
			tsi.setSpeed(trId, trSpeed);
			while (true) {
				startTime = System.currentTimeMillis() / 1000;
				sensorsList.set(sensorIndex, tsi.getSensor(trId));
				Semaphore next = whichSemaphore();
				int n = 0;
				while(!next.equals(semaphores[n])){
					n++;
				}
				System.err.println("chosen semaphor: "+ n+" by "+ trId);
			}
		} catch (CommandException | InterruptedException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private Semaphore whichSemaphore() {
		SensorEvent currentSensor = sensorsList.get(sensorIndex);
		Semaphore lastSem = semList.get(semIndex);
		Semaphore ret = null;
		
		if (currentSensor.getYpos() >= 11) {
			if(semIndex == 0){
				ret = semaphores[5];				
			}
//			BASED ON PREVIOUS SENSOR
		} else if (currentSensor.getYpos() <= 5) {
			if(semIndex == 0){
				ret = semaphores[1];
			}
//			BASED ON PREVIOUS SENSOR
		}else{
			int prev = 0,current =0;
			while(!semList.get(semIndex-1).equals(semaphores[prev])){
				prev++;
			}
			while(!lastSem.equals(semaphores[current])){
				current++;
			}
			if(prev<current){
				ret = semaphores[current+1];
			}else{
				ret = semaphores[current-1];
			}			
		}
		
		
		
//		if (sensorIndex == 0) {
//			if (semIndex == 0) {
//				if (currentSensor.getYpos() >= 11) {
//					ret = semaphores[5];
//				} else if (currentSensor.getYpos() <= 5) {
//					ret = semaphores[1];
//				}
//			} else {
//				if (lastSem.equals(semaphores[6])
//						|| lastSem.equals(semaphores[5])) {
//					ret = semaphores[5];
//				} else if (lastSem.equals(semaphores[0])
//						|| lastSem.equals(semaphores[1])) {
//					ret = semaphores[1];
//				}
//			}
//		} else {
//			if(semIndex == 0){
//				if(lastSem.equals(semaphores[1])){
//					ret = semaphores[2];
//				}else if(lastSem.equals(semaphores[5])){
//					ret = semaphores[4];
//				}
//			}else{
//				int prev = 0,current =0;
//				while(!semList.get(semIndex-1).equals(semaphores[prev])){
//					prev++;
//				}
//				while(!lastSem.equals(semaphores[current])){
//					current++;
//				}
//				if(prev<current){
//					ret = semaphores[current+1];
//				}else{
//					ret = semaphores[current-1];
//				}
//			}
//			
//		}
		return ret;
	}

}
