package springbook.user.serviceAbstract1.dao;

public interface LineCallback<T> {
	T doSomethingWithLine(String line, T value);

}
