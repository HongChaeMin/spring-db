package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final LogRepository logRepository;

  public void joinV1(String userName) {
    Member member = new Member(userName);
    Log logMessage = new Log(userName);

    log.info("== start call memberRepository ==");
    memberRepository.save(member);
    log.info("=== end call memberRepository ===");

    log.info("=== start call logRepository ====");
    logRepository.save(logMessage);
    log.info("==== end call logRepository =====");
  }

  public void joinV2(String username) {
    Member member = new Member(username);
    Log logMessage = new Log(username);

    log.info("== start call memberRepository ==");
    memberRepository.save(member);

    log.info("== logRepository 호출 시작 ==");
    try {
      logRepository.save(logMessage);
    } catch (RuntimeException e) {
      log.info("log 저장에 실패했습니다. logMessage={}", logMessage.getMessage());
      log.info("정상 흐름 변환");
    }
    log.info("== logRepository 호출 종료 ==");
  }

}
