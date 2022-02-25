package sunset.spring.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MemberRepository {

    public String getName(Member member) {
        log.info("MemberRepository search. member[{}]", member);
        return member.getName();
    }
}
