# 실행 2
SET LOCK_TIMEOUT 60000; # 락 획득 시간 60초 (60초 안에 락을 얻지 못하면 예외 발생)
set autocommit false;
update member set money=1000 where member_id = 'memberA';

# lock1.sql이 commit을 하면 락을 얻어서 위 쿼리 실행 후 커밋
commit;

# 결과 적으로는 member.money = 1000

################################################################################

# lock1.sql이 트랜잭션을 종료할 때 까지 대기
# lock1.sql이 트랜잭션을 종료하면 락을 획득하고 데이터 변경
# 만약 락 타임아웃이 지나면 락 타임아웃 예외 발생
set autocommit false;
update member set money=500 where member_id = 'memberA';
