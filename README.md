# Jwt_tutorial

[https://www.inflearn.com/course/스프링부트-jwt/dashboard](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-jwt/dashboard) 의 강의영상의 실습 code

---

## 나의 환경설정

- 개발환경 : Windows11
- JAVA : openjdk-11
- spring boot : 2.7.10
- Maven 프로젝트

---

[https://okky.kr/articles/382738](https://okky.kr/articles/382738) ⇒ Spring Security 관련 좋은 글

[https://webcoding-start.tistory.com/35](https://webcoding-start.tistory.com/35) ⇒ 

## 배운 공부

### Spring Security의 간단한 구조설명

1. Security Filter Chain
    - Spring Security의 핵심 컴포넌트 중 하나로, HTTP 요청에 대한 보안 처리를 수행하는 필터 체인입니다. 보안 인증, 인가 및 보안 로직을 적용합니다.
2. Authentication Manager
    - 인증 관련 처리를 수행하는 컴포넌트입니다. 사용자가 제공한 인증 정보를 검증하고 인증에 성공하면 SecurityContext에 인증된 사용자 정보를 저장합니다.
3. UserDetailsService
    - 사용자 정보를 로드하는 인터페이스입니다. Authentication Manager에서 사용자 정보를 가져오기 위해 호출됩니다. 이를 통해 인증된 사용자의 권한을 구분한다.

### 사용했던 객체 또는 인터페이스 등

- **SpringSecurityFilterChain**
    - spring security에서 가장 중요한 부분
    - 여러 개의 필터로 구성되어 있으며 각 필터는 보안측며에서 서로 다른 작업을 수행한다.
    - 이러한 필터들은 순서대로 실행되고 체인의 끝에서 다른 모든 필터를 통한 의사결정을 진행
    - WebSecurityConfigurerAdapter를 상속한 클래스에서 configure 메서드를 사용하여 필터 체인을 정의
- **ResponseEntity**
    - httpentity를 상속받아 그에 대한 결과 데이터와 HTTP 상태 코드를 직접 제어할 수 있는 클래스.
    - HTTP 요청(Request) 또는 응답(Response)에 해당하는 정보를 담아 객체화 시켜서 반환해줌.
    - HTTP에 대한 요청, 응답에 있어 서버가 클라이언트에게 요청과 응답에 대한 커스텀이 가능하게 하는듯.
    - ResponseEntity 를 사용할 때, Constructor 를 사용하기보다는 Builder 를 활용하는 것을 권장하고 있다. 상태코드를 넣을 때 잘못된 숫자의 대입을 예방하기 위함.
    
- Spring Security의 WebSecurity와 HttpSecurity
    - WebSecurity
        - WebSecurity는 spring에서 기본 웹 보안 구성에 대한 메서드를 제공하여 웹 보안의 **기본 동작을 변경**하고자 할 때 사용.
        - 이 클래스의 일부 메서드는 HttpSecurity객체를 반환
    - HttpSecurity
        - WebSecurity보다 상대적으로 구체적인 웹 보안을 **정의**해준다.
        - 예를들어 spring 자체의 csrf 설정, session 관리, 인증, 인가, 로그인 페이지, https 등.
        - 스프링 시큐리티의 설정은 HttpSecurity가 한다고 생각해야 한다.
        
- SecurityContext
    - Authentication을 보관하는 역할을 하며, SecurityContext를 통해 Authentication 객체를 꺼내올 수 있음.

- Authentication(인가)
    - Authentication는 현재 접근하는 주체의 정보와 권한을 담는 인터페이스이다. Authentication 객체는 Security Context에 저장되며, SecurityContextHolder를 통해 SecurityContext에 접근하고, SecurityContext를 통해 Authentication에 접근할 수 있다.
    
- Claims
    - JWT를 구성하는 헤더, 페이로드, 서명 부분 중 페이로드에는 토큰에 저장된 사용자 정보가 있다. Claims 객체는 이 페이로드에서 정보를 처리할 수 있는 객체.
    - Claims를 객체를 통해 클레임 정보를 파싱해서 사용자가 필요로 하는 정보를 추출 할 수 있다.
        - 예를 들어 사용자 ID, 사용자 이름, 역할 및 권한과 같은 정보
    - [https://jwt.io/](https://jwt.io/)
        
        ![스크린샷 2023-03-26 203533](https://user-images.githubusercontent.com/72079877/227774237-23ce45b0-be41-4d2f-9f00-6d697e53e056.png)

        
        다음과 같이 jwt 토큰을 디코드하면 헤더, 페이로드, 서명란이 나눠져있다. 이때 Payload의 정보를 claim 정보라고 하는 것 같다.
        
        ```java
        import org.springframework.security.jwt.Jwt;
        import org.springframework.security.jwt.JwtHelper;
        
        String jwtToken = "your JWT token";
        String secretKey = "your secret key";
        
        Jwt jwt = JwtHelper.decodeAndVerify(jwtToken, new MacSigner(secretKey));
        String claims = jwt.getClaims();
        
        // 클레임 정보를 추출하여 사용합니다.
        String userId = claims.get("userId");
        String username = claims.get("username");
        List<String> roles = claims.get("roles");
        ```
