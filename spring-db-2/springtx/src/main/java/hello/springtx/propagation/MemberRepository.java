package hello.springtx.propagation;

import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

  private final EntityManager em;

  // @Transactional
  public void save(Member member) {
    log.info("save member");
    em.persist(member);
  }

  public Optional<Member> find(String userName) {
    return em.createQuery("select m from Member m where m.userName = :userName", Member.class)
        .setParameter("userName", userName)
        .getResultList().stream().findAny();
  }

}
