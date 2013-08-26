package flatsql.exceptions;

public class TypeNotRegisteredException extends Exception {
	
	private Class<?> unregisteredType = null;
	
	public TypeNotRegisteredException(Class<?> type) {
		this.unregisteredType = type;
	}
	
	public Class<?> getUnregisteredType() {
		return this.unregisteredType;
	}
}
