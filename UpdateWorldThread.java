package assignment5;

public class UpdateWorldThread implements Runnable {

	@Override
	public void run() {
		Main.timerFlag=true;
		Critter.displayWorld(Main.grid);
		Main.timerFlag=false;
	}

}
