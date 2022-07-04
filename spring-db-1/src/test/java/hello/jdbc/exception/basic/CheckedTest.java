package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

  @Test
  void checked_catch() {
    Service service = new Service();
    service.callCatch();
  }

  @Test
  void checked_throw() {
    Service service = new Service();
    // service.callThrow() 에서 예외를 처리하지 않고, 밖으로 던졌기 때문에 예외가 테스트 메서드까지 올라온다
    assertThatThrownBy(service::callThrow)
        .isInstanceOf(MyCheckedException.class);
  }

  /**
   * Exception 을 상속받은 예외는 체크 예외가 된다
   **/
  static class MyCheckedException extends Exception {
    public MyCheckedException(String message) {
      super(message);
    }
    // - MyCheckedException 는 Exception 을 상속받았다. Exception 을 상속받으면 체크 예외가 된다.
    // - 참고로 RuntimeException 을 상속받으면 언체크 예외가 된다. 이런 규칙은 자바 언어에서 문법으로 정한 것이다.
    // - 예외가 제공하는 여러가지 기본 기능이 있는데, 그 중에 오류 메시지를 보관하는 기능도 있다. 예제에서 보는 것 처럼
    //   생성자를 통해서 해당 기능을 그대로 사용하면 편리하다.
  }

  /**
   * Checked 예외는
   * 예외를 잡아서 처리하거나, 던지거나 둘 중 하나를 필수로 선택해야 한다
   **/
  static class Service {
    Repository repository = new Repository();

    /**
     * 예외를 잡아서 처리하는 코드
     **/
    public void callCatch() {
      try {
        repository.call();
      } catch (MyCheckedException e) {
        // 예외 처리 로직
        // service.callCatch() 에서 예외를 처리했기 때문에 테스트 메서드까지 예외가 올라오지 않는다

        // - catch 에 MyCheckedException 의 상위 타입인 Exception 을 적어주어도 MyCheckedException 을 잡을 수 있다.
        // - catch 에 예외를 지정하면 해당 예외와 그 하위 타입 예외를 모두 잡아준다.
        // - 물론 정확하게 MyCheckedException 만 잡고 싶다면 catch 에 MyCheckedException 을 적어주어야 한다
        log.error("예외 처리, message : {}", e.getMessage(), e);
      }
    }

    /**
     * 체크 예외를 밖으로 던지는 코드
     * 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws 예외를 메서드에 필수도 선언해야한다
     * @throws MyCheckedException
     **/
    public void callThrow() throws MyCheckedException {
      repository.call();
    }
  }

  static class Repository {
    public void call() throws MyCheckedException {
      throw new MyCheckedException("ex");
    }
  }

}
