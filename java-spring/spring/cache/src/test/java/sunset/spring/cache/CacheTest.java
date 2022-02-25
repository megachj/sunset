package sunset.spring.cache;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest(classes = {MemberService.class, MemberRepository.class})
@RunWith(SpringRunner.class)
public class CacheTest {

    @Autowired
    private MemberService memberService;

    private Member smeb;

    @Before
    public void init() {
        smeb = new Member("smeb", 27);
    }

    @Test
    public void test1() {
        memberService.getMemberName(smeb);
        memberService.getMemberNameCacheable(smeb);
        memberService.getMemberNameCacheable(smeb);
        memberService.getNewMemberNameCacheable(smeb);
    }
}
