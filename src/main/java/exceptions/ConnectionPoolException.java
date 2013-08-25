package exceptions;

public class ConnectionPoolException extends Exception {
	private static final long serialVersionUID = 1L;

	public ConnectionPoolException(String msg) {
		super(msg);
	}
}
