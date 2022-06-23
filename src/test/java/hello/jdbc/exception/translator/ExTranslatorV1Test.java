package hello.jdbc.exception.translator;

import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.USERNAME;

import hello.jdbc.domain.Member;
import hello.jdbc.exception.MyDBException;
import hello.jdbc.exception.MyDuplicateKeyException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

@Slf4j
public class ExTranslatorV1Test {

  Repository repository;
  Service service;

  @BeforeEach
  void init() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    repository = new Repository(dataSource);
    service = new Service(repository);
  }

  @Test
  void duplicateKeySave() {
    service.create("myId");
    service.create("myId"); // 같은 ID 저장 시도
  }

  @Slf4j
  @RequiredArgsConstructor
  static class Service {

    private final Repository repository;

    void create(String memberId) {
      try {
        repository.save(new Member(memberId, 0));
        log.info("saveId = {}", memberId);
      } catch (MyDuplicateKeyException e) {
        log.error("키 중복, 복구 시도");
        String retryId = generateNewId(memberId);
        log.info("retryId = {}", retryId);
        repository.save(new Member(retryId, 0));
      } catch (MyDBException e) {
        // 참고로 이 경우 여기서 예외 로그를 남기지 않아도 된다
        // 어차피 복구할 수 없는 예외는 예외를 공통으로 처리하는 부분까지 전달되기 때문이다
        // 따라서 이렇게 복구 할 수 없는 예외는 공통으로 예외를 처리하는 곳에서 예외 로그를 남기는 것이 좋다
        log.error("데이터 접근 계층 예외", e);
        throw e;
      }
    }

    private String generateNewId(String memberId) {
      return memberId + new Random().nextInt(10000);
    }

  }

  record Repository(DataSource dataSource) {

    public Member save(Member member) {
      String sql = "insert into member(member_id, money) values(?, ?)";
      Connection con = null;
      PreparedStatement pstmt = null;

      try {
        con = dataSource.getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, member.getMemberId());
        pstmt.setInt(2, member.getMoney());
        pstmt.executeUpdate();
        return member;
      } catch (SQLException e) {
        // h2 db
        if (e.getErrorCode() == 23505) {
          throw new MyDuplicateKeyException(e);
        }
        throw new MyDBException(e);
      } finally {
        JdbcUtils.closeStatement(pstmt);
        JdbcUtils.closeConnection(con);
      }
    }
  }
}
