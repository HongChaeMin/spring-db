package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 트랜잭션 - 트랜잭션 매니저
 **/
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    // 트랜잭션 매니저를 주입 받는다. 지금은 JDBC 기술을 사용하기 때문에 DataSourceTransactionManager 구현체를 주입 받아야 한다.
    // 물론 JPA 같은 기술로 변경되면 JpaTransactionManager 를 주입 받으면 된다.
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) {
        // 트랜잭션을 시작한다.  // new DefaultTransactionDefinition : 트랜잭션과 관련된 옵션을 지정
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); // 트랜잭션이 성공하면 로직 호출해서 커밋
        } catch (Exception e) {
            transactionManager.rollback(status); // 문제가 발생하면 로직 호출해서 트랜잭션 롤백
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
