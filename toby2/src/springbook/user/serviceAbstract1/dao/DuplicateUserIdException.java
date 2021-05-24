package springbook.user.serviceAbstract1.dao;

public class DuplicateUserIdException extends RuntimeException {
	public DuplicateUserIdException(Throwable cause) {
		super(cause);
	}

}
