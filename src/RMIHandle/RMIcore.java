package RMIHandle;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class RMIcore extends UnicastRemoteObject implements RMIInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected RMIcore() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public abstract void status(String port,int logicClock) throws RemoteException;
	public abstract void init() throws RemoteException;
}
