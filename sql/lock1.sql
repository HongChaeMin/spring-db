# 기본 데이터 입력 - SQL
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA',10000);

# 실행 1
set autocommit false;
update member set money=500 where member_id = 'memberA';

# 커밋 되면 락을 반환
commit;

################################################################################
# lock 조회

# 기본 데이터 입력 - SQL
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA',10000);

# select for update 구문을 사용하면 조회하면서 락도 획득
# 트랙잭션을 종료할 때 까지 락 보유
set autocommit false;
select * from member where member_id='memberA' for update;
