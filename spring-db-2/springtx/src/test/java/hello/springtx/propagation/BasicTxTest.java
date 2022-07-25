package hello.springtx.propagation;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Slf4j
@SpringBootTest
public class BasicTxTest {

  @Autowired
  PlatformTransactionManager txManager;

  @TestConfiguration
  static class Config {
    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
      return new DataSourceTransactionManager(dataSource);
    }
  }

  @Test
  void commit() {
    log.info("start transaction");
    // 트랜잭션 매니저를 통해 트랜잭션을 시작(획득)한다
    TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

    log.info("start transaction commit");
    txManager.commit(status); // 트랜잭션을 커밋한다
    log.info("end transaction commit");
  }

  @Test
  void rollback() {
    log.info("start transaction");
    // 트랜잭션 매니저를 통해 트랜잭션을 시작(획득)한다
    TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

    log.info("start transaction rollback");
    txManager.rollback(status); // 트랜잭션을 롤백한다
    log.info("end transaction rollback");
  }

  @Test
  void doubleCommit() {
    log.info("start transaction1");
    TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("transaction1 commit");
    txManager.commit(tx1);

    log.info("start transaction2");
    TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("transaction2 commit");
    txManager.commit(tx2);
  }

  @Test
  void doubleCommitAndRollback() {
    log.info("start transaction1");
    TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("transaction1 commit");
    txManager.commit(tx1);

    log.info("start transaction2");
    TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("transaction2 rollback");
    txManager.rollback(tx2);
  }

  @Test
  void innerCommit() {
    log.info("start transaction outer");
    TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("outer.isNewTransaction() = {}", outer.isNewTransaction()); // 처음 시작한 트랜잭션이냐?

    inner();

    log.info("outer transaction commit");
    txManager.commit(outer);
  }

  private void inner() {
    log.info("start transaction inner");
    TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("inner.isNewTransaction() = {}", inner.isNewTransaction()); // 처음 시작한 트랜잭션이냐?

    log.info("inner transaction commit");
    txManager.commit(inner);
  }

  @Test
  void outerRollback() {
    log.info("start transaction outer");
    TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

    inner();

    log.info("outer transaction rollback");
    txManager.rollback(outer);
  }

}
