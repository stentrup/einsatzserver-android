package net.tentrup.einsatzserver.comm;

import android.content.Context;

public class CommunicatorSingleton {

	private static Communicator communicator = null;

	public static Communicator getCommunicator(Context context) {
		if (communicator == null) {
			communicator = new Communicator(context);
		}
		return communicator;
	}

	/**
	 * The {@link Communicator} can be replaced for testing purposes.
	 *
	 * @param communicator
	 */
	public static void setCommunictor(Communicator communicator) {
		CommunicatorSingleton.communicator = communicator;
	}
}
