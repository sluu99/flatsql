package flatsql.exceptions;

public class TypeNotRegisteredException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private Class<?> unregisteredType = null;
	
	public TypeNotRegisteredException(Class<?> type) {
		this.unregisteredType = type;
	}
	
	public Class<?> getUnregisteredType() {
		return this.unregisteredType;
	}
}
