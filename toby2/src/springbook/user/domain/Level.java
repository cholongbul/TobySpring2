package springbook.user.domain;

public enum Level {
	BASIC(1), SILVER(2), GOLD(3);
	private int value;
	Level(int value){
		this.value = value;
	}
	public int intValue() {//값을 가져오는 메소드
		return value;
	}
	
	public static Level valueOf(int value) {
		switch(value) {
		case 1: return BASIC;
		case 2: return SILVER;
		case 3: return GOLD;
		default: throw new AssertionError("Unknown value: " + value);
		}
	}
}
