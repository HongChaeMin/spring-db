set autocommit true; # 자동 커밋 모드 설정
insert into member(member_id, money) values ('data1',10000); # 자동 커밋
insert into member(member_id, money) values ('data2',10000); # 자동 커밋

set autocommit false; # 수동 커밋 모드 설정
insert into member(member_id, money) values ('data3',10000);
insert into member(member_id, money) values ('data4',10000);
commit; # 수동 커밋

# 보통 자동 커밋 모드가 기본으로 설정된 경우가 많기 때문에, 수동 커밋 모드로 설정하는 것을 트랜잭션을 시작한다고 표현
# 수동 커밋 설정을 하면 이후에 꼭 commit , rollback 을 호출
# 참고로 수동 커밋 모드나 자동 커밋 모드는 한번 설정하면 해당 세션에서는 계속 유지
# 중간에 변경하는 것은 가능