package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class MemberServiceTest {

  @Autowired
  MemberService memberService;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  LogRepository logRepository;

  /**
   * MemberService    @Transactional: OFF
   * MemberRepository @Transactional: ON
   * LogRepository    @Transactional: ON
   **/
  @Test
  void outerTxOff_success() {
    // given
    String userName = "outerTxOff_success";

    // when
    memberService.joinV1(userName);

    // then
    assertTrue(memberRepository.find(userName).isPresent());
    assertTrue(logRepository.find(userName).isPresent());
  }

  /**
   * MemberService    @Transactional: OFF
   * MemberRepository @Transactional: ON
   * LogRepository    @Transactional: ON Exception
   **/
  @Test
  void outerTxOff_fail() {
    // given
    String userName = "로그 예외_outerTxOff_fail";

    // when
    assertThatThrownBy(() -> memberService.joinV1(userName))
        .isInstanceOf(RuntimeException.class);

    // then : 완전히 롤백되지 않고, member 데이터가 남아서 저장된다 (각각 다른 connection)
    assertTrue(memberRepository.find(userName).isPresent());
    assertTrue(logRepository.find(userName).isEmpty());
  }

  /**
   * MemberService    @Transactional: ON
   * MemberRepository @Transactional: OF
   * LogRepository    @Transactional: OF
   **/
  @Test
  void singleTx() {
    // given
    String userName = "singleTx";

    // when
    memberService.joinV1(userName);

    // then
    assertTrue(memberRepository.find(userName).isPresent());
    assertTrue(logRepository.find(userName).isPresent());
  }

  /**
   * MemberService    @Transactional: ON
   * MemberRepository @Transactional: ON
   * LogRepository    @Transactional: ON
   **/
  @Test
  void outerTxOn_success() {
    // given
    String userName = "outerTxOn_success";

    // when
    memberService.joinV1(userName);

    // then
    assertTrue(memberRepository.find(userName).isPresent());
    assertTrue(logRepository.find(userName).isPresent());
  }

  /**
   * MemberService    @Transactional: ON
   * MemberRepository @Transactional: ON
   * LogRepository    @Transactional: ON Exception
   **/
  @Test
  void outerTxOn_fail() {
    // given
    String userName = "로그 예외_outerTxOn_fail";

    // when
    assertThatThrownBy(() -> memberService.joinV1(userName))
        .isInstanceOf(RuntimeException.class);

    // then : 모든 데이터 롤백
    assertTrue(memberRepository.find(userName).isEmpty());
    assertTrue(logRepository.find(userName).isEmpty());
  }

}