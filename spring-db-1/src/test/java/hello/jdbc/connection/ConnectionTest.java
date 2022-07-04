package hello.jdbc.connection;

import static hello.jdbc.connection.ConnectionConst.*;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        Connection connection1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection connection2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection = {}, class = {}", connection1, connection1.getClass());
        log.info("connection = {}, class = {}", connection2, connection2.getClass());
    }

    @Test
    void datatSourceDriverManager() throws SQLException {
        // DriverManagerDataSource - 항상 새로운 커넥션을 획득
        // 스프링이 제공하는 DataSource 가 적용된 DriverManager 인 DriverManagerDataSource
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(driverManagerDataSource);
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10); // 기본 10개
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        // Thread.sleep(1000); //커넥션 풀에서 커넥션 생성 시간 대기
        // [MyPool connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - MyPool - Added connection conn2: url=jdbc:h2:tcp://localhost/~/test user=SA
        // 풀 생성 로그 (10개)
    }


    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection connection1 = dataSource.getConnection();
        Connection connection2 = dataSource.getConnection();
        log.info("connection = {}, class = {}", connection1, connection1.getClass());
        log.info("connection = {}, class = {}", connection2, connection2.getClass());
    }

}
