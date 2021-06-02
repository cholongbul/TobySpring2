package springbook.user.AOP.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
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
	ApplicationContext context;

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

	List<User2> users; // 테스트 픽스처

	@Before
	public void setup() {
		users = Arrays.asList(
				new User2("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "ab1c@naver.com"), // 경계값																	// 활용
				new User2("joythouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "abc2@naver.com"),
				new User2("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1, "abc3@naver.com"),
				new User2("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "abc4@naver.com"),
				new User2("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "abc5@naver.com")

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
		
		List<User2> updated = mockUserDao.getUpdated(); //MockUserDao로부터 업데이트 결과를 가져옴
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

		User2 userWithLevel = users.get(4); // GOLD레벨
		User2 userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null); // 레벨이 비어있는 사용자

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
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));// 업그레이드가 일어났는지 확인
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));// 업그레이드가 일어나지 않았는지 확인
		}

	}
	
	private void checkUserAndLevel(User2 updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}

	@Test
	@DirtiesContext
	public void upgradeAllorNothing() throws Exception {
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setMailSender(mailSender);
		
		TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class); //테스트용 타깃 주입
		txProxyFactoryBean.setTarget(testUserService);
		UserService txUserService = 
				(UserService) txProxyFactoryBean.getObject();

		userDao.deleteAll();
		for (User2 user : users) userDao.add(user);

		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}
		checkLevelUpgraded(users.get(1), false);
	}
	
	//Mockito를 적용한 테스트 코드
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
	
		verify(mockUserDao, times(2)).update(any(User2.class));
		verify(mockUserDao, times(2)).update(any(User2.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}

	// 스태틱 내부 클래스
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

	// 스태틱 내부 클래스
	static class TestUserServiceException extends RuntimeException {
	}

	// 스태틱 내부 클래스
	static class MockUserDao implements UserDao {
		private List<User2> users; // 레벨 업그레이드 후보 User오브젝트 목록
		private List<User2> updated = new ArrayList(); // 업그레이드 대상 오브젝트를 저장해둘 목록

		private MockUserDao(List<User2> users) {
			this.users = users;
		}

		public List<User2> getUpdated() {
			return this.updated;
		}
		
		@Override
		public List<User2> getAll() {
			return this.users; // 스텁 기능 제공
		}
		
		@Override
		public void update(User2 user) {
			updated.add(user);

		}//목 오브젝트 기능 제공



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
