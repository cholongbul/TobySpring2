package springbook.user.AOP.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class DynamicProxyTest {


	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UppercaseAdvice());

		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
		
	}
	
	@Test
	public void pointcutAdvisor() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*"); //�̸� ������ ����. sayH�� �����ϴ� ��� �޼ҵ带 �����ϰ� �Ѵ�.
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed(); //���÷����� Method�� �޸� �޼ҵ� ���� �� Ÿ�� ������Ʈ�� ������ �ʿ䰡 ����.
			return ret.toUpperCase();
		}
	}
	
	@Test
	public void classNamePointcutAdvisor() {
		//����Ʈ �� �غ�
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			public ClassFilter getClassFilter() {//�͸� ���� Ŭ����
				return new ClassFilter() {
					
					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");//Ŭ���� �̸��� HelloT�� �����ϴ� �͸�
					}
					
				};
				
			}
		};
		classMethodPointcut.setMappedName("sayH*");//�޼ҵ� ����
		
		//�׽�Ʈ
		checkAdviced(new HelloTarget(), classMethodPointcut, true); //���� Ŭ����
		
		class HelloWorld extends HelloTarget{};//������ Ŭ����
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		class HelloToby extends HelloTarget{}; //���� Ŭ����
		checkAdviced(new HelloToby(), classMethodPointcut, true);
		
	}
	
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) { 
			ProxyFactoryBean pfBean= new ProxyFactoryBean();
			pfBean.setTarget(target);
			pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
			Hello proxiedHello = (Hello) pfBean.getObject();
			
			
			if (adviced) { //�����̽� ����
				assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
				assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
				assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
			}
			else { //������
				assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
				assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
				assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
			}
	}

}
