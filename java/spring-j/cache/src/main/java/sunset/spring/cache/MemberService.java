package sunset.spring.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service @CacheConfig(cacheNames = "member")
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public String getMemberName(Member member) {
        return memberRepository.getName(member);
    }

    @Cacheable
    public String getMemberNameCacheable(Member member) {
        return memberRepository.getName(member);
    }

    @Cacheable(cacheNames = "newMember")
    public String getNewMemberNameCacheable(Member member) {
        return memberRepository.getName(member);
    }
}
