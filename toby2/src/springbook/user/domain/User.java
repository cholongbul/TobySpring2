package springbook.user.domain;

public class User {
	
	String id;
	String name;
	String password;




	public User(String id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}
	
	public User(){
		//자바빈 규약에 따르는 클래스에 생성자를 명시적으로 추가했을 때는 파라미터가 없는 디폴트 생성자도 정의해준다
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
