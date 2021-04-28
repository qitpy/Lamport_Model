package RMIHandle;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {

	void status(String port, int logicClock) throws RemoteException;	
	// Handle can gi thi ben nay se cung cap chuc nang do
	
	/*
	 * @Override
	public void status(int logicClock) throws RemoteException {
		int max = (logicClock > logicNum) ? logicClock : logicNum;
		max++;
		logicNum = max;
	}
	 * */
}
