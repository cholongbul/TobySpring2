package springbook.user.AOP.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

public class ReflectionTest {
	
	@Test
	public void invokeMethod() throws Exception{
		String name = "spring";
		
		assertThat(name.length(), is(6));
		
		Method lengthMethod = String.class.getMethod("length");
		assertThat((Integer)lengthMethod.invoke(name), is(6));
		
		Method charAtMethod =String.class.getMethod("charAt", int.class);
		assertThat((Character) charAtMethod.invoke(name, 0), is('s'));
	}
	
	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));
		
//		Hello proxiedHello = new HelloUppercase(new HelloTarget());
//		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
//		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
//		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
		
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
				getClass().getClassLoader(), // �������� �����Ǵ� ���̳��� ���Ͻ� Ŭ������ �ε��� ����� Ŭ���� �δ�
				new Class[] {Hello.class}, // ������ �������̽�
				new UppercaseHandler(new HelloTarget())); //�ΰ���ɰ� ���� �ڵ带 ���� InvocationHandler
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
	assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));

	}

}
