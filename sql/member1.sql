# 기본 데이터 입력 - SQL
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA',10000);
insert into member(member_id, money) values ('memberB',10000);

# 계좌이체 실행 SQL - 성공
set autocommit false;
update member set money=10000 - 2000 where member_id = 'memberA';
update member set money=10000 + 2000 where member_id = 'memberB';

# member2.sql 에서 조회 가능
commit;

# 계좌이체 실행 SQL - 오류
set autocommit false;
update member set money=10000 - 2000 where member_id = 'memberA'; //성공
update member set money=10000 + 2000 where member_iddd = 'memberB'; //쿼리 예외

# 실패한 상태로 commit 하면 memberA의 돈만 2000원이 날라가는 문제 발생
# commit;

# 처음 상태로 돌아감
rollback;