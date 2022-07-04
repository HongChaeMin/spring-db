package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.ConnectException;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedAppTest {

  @Test
  void checked() {
    Controller controller = new Controller();
    assertThatThrownBy(controller::request)
        .isInstanceOf(Exception.class);
  }

  static class Controller {
    Service service = new Service();

    // 서비스, 컨트롤러에서 java.sql.SQLException 을 의존하기 때문에 문제가 된다
    public void request() throws SQLException, ConnectException { // 체크 예외를 처리하지 못해서 밖으로 던짐
      service.logic();
    }
  }

  static class Service {
    Repository repository = new Repository();
    NetworkClient networkClient = new NetworkClient();

    public void logic() throws SQLException, ConnectException { // 체크 예외를 처리하지 못해서 밖으로 던짐
      repository.call();
      networkClient.call();
    }
  }

  static class NetworkClient {
    public void call() throws ConnectException {
      throw new ConnectException("연결 실패");
    }
  }

  static class Repository {
    public void call() throws SQLException {
      throw new SQLException("ex");
    }
  }

}
