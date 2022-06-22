package hello.jdbc.exception.basic;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedAppTest {

  @Test
  void unchecked() {
    Controller controller = new Controller();
    Assertions.assertThatThrownBy(controller::request)
        .isInstanceOf(Exception.class);
  }

  @Test
  void printEx() {
    Controller controller = new Controller();
    try {
      controller.request();
    } catch (Exception e) {
      log.error("ex", e); // 로그를 출력할 때 마지막 파라미터에 예외를 넣어주면 로그에 스택 트레이스를 출력

      // System.out 에 스택 트레이스를 출력하려면 e.printStackTrace() 를 사용하면 된다
      // - 실무에서는 항상 로그를 사용해야 한다는 점을 기억
    }
  }

  static class Controller {
    Service service = new Service();

    public void request() { // 컨트롤러와 서비스에서 해당 예외에 대한 의존 관계가 발생하지 않음
      service.logic();
    }
  }

  static class Service {
    Repository repository = new Repository();
    NetworkClient networkClient = new NetworkClient();

    public void logic() { // 컨트롤러와 서비스에서 해당 예외에 대한 의존 관계가 발생하지 않음
      repository.call();
      networkClient.call();
    }
  }

  static class NetworkClient {
    public void call() {
      // NetworkClient 는 단순히 기존 체크 예외를 RuntimeConnectException 이라는 런타임 예외가 발생
      throw new RuntimeConnectException("fail connect");
    }
  }

  static class Repository {
    public void call() {
      try {
        runSQL();
      } catch (SQLException e) {
        // 체크 예외인 SQLException 이 발생하면 런타임 예외인 RuntimeSQLException 으로 전환해서 예외
        // 참고로 이때 기존 예외를 포함해주어야 예외 출력시 스택 트레이스에서 기존 예외도 함께 확인
        throw new RuntimeSQLException(e);
        // throw new RuntimeSQLException(); -> 기존 예외 미포함
      }
    }

    private void runSQL() throws SQLException {
      throw new SQLException("ex");
    }
  }

  // 대부분 복구 불가능한 예외 : 런타임 예외를 사용하면 서비스나 컨트롤러가 이런 복구 불가능한 예외를 신경쓰지 않아도 된다
  // 의존 관계에 대한 문제 : 런타임 예외는 해당 객체가 처리할 수 없는 예외는 무시

  static class RuntimeConnectException extends RuntimeException {
    public RuntimeConnectException(String message) {
      super(message);
    }
  }

  static class RuntimeSQLException extends RuntimeException {
    public RuntimeSQLException(Throwable cause) {
      super(cause);
    }

    public RuntimeSQLException() {
    }
  }

}
