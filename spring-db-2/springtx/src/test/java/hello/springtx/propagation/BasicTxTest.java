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
}
