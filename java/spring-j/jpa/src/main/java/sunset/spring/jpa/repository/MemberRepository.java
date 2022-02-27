package sunset.spring.jpa.repository;

import sunset.spring.jpa.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByUsernameIn(List<String> usernameList);

    @Query("SELECT m, t FROM Member m INNER JOIN m.team t WHERE m.username IN :usernameList")
    List<Member> partialInnerJoinFindAllByUsernameIn(List<String> usernameList);

    @Query("SELECT m FROM Member m JOIN FETCH m.team WHERE m.username IN :usernameList")
    List<Member> partialFetchJoinFindAllByUsernameIn(List<String> usernameList);

    @Query("SELECT m, t, l FROM Member m INNER JOIN m.team t INNER JOIN t.league l WHERE m.username IN :usernameList")
    List<Member> allInnerJoinFindAllByUsernameIn(List<String> usernameList);

    @Query("SELECT m, t FROM Member m INNER JOIN m.team t JOIN FETCH t.league WHERE m.username IN :usernameList")
    List<Member> allFetchJoinFindAllByUsernameIn(List<String> usernameList);
}
