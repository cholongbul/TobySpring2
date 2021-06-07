package springbook.user.AOP.service;

import java.util.List;

import springbook.user.domain.User2;

public interface UserService {
	void add(User2 user);
	User2 get(String id);
	List<User2> getAll();
	void deleteAll();
	void update(User2 user);
	
	void upgradeLevels();

}
