# 데이터 초기화
set autocommit true;
delete from member;
insert into member(member_id, money) values ('oldId',10000);

# trancsaction1 이 커밋을 한 상태면 newId1, newId2가 보임
select * from member;