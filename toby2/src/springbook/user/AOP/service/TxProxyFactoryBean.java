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
	public Object getObject() throws Exception { //DI���� ������ �̿��ؼ� TransactionHandler�� ����ϴ� ���̳��� ���Ͻ� ����
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {serviceInterface}, txHandler);
	}

	@Override
	public Class<?> getObjectType() { 
		return serviceInterface;  //���丮 ���� �����ϴ� ������Ʈ�� Ÿ���� DI���� �������̽� Ÿ�Կ� ���� �޶�����. ���� �پ��� Ÿ���� ���α� ������Ʈ ������ ������ �� �ִ�.
	}

	@Override
	public boolean isSingleton() {
		return false; //�̱����� �ƴϴ�.
	}

}