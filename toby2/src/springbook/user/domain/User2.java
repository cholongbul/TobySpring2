package springbook.user.domain;

public class User2 {
	
	String id;
	String name;
	String password;
//	private static final int BASIC = 1;
//	private static final int SILVER = 2;
//	private static final int GOLD = 3;
	//int level;
//	
//	public void setLevel(int level) {
//		this.level = level;
//	} 타입을 int로 해서 다른 종류의 정보를 넣는 실수가 일어날 수 있다. 따라서 enum을 이용해는 게 안전하다
	
	Level level;
	int login;
	int recommend;
	
	public void upgradeLevel() {
		Level nextLevel = this.level.nextLevel();
		if(nextLevel == null) {
			throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
		}
		else {
			this.level = nextLevel;
		}
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public User2(String id, String name, String password, Level level, int login, int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login =login;
		this.recommend = recommend;
	}
	
	public User2(){
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
