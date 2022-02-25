package sunset.spring.jpa.repository;

import sunset.spring.jpa.model.entity.League;
import sunset.spring.jpa.model.entity.Member;
import sunset.spring.jpa.model.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class MemberRepositoryTest extends DataTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Before
    public void initTestData() {
        League lck = new League();
        ReflectionTestUtils.setField(lck, "name", "lck");
        League lpl = new League();
        ReflectionTestUtils.setField(lpl, "name", "lpl");
        saveAll(Arrays.asList(lck, lpl));

        Team skt = new Team();
        ReflectionTestUtils.setField(skt, "league", lck);
        ReflectionTestUtils.setField(skt, "name", "SKT");
        Team damwon = new Team();
        ReflectionTestUtils.setField(damwon, "league", lck);
        ReflectionTestUtils.setField(damwon, "name", "DamWon");
        Team ig = new Team();
        ReflectionTestUtils.setField(ig, "league", lpl);
        ReflectionTestUtils.setField(ig, "name", "ig");
        saveAll(Arrays.asList(skt, damwon, ig));

        Member faker = new Member();
        ReflectionTestUtils.setField(faker, "team", skt);
        ReflectionTestUtils.setField(faker, "username", "faker");
        Member bang = new Member();
        ReflectionTestUtils.setField(bang, "team", skt);
        ReflectionTestUtils.setField(bang, "username", "bang");
        Member wolf = new Member();
        ReflectionTestUtils.setField(wolf, "team", skt);
        ReflectionTestUtils.setField(wolf, "username", "wolf");
        Member nuguri = new Member();
        ReflectionTestUtils.setField(nuguri, "team", damwon);
        ReflectionTestUtils.setField(nuguri, "username", "nuguri");
        Member canyon = new Member();
        ReflectionTestUtils.setField(canyon, "team", damwon);
        ReflectionTestUtils.setField(canyon, "username", "canyon");
        Member rookie = new Member();
        ReflectionTestUtils.setField(rookie, "team", ig);
        ReflectionTestUtils.setField(rookie, "username", "rookie");
        saveAll(Arrays.asList(faker, bang, wolf, nuguri, canyon, rookie));
    }

    @Test
    public void test1() {
        List<Member> members = memberRepository.findAllByUsernameIn(Arrays.asList("faker", "bang", "nuguri", "rookie"));
        log.info("{}", members);
    }

    @Test
    public void test2() {
        List<Member> members = memberRepository.partialInnerJoinFindAllByUsernameIn(Arrays.asList("faker", "bang", "nuguri", "rookie"));
        log.info("{}", members);
    }

    @Test
    public void test3() {
        List<Member> members = memberRepository.partialFetchJoinFindAllByUsernameIn(Arrays.asList("faker", "bang", "nuguri", "rookie"));
        log.info("{}", members);
    }

    @Test
    public void test4() {
        List<Member> members = memberRepository.allInnerJoinFindAllByUsernameIn(Arrays.asList("faker", "bang", "nuguri", "rookie"));
        log.info("{}", members);
    }

    @Test
    public void test5() {
        List<Member> members = memberRepository.allFetchJoinFindAllByUsernameIn(Arrays.asList("faker", "bang", "nuguri", "rookie"));
        log.info("{}", members);
    }
}
