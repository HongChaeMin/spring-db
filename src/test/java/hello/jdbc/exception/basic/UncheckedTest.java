package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

  @Test
  void unchecked_catch() {
    Service service = new Service();
    service.callCatch();
  }

  @Test
  void unchecked_throw() {
    Service service = new Service();
    assertThatThrownBy(service::callThrow)
        .isInstanceOf(MyUncheckedException.class);
  }

  /**
   * RuntimeException을 상속받은 예외는 언체크 예외가 된다
   **/
  static class MyUncheckedException extends RuntimeException {
    public MyUncheckedException(String message) {
      super(message);
    }
  }

  /**
   * UnChecked 예외는
   * 예외를 잡거나, 던지지 않아도 된다
   * 예외를 잡지 않으면 자동으로 밖으로 던진다
   **/
  static class Service {
    Repository repository = new Repository();

    /**
     * 필요한 경우 예외를 잡아서 처리하면 된다
     **/
    public void callCatch() {
      try {
        repository.call();
      } catch (MyUncheckedException e) {
        // 예외 체크 로직
        log.error("예외 처리, message = {}", e.getMessage(), e);
      }
    }

    /**
     * 예외를 잡지 않아도 된다 자연스럽게 상위로 넘어간다
     * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다
     **/
    public void callThrow() {
      repository.call();
      // - 언체크 예외는 체크 예외와 다르게 throws 예외 를 선언하지 않아도 된다
      // - 말 그대로 컴파일러가 이런 부분을 체크하지 않기 때문에 언체크 예외이다
    }
  }

  static class Repository {
    public void call() {
      throw new MyUncheckedException("ex");
    }
  }

}
