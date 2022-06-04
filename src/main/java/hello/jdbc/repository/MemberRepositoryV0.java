package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.DBConnectionUtil.getConnection;

/**
 * JDBC - DriverManager 사용
 **/
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        // 이것두 되는데 권장 X
        // String sql = "insert into member(member_id, money) values (" + member.getMemberId() + ", " + member.getMemberMoney() + ")";;
        String sql = "insert into member(member_id, money) values (?, ?)"; // 데이터베이스에 전달할 SQL을 정의

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection(); // 이전에 만들어둔 DBConnectionUtil 를 통해서 데이터베이스 커넥션을 획득
            preparedStatement = connection.prepareStatement(sql); // 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate(); // 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달
            // 참고로 executeUpdate() 은 int 를 반환하는데 영향받은 DB row 수를 반환
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);

            // 데이터를 변경할 때는 executeUpdate() 를 사용하지만, 데이터를 조회할 때는 executeQuery() 를 사용한다
            // executeQuery() 는 결과를 ResultSet 에 담아서 반환
            resultSet = preparedStatement.executeQuery();

            // ResultSet 내부에 있는 커서( cursor )를 이동해서 다음 데이터를 조회
            // rs.next() : 이것을 호출하면 커서가 다음으로 이동한다
            //             참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에 rs.next() 를 최초 한번은 호출해야 데이터를 조회
            if (resultSet.next()) {
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId : " + memberId);
            }
        } catch (SQLException e) {
            log.error("db error {}", e);
            throw e;
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    // 리소스 정리
    // 쿼리를 실행하고 나면 리소스를 정리해야 한다. 여기서는 Connection , PreparedStatement 를사용했다. 리소스를 정리할 때는 항상 역순으로 해야한다
    // Connection 을 먼저 획득하고 Connection 을 통해 PreparedStatement 를 만들었기 때문에 리소스를 반환할 때는 PreparedStatement 를
    // 먼저 종료하고, 그 다음에 Connection 을 종료하면 된다. 참고로 여기서 사용하지 않은 ResultSet 은 결과를
    // 조회할 때 사용한다. 조금 뒤에 조회 부분에서 알아보자
    private void close(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

}