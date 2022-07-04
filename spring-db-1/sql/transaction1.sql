# 세션 1 신규 데이터 추가 SQL
# 트랜잭션 시작
set autocommit false; # 수동 커밋 모드
insert into member(member_id, money) values ('newId1',10000);
insert into member(member_id, money) values ('newId2',10000);

# commit 하기 전 상태이기 때문에 trancsaction2 에서는 안보임
select * from member;

# db가 반영되서 transcation2에서 보임
commit;

# insert 하기 전으로 돌아가 데이터 초기화됨
rollback;