# 기본 데이터 입력 - SQL
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA',10000);

# 실행 1
set autocommit false;
update member set money=500 where member_id = 'memberA';

# 커밋 되면 락을 반환
commit;
