package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.exception.MyDBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * 예외 누수 문제 해결
 * 체크 예외를 런타임 예외로 변경
 * MemberRepository 인터페이스 사용
 * throws SQLException 제거
 **/
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV4_1 implements MemberRepository {

    private final DataSource dataSource;

    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?, ?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate();
            return member;
        } catch (SQLException e) {
            // 잘 보면 기존 예외를 생성자를 통해서 포함하고 있는 것을 확인
            // 예외는 원인이 되는 예외를 내부에 포함할 수 있는데, 꼭 이렇게 작성
            // 예외를 출력했을 때 원인이 되는 기존 예외를 스택 트레이스를 통해 함께 확인
            throw new MyDBException(e);
        } finally {
            close(connection, preparedStatement, null);
        }

    }

    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " +  memberId);
            }
        } catch (SQLException e) {
            throw new MyDBException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    public void update(String memberId, int money) {
        String sql = "update member set money = ? where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new MyDBException(e);
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new MyDBException(e);
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = {}, class = {}", con, con.getClass());
        return con;
    }

}
