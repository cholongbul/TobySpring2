package springbook.user.AOP.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User2;

//스프링의 JdbcTemplate
public class UserDaoJdbc implements UserDao {

	

	private Map<String, String> sqlMap;
	
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}

	private JdbcTemplate jdbcTemplate;// 기존 JdbcContext에서 JdbcTemplate로

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);// 생성 및 주입

	}// 수정자 메소드 이용해서 주입

	private RowMapper<User2> userMapper = new RowMapper<User2>() {
		public User2 mapRow(ResultSet rs, int rowNum) throws SQLException {
			User2 user = new User2();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			user.setEmail(rs.getString("email"));
			return user;
		}
	};// 중복 분리

	public void add(final User2 user) {// 클라이언트 final변수

		this.jdbcTemplate.update(this.sqlMap.get("add"), // 외부 주입 받은 sql사용
				user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(),
				user.getRecommend(), user.getEmail());
	}

	public User2 get(String id) {
		return this.jdbcTemplate.queryForObject(this.sqlMap.get("get"), new Object[] { id }, this.userMapper);

	}

	public List<User2> getAll() {
		return this.jdbcTemplate.query(this.sqlMap.get("getAll"), this.userMapper);
	}

	// 데이터 모두 삭제
	public void deleteAll() {
		this.jdbcTemplate.update(this.sqlMap.get("deleteAll"));// 내장 콜백 사용
	}

	// 테이블 레코드 갯수 리턴
	public int getCount() {

		return this.jdbcTemplate.queryForInt(this.sqlMap.get("getCount"));// 위의 코드 압축버전
	}

	@Override
	public void update(User2 user) {
		this.jdbcTemplate.update(this.sqlMap.get("update"), user.getName(), user.getPassword(), user.getLevel().intValue(),
				user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());

	}

}
