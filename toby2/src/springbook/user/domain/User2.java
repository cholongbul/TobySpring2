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
//	} Ÿ���� int�� �ؼ� �ٸ� ������ ������ �ִ� �Ǽ��� �Ͼ �� �ִ�. ���� enum�� �̿��ش� �� �����ϴ�
	
	Level level;
	int login;
	int recommend;
	


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
		//�ڹٺ� �Ծ࿡ ������ Ŭ������ �����ڸ� ���������� �߰����� ���� �Ķ���Ͱ� ���� ����Ʈ �����ڵ� �������ش�
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