package springbook.user.serviceAbstract1.service;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.domain.Level;
import springbook.user.domain.User2;
import springbook.user.serviceAbstract1.dao.UserDao;

public class UserService {
	
	private DataSource dataSource;
	
	UserDao userDao;
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
//	public void upgradeLevels() {
//		List<User2> users = userDao.getAll();
//		for(User2 user : users) {
//			Boolean changed = null;
//			if(user.getLevel() == Level.BASIC && user.getLogin()>=50) {
//				user.setLevel(Level.SILVER);
//				changed = true;
//			}
//			else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
//				user.setLevel(Level.GOLD);
//				changed = true;
//			}
//			else if (user.getLevel() == Level.GOLD) { changed = false;}
//			else {changed = false;}
//			
//			if(changed) {userDao.update(user);}
//		}
//	}
	
	//∏Æ∆—≈‰∏µ
	public void upgradeLevels() throws Exception {
		TransactionSynchronizationManager.initSynchronization();
		Connection c = DataSourceUtils.getConnection(dataSource);
		c.setAutoCommit(false);
		try {
		List<User2> users = userDao.getAll();
		for(User2 user : users) {
			if(canUpgradeLevel(user) ) {
				upgradeLevel(user);
			}
		}
		} catch (Exception e) {
			c.rollback();
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(c, dataSource);
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}
		
	}
	
	private boolean canUpgradeLevel(User2 user) {
		Level currentLevel = user.getLevel();
		switch(currentLevel) {
		case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
		case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
		case GOLD: return false;
		default: throw new IllegalArgumentException("Unkonwn Level: " + currentLevel);
		}
	}
	
	protected void upgradeLevel(User2 user) {
		user.upgradeLevel();
		userDao.update(user);
	}
	
	public void add(User2 user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}

}
