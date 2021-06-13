package springbook.user.AOP.sqlservice;

public interface SqlService {
	String getSql(String key) throws SqlRetrievalFailureException;
}
