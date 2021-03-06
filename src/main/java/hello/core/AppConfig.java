package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 이 클래스는 의존성 주입을 설정하는 클래스야.
public class AppConfig {

    // @Bean memberService -> new MemoryMemberRepository()
    // @Bean orderService -> new MemoryMemberRepository()
    // 2번 호출되네? 싱글톤 깨지는거 아녀?

    // 우리가 예상한 결과
        // call AppConfig.memberService
        // call AppConfig.memberRepository
        // call AppConfig.memberRepository
        // call AppConfig.orderService
        // call AppConfig.memberRepository

    // 실제 Test 결과
        // call AppConfig.memberService
        // call AppConfig.memberRepository
        // call AppConfig.orderService

    // 실제로는 memberRepository가 한번 호출됐다.


    @Bean // @Bean 태그를 설정하면 설정된 메소드 모두 스프링 컨테이너에 등록된다.
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    // 나의 App 에서는 MemberRepository를 Memory로 사용할거야.
    // 나중에 JDBCMemter로 바뀌면 이 코드만 바꿔주면 된다.
    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
//        return null;
    }


    @Bean
    public DiscountPolicy discountPolicy() {
        // return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }


}

/*
 이전에는 객체를 생성하고 인터페이스에 어떤 것들이 들어가야 하는지에 대한 설정을
 MemberServiceImpl 에 직접 해줬다.

 기존의 MemberServiceImpl 클래스를 보면
 private final MemberRepository memberRepository = new MemoryMemberReposotiry()
 코드에서 MemoryMemberReposotiry를 직접 MemberServiceImpl를 지정 해주고 있었다.

 MemberServiceImpl은 회원에 대한 로직만 처리해야 하는데, 구체적인 MemoryMemberReposotiry(member DB 설정 관련)
 까지 관여하고 있었던 것이다.

 이런 것들을을 AppConfig 가 하도록 분리해야 한다.

 1. AppConfig가 new MemoryMemberRepository() 로 인터페이스 구현
    return new MemberServiceImpl(new MemoryMemberRepository());

 2. 기존의 MemberServiceImpl 코드에서
    private final MemberRepository memberRepository = new MemoryMemberRepository()를
    private final MemberRepository memberRepository; 로 수정한 후 생성자를 만듦.
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    -> 생성자를 통해 memberRepository에 어떤 값이 들어갈지를 결정.
       이게 바로 '생성자 주입'
*/
