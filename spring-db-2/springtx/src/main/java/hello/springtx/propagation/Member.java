package hello.springtx.propagation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Member {

  @Id @GeneratedValue
  private Long id;
  private String userName;

  public Member(String userName) {
    this.userName = userName;
  }

}
