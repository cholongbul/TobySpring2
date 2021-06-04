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
		pointcut.setMappedName("sayH*"); //이름 비교조건 설정. sayH로 시작하는 모든 메소드를 선택하게 한다.
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed(); //리플렉션의 Method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요가 없다.
			return ret.toUpperCase();
		}
	}
	
	@Test
	public void classNamePointcutAdvisor() {
		//포인트 컷 준비
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			public ClassFilter getClassFilter() {//익명 내부 클래스
				return new ClassFilter() {
					
					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");//클래스 이름이 HelloT로 시작하는 것만
					}
					
				};
				
			}
		};
		classMethodPointcut.setMappedName("sayH*");//메소드 선정
		
		//테스트
		checkAdviced(new HelloTarget(), classMethodPointcut, true); //적용 클래스
		
		class HelloWorld extends HelloTarget{};//미적용 클래스
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		class HelloToby extends HelloTarget{}; //적용 클래스
		checkAdviced(new HelloToby(), classMethodPointcut, true);
		
	}
	
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) { 
			ProxyFactoryBean pfBean= new ProxyFactoryBean();
			pfBean.setTarget(target);
			pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
			Hello proxiedHello = (Hello) pfBean.getObject();
			
			
			if (adviced) { //어드바이스 적용
				assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
				assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
				assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
			}
			else { //미적용
				assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
				assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
				assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
			}
	}

}
