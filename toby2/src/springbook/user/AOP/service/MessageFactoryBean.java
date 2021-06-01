package springbook.user.AOP.service;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {
	String text;
	
	public void setText(String text) { //오브젝트를 생성할 떄 필요한 정보를 DI받기
		this.text = text;
	}

	@Override
	public Message getObject() throws Exception {
		
		return Message.newMessage(this.text); //실제 빈으로 사용될 오브젝트를 직접 생성
	}

	@Override
	public Class<?> getObjectType() {
		return Message.class;
	}

	@Override
	public boolean isSingleton() {//싱글톤인지 아닌지. 요청할 때마다 매번 만듦으로 false로 설정.
		return false;
	}
	

}
