package springbook.user.AOP.service;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {
	String text;
	
	public void setText(String text) { //������Ʈ�� ������ �� �ʿ��� ������ DI�ޱ�
		this.text = text;
	}

	@Override
	public Message getObject() throws Exception {
		
		return Message.newMessage(this.text); //���� ������ ���� ������Ʈ�� ���� ����
	}

	@Override
	public Class<?> getObjectType() {
		return Message.class;
	}

	@Override
	public boolean isSingleton() {//�̱������� �ƴ���. ��û�� ������ �Ź� �������� false�� ����.
		return false;
	}
	

}
