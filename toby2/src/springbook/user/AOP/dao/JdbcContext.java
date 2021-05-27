package springbook.user.AOP.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcContext {
	
private DataSource dataSource;//DB커넥션 기능을 가진 제공되는 인터페이스 DataSource인터페이스로 변환
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}//수정자 메소드 이용해서 주입
	

	//try/catch/finally컨텍스트 코드 메소드로 분리
	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException{
		Connection c = null;
		PreparedStatement ps = null;
		
		try {
			 c= dataSource.getConnection();
			 ps = stmt.makePreparedStatement(c);
			 ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if ( ps != null) {try {ps.close();} catch (SQLException e) {}}
			if ( ps != null) {try {c.close();} catch (SQLException e) {}}
		}
		
	}
	
	//변하지 않는 분리해서 jdbccontext로 옮김
	public void executeSql(final String query) throws SQLException {//분리해서 재활용 가능
		workWithStatementStrategy(// 템플릿
				new StatementStrategy() {// 콜백 인터페이스

					@Override
					public PreparedStatement makePreparedStatement(Connection c) throws SQLException {// 콜백 메소드
						PreparedStatement ps = c.prepareStatement(query);
						return ps;
					}
				}); // 컨텍스트 호출 전략 오브젝트 전달
	}
	
	
	

}
