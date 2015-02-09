import TSim.*;

import java.util.concurrent.Semaphore;

public class Lab1 {
	final static int defaultSimSpeed = 100;
	final int maxSpeed = 20;
	int simSpeed, tr1Speed, tr2Speed;
	trainController tr1, tr2;

	public static void main(String[] args) {
		new Lab1(args);
	}

	public Lab1(String[] args) {
		checkArgs(args);
		Semaphore[] semaphores = new Semaphore[6];
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
			System.err.println(" PARAMETERS ******  " + args[0] + " - "
					+ args[1] + " - " + args[2]);
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
	private final int RIGHT = TSimInterface.SWITCH_RIGHT;
	private final int LEFT = TSimInterface.SWITCH_LEFT;
	private final int ACTIVE = SensorEvent.ACTIVE;
	private final int INACTIVE = SensorEvent.INACTIVE;
	private int trSpeed;
	private final int trId;
	private TSimInterface tsi;
//	private boolean alterRoute;
	private Semaphore[] semaphores;
	private int sensorIndex = -1;

	AddingArrayList<SensorEvent> sensorsList;

	public trainController(int id, int speed, Semaphore[] allSem) {
		trSpeed = speed;
		trId = id;
		tsi = TSimInterface.getInstance();
		tsi.setDebug(true);
		sensorsList = new AddingArrayList<>();
		semaphores = allSem;
		try {
			if (trId == 1) {
				semaphores[0].acquire();
			} else {
				semaphores[5].acquire();
			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}

	public void run() {
		try {
			tsi.setSpeed(trId, trSpeed);
			while (true) {

				SensorEvent sen = tsi.getSensor(trId);
				if (sen.getStatus() == ACTIVE) {
					sensorIndex++;
					sensorsList.set(sensorIndex, sen);
					whichSemaphore();
				}

			}
		} catch (CommandException | InterruptedException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private int whichSemaphore() throws InterruptedException, CommandException {
		SensorEvent currentSensor = sensorsList.get(sensorIndex);
		int ret = -1;
		if (sensorIndex > 1) {
//			System.err.println("************HERE************" + trId);
			SensorEvent prevSen = sensorsList.get(sensorIndex - 1);
			switch (currentSensor.getXpos()) {
			case 14:
				switch (currentSensor.getYpos()) {
				case 3:
				case 5:
					if (prevSen.getXpos() == 8 || prevSen.getXpos() == 6) {
						stopTrain();
					}
					break;
				case 11:
				case 13:
					if (prevSen.getXpos() == 6) {
						stopTrain();
					}
					break;
				default:
					System.exit(0);
					break;
				}

				break;
			case 12:
				if (prevSen.getXpos() == 19) {
					semaphores[2].release();
					waitForSem(1);
				} else {
					semaphores[1].release();
					waitForSem(2);
					if (currentSensor.getYpos() == 7) {
						tsi.setSwitch(17, 7, RIGHT);
					} else {
						tsi.setSwitch(17, 7, LEFT);
					}
				}

				break;
			case 10:
				if(prevSen.getXpos() == 1){
					semaphores[4].release();
					waitForSem(2);
					if(currentSensor.getYpos() == 9){
						tsi.setSwitch(15, 9, RIGHT);
					}else{
						tsi.setSwitch(15, 9, LEFT);
					}
				}else{
					semaphores[2].release();
					waitForSem(4);
					if(currentSensor.getYpos() == 9){
						tsi.setSwitch(4, 9, LEFT);
					}else{
						tsi.setSwitch(4, 9, RIGHT);
					}
				}
				break;
			case 6:
				if(currentSensor.getYpos() == 5){
					if(prevSen.getXpos() == 14){
						waitForSem(1);
					}else{
						semaphores[1].release();
					}
				}else{
					if(prevSen.getXpos() == 14){
						waitForSem(4);
						if(currentSensor.getYpos() == 11){
							tsi.setSwitch(3, 11, LEFT);
						}else{
							tsi.setSwitch(3, 11, RIGHT);
						}
					}else{
						semaphores[4].release();
					}
				}
				break;
			case 8:
				if(prevSen.getXpos() == 14){
					waitForSem(1);
				}else{
					semaphores[1].release();
				}
				break;
			case 1:
				if(prevSen.getXpos() == 10){
					if(prevSen.getYpos() == 9){
						semaphores[3].release();
					}
					if(semaphores[5].tryAcquire()){
						tsi.setSwitch(3, 11, LEFT);
					}else{
						tsi.setSwitch(3, 11, RIGHT);
					}
				}else{
					if(prevSen.getYpos() == 11){
						semaphores[5].release();
					}
					if(semaphores[3].tryAcquire()){
						tsi.setSwitch(4, 9, LEFT);
					}else{
						tsi.setSwitch(4, 9, RIGHT);
					}
				}
				break;
			case 19:
				if(prevSen.getXpos() == 12){
					if(prevSen.getYpos() == 7){
						semaphores[0].release();
					}
					if(semaphores[3].tryAcquire()){
						tsi.setSwitch(15, 9, RIGHT);
					}else{
						tsi.setSwitch(15, 9, LEFT);
					}
				}else{
					if(prevSen.getYpos() == 9){
						semaphores[3].release();
					}
					if(semaphores[0].tryAcquire()){
						tsi.setSwitch(17, 7, RIGHT);
					}else{
						tsi.setSwitch(17, 7, LEFT);
					}
				}
				break;
			default:
				break;
			}
		}
		return ret;
	}

	private void stopTrain() throws CommandException, InterruptedException {
		tsi.setSpeed(trId, 0);
		sleep(2 * Lab1.defaultSimSpeed * Math.abs(trSpeed));
		trSpeed = -1 * trSpeed;
		tsi.setSpeed(trId, trSpeed);
	}

	private void waitForSem(int i) throws CommandException,
			InterruptedException {
		tsi.setSpeed(trId, 0);
		semaphores[i].acquire();
		System.err.println("*********************acquired :" + i + " id: "+ trId);
		tsi.setSpeed(trId, trSpeed);
	}
}
