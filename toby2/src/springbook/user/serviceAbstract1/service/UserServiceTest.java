package springbook.user.serviceAbstract1.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static springbook.user.serviceAbstract1.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.serviceAbstract1.service.UserService.MIN_RECCOMEND_FOR_GOLD;
import springbook.user.domain.Level;
import springbook.user.domain.User2;
import springbook.user.serviceAbstract1.dao.UserDao;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/springbook/user/serviceAbstract1/dao/applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;
	
	@Autowired
	DataSource dataSource;
	
	List<User2> users; //테스트 픽스처
	
	
	
	@Before
	public void setup() {
		users = Arrays.asList(
				new User2("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0), //경계값 활용
				new User2("joythouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User2("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
				new User2("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
				new User2("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)

				);
				
	}
	
	@Test
	public void upgradeLevels() throws Exception {
		userDao.deleteAll();
		for(User2 user: users) userDao.add(user);
		
		userService.upgradeLevels();
		
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), false);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), false);
		checkLevelUpgraded(users.get(4), false);

		
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User2 userWithLevel = users.get(4); //GOLD레벨
		User2 userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null); //레벨이 비어있는 사용자
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User2 userWithLevelRead = userDao.get(userWithLevel.getId());
		User2 userWithoutLevelRead = userDao.get(userWithoutLevel.getId()); // DB에 저장된 결과 확인
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	private void checkLevel(User2 user, Level expectedLevel) {
		User2 userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
		
	}
	
	private void checkLevelUpgraded(User2 user, boolean upgraded) {
		User2 userUpdate = userDao.get(user.getId());
		if(upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));//업그레이드가 일어났는지 확인
		}
		else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));//업그레이드가 일어나지 않았는지 확인
		}
		
	}
	
	@Test
	public void upgradeAllorNothing() throws Exception {
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setDataSource(this.dataSource);
		userDao.deleteAll();
		for(User2 user : users) userDao.add(user);
		
		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		}
		catch (TestUserServiceException e) {
		}
		checkLevelUpgraded(users.get(1), false);
	}

	static class TestUserService extends UserService {
		private String id;

		private TestUserService(String id) {
			this.id = id;

		}
		
		@Override
		protected void upgradeLevel(User2 user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}

	}
	
	static class TestUserServiceException extends RuntimeException {
	}


}
