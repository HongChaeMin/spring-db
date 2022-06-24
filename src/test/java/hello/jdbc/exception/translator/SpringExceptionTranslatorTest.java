package hello.jdbc.exception.translator;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

@Slf4j
public class SpringExceptionTranslatorTest {

  DataSource dataSource;

  @BeforeEach
  void init() {
    dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
  }

  @Test
  void sqlExceptionErrorCode() {
    String sql = "select bad grammer";

    try {
      Connection con = dataSource.getConnection();
      PreparedStatement stmt = con.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (SQLException e) {
      assertThat(e.getErrorCode()).isEqualTo(42122);
      int errorCode = e.getErrorCode();
      log.error("errorCode = {}", errorCode);
      log.error("error", e);

      // 이전에 살펴봤던 SQL ErrorCode를 직접 확인하는 방법이다. 이렇게 직접 예외를 확인하고 하나하나
      // 스프링이 만들어준 예외로 변환하는 것은 현실성이 없다. 이렇게 하려면 해당 오류 코드를 확인하고
      // 스프링의 예외 체계에 맞추어 예외를 직접 변환해야 할 것이다. 그리고 데이터베이스마다 오류 코드가
      // 다르다는 점도 해결해야 한다
    }
  }

  @Test
  void exceptionTranslator() {
    String sql = "select bad grammer";

    try {
      Connection con = dataSource.getConnection();
      PreparedStatement stmt = con.prepareStatement(sql);
      stmt.executeQuery();
    } catch (SQLException e) {
      assertThat(e.getErrorCode()).isEqualTo(42122);

      // 스프링이 제공하는 SQL 예외 변환기
      SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
      DataAccessException resultEx = exTranslator.translate("select", sql, e);
      log.info("resultEx", resultEx);
      assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
      // org.springframework.jdbc.support.sql-error-codes.xml
      // 스프링 SQL 예외 변환기는 SQL ErrorCode를 이 파일에 대입해서 어떤 스프링 데이터 접근 예외로
      //전환해야 할지 찾아낸다
    }
  }

}
