# AuthServer (토큰 기반 인증/인가 모델)

Session 기반의 Security를 적용하는 경우 손쉽게 구현이 가능하지만 Session 데이터는 서버 애플리케이션 메모리에 저장되기 때문에 실제로 프로덕션 환경에서 여러 대의 서버 인스턴스를 운용할 경우 Session 기반 Security는 따져야할 것이 많다.

여러 서비스를 독립적으로 운용해야하는 MSA 환경에서 Session을 핸들링하는 코드의 중복이 여럿 생길 수 밖에 없다.

따라서 여러 서버를 운용할 때 클라이언트에 대한 인가 처리를 비교적 쉽게 처리 가능한 토큰 기반 인증/인가 모델을 사용해본다.

(또한 Session과 같이 Cookie를 사용하지 않기 때문에 CSRF에 대한 걱정도 덜 수 있다)

한편, Session에 비해 토큰 발급, 유효성 검사, 또는 서버에서 클라이언트 강제 로그아웃 처리 등은 추가적으로 작업이 필요하다는 단점의 보완이 필요하다.


<br>

**관련 Repository**</br>
[Auth-Service](https://github.com/minjun0124/nude-msa/tree/main/auth-service)</br>
[API-Gateway](https://github.com/minjun0124/nude-msa/tree/main/gateway)</br>
[User-Service (Micro-Service 예시)](https://github.com/minjun0124/nude-msa/tree/main/user-service)</br></br><br>

**Auth-Service**
---

<br>

**SecurityConfig.java**
``` java
// 기본적인 Web 보안을 활성화
// 추가적으로 WebSecurityConfigurer 를 implements 하거나
// WebSecurityAdapter 를 extends 하는 방법이 있다.
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //@PreAuthorize 어노테이션을 메소드단위로 추가하기 위해서 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * @param tokenProvider
     * @param jwtAuthenticationEntryPoint
     * @param jwtAccessDeniedHandler      위 클래스를 생성자 주입
     */
    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    /**
     * password encoder 로는 BCryptPasswordEncoder 를 사용
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * h2-console 하위 모든 요청들과 파비콘 관련 요청은 SpringSecurity 로직을 수행하지 않도록
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/h2-console/**"
                        , "/favicon.ico"
                        , "/error"
                );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

                // exception handling 처리에 내가 만든
                // authenticationEntryPoint(401), accessDeniedHandler(403) 를 추가해준다.
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // enable h2-console
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // permitAll() - 토큰이 없는 상태에서 요청이 들어오는 Request 에 대해서 permit all 설정
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()

                // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
```
</br>

**SecurityConfig.java**

authorize : username, password를 기반으로 토큰을 생성하여 응답한다.<br>
logout : 사용자에게 발급한 AccessToken을 Redis Blacklist에 추가한다. <br>
reIssueToken : AccessToken을 재발급한다.<br> AccessToken과 RefreshToken의 유효성을 검사한 후 AccessToken을 재발급한다. 기존 AccessToken은 Blacklist 처리된다.

``` java
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final TokenProvider tokenProvider;
    // 스프링 시큐리티의 인증에 대한 지원을 설정하는 몇가지 메소드를 가지고 있다.
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping
    @Timed(value = "auth.authorize", longTask = true)
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody SignInForm signInForm) {

        // username, password 를 가지고 Authentication Token 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword());

        // 생성된 토큰을 이용해서 authenticate 메소드가 실행이 될 때,
        // CustomUserDetailsService:loadUserByUsername 메소드가 실행, User 객체 생성
        // User 객체로 Authentication 객체 생성
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 위에서 생성된 인증정보인 authentication 을 기준으로 JWT Token 생성
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(signInForm.getUsername());

        // TokenDto를 이용해서 ResponseBody 에도 넣어서 return
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);

        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken) {
        tokenProvider.blackAccessToken(accessToken);
        tokenProvider.deleteRefreshToken(accessToken);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity reIssueToken(@RequestBody TokenDto tokenDto) {
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        Claims claims = tokenProvider.getClaims(accessToken);
        String username = claims.getSubject();

        Boolean chkResult = tokenProvider.refreshTokenCheck(username, refreshToken);

        if (chkResult) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String reIssuedToken = tokenProvider.reIssueAccessToken(claims);

        return new ResponseEntity<>(new TokenDto(reIssuedToken, refreshToken), HttpStatus.OK);
    }
}
```
<br><br>

**API-Gateway**
---
<br>
API-Gateway에서 Redis를 참조하여 Blacklist 처리된 AccessToken은 로그아웃된 사용자의 토큰으로 간주하여 접근을 제한한다. <br><br>

**AuthorizationHeaderFilter.java - apply**
``` java
...
            if (!isJwtValid(jwt) && redisService.isBlack(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
...
```

<br><br>

**User-Service**
---

<br>

**JwtFilter.java - doFilter** <br>
요청 헤더의 AccessToken의 유효성을 검사하고 정상 토큰이며 SecurityContext에 토큰 정보를 저장한다.


``` java
/**
 * No2.
 * JWT 를 위한 커스텀 필터를 만들기 위해 JwtFilter 클래스 생성
 */
public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";

    // TokenProvider 주입
    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * 실제 필터링 로직
     * doFilter 는 토큰의 인증정보를 SecurityContext 에 저장하는 역할 수행
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        // 1. Request 에서 토큰을 받아서
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();
        // 2. 유효성 검사를 하고 정상 토큰이면
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            // 3. 토큰에서 Authentication 객체를 받아와서
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            // 4. SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    ...

}
```

<br>

**UserController.java - getMyUserInfo** <br>
SecurityContext에 저장된 토큰 정보를 기반으로 쉽게 권한 인가처리를 할 수 있다.<br>
**@PreAuthorize**를 활용하여 해당 권한이 있는 사용자에게만 접근 권한을 준다.<br>
접근 권한이 없을 시 401(Unauthorized)에러


``` java
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final Greeting greeting;
    private final Environment env;

    ...


    // PreAuthorize 활용
    // USER Role 과 ADMIN Role 모두 접근할 수 있다.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDto> getMyUserInfo(@RequestHeader("Authorization") String jwt) {
        UserDto userDto = userService.getUserWithJwt(jwt);

        return ResponseEntity.ok(userDto);
    }

    ...


}
```
