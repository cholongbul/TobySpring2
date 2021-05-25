package springbook.user.serviceAbstract1.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.Level;
import springbook.user.domain.User2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/springbook/user/serviceAbstract1/dao/applicationContext.xml")
public class UserTest {

	User2 user;

	@Before
	public void setUp() {
		user = new User2();
	}

	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for (Level level : levels) {
			if (level.nextLevel() == null)
				continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));

		}
	}

	@Test(expected = IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		for (Level level : levels) {
			if (level.nextLevel() != null)
				continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
	

}
