package springbook.user.AOP.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object> {
	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;
	Class<?> serviceInterface;
	
	public void setTarget(Object target) {
		this.target = target;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public Object getObject() throws Exception { //DI받은 정보를 이용해서 TransactionHandler를 사용하는 다이내믹 프록시 생성
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {serviceInterface}, txHandler);
	}

	@Override
	public Class<?> getObjectType() { 
		return serviceInterface;  //팩토리 빈이 생성하는 오브젝트의 타입은 DI받은 인터페이스 타입에 따라 달라진다. 따라서 다양한 타입의 프로깃 오브젝트 생성에 재사용할 수 있다.
	}

	@Override
	public boolean isSingleton() {
		return false; //싱글톤이 아니다.
	}

}
