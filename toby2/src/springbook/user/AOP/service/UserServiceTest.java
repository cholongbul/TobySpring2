package springbook.user.AOP.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import static springbook.user.AOP.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.AOP.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import springbook.user.domain.Level;
import springbook.user.domain.User2;
import springbook.user.AOP.dao.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/springbook/user/AOP/dao/applicationContext.xml")
public class UserServiceTest {

	@Autowired
	UserService userService;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	UserDao userDao;

	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	MailSender mailSender;

	List<User2> users; // �׽�Ʈ �Ƚ�ó

	@Before
	public void setup() {
		users = Arrays.asList(
				new User2("bumjin", "�ڹ���", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "ab1c@naver.com"), // ��谪																	// Ȱ��
				new User2("joythouch", "����", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "abc2@naver.com"),
				new User2("erwins", "�Ž���", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1, "abc3@naver.com"),
				new User2("madnite1", "�̻�ȣ", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "abc4@naver.com"),
				new User2("green", "���α�", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "abc5@naver.com")

		);

	}

	@Test
	public void upgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevels();
		
		List<User2> updated = mockUserDao.getUpdated(); //MockUserDao�κ��� ������Ʈ ����� ������
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "joythouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

		List<String> request = mockMailSender.getRequest();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));

	}

	@Test
	public void add() {
		userDao.deleteAll();

		User2 userWithLevel = users.get(4); // GOLD����
		User2 userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null); // ������ ����ִ� �����

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		User2 userWithLevelRead = userDao.get(userWithLevel.getId());
		User2 userWithoutLevelRead = userDao.get(userWithoutLevel.getId()); // DB�� ����� ��� Ȯ��

		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}

	private void checkLevel(User2 user, Level expectedLevel) {
		User2 userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));

	}

	private void checkLevelUpgraded(User2 user, boolean upgraded) {
		User2 userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));// ���׷��̵尡 �Ͼ���� Ȯ��
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));// ���׷��̵尡 �Ͼ�� �ʾҴ��� Ȯ��
		}

	}
	
	private void checkUserAndLevel(User2 updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}

	@Test
	public void upgradeAllorNothing() throws Exception {
		UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setMailSender(mailSender);

		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);

		userDao.deleteAll();
		for (User2 user : users) userDao.add(user);

		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}
		checkLevelUpgraded(users.get(1), false);
	}

	// ����ƽ ���� Ŭ����
	static class TestUserService extends UserServiceImpl {
		private String id;

		private TestUserService(String id) {
			this.id = id;

		}

		@Override
		protected void upgradeLevel(User2 user) {
			if (user.getId().equals(this.id))
				throw new TestUserServiceException();
			super.upgradeLevel(user);
		}

	}

	// ����ƽ ���� Ŭ����
	static class TestUserServiceException extends RuntimeException {
	}

	// ����ƽ ���� Ŭ����
	static class MockUserDao implements UserDao {
		private List<User2> users; // ���� ���׷��̵� �ĺ� User������Ʈ ���
		private List<User2> updated = new ArrayList(); // ���׷��̵� ��� ������Ʈ�� �����ص� ���

		private MockUserDao(List<User2> users) {
			this.users = users;
		}

		public List<User2> getUpdated() {
			return this.updated;
		}
		
		@Override
		public List<User2> getAll() {
			return this.users; // ���� ��� ����
		}
		
		@Override
		public void update(User2 user) {
			updated.add(user);

		}//�� ������Ʈ ��� ����



		@Override
		public void add(User2 user) {
			throw new UnsupportedOperationException();

		}

		@Override
		public User2 get(String id) {
			throw new UnsupportedOperationException();

		}

		
		@Override
		public void deleteAll() {
			throw new UnsupportedOperationException();

		}

		@Override
		public int getCount() {
			throw new UnsupportedOperationException();

		}

		
	}

}
