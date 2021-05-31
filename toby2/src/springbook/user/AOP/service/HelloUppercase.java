package springbook.user.AOP.service;

public class HelloUppercase implements Hello {
	Hello hello; //Ÿ�� ������Ʈ, �ٸ� ���Ͻø� �߰��� ���� ������ �������̽��� ����
	
	public HelloUppercase(Hello hello) {
		this.hello = hello;
	}

	@Override
	public String sayHello(String name) {
		return hello.sayHello(name).toUpperCase();
	}

	@Override
	public String sayHi(String name) {
		return hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankYou(String name) {
		return hello.sayThankYou(name).toUpperCase();
	}

}
