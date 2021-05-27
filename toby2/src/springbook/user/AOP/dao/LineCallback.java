package springbook.user.AOP.dao;

public interface LineCallback<T> {
	T doSomethingWithLine(String line, T value);

}
