# SpringCloudGateway (API Gateway)

SCG(SpringCloudGateway)는 API Gateway의 하나이다.

기존의 Netflix OSS인 Zuul과는 다음과 같은 차이가 있으며 비동기 처리에 최적화되어 현재는 Spring Project에서는 SCG를 주로 활용한다.

- Zuul은 Blocking 방식 활용하는 반면 SCG는 Non-Blocking 방식을 활용하여 비동기 처리에서 더 나은 성능을 보여준다.
- Zuul은 Filter만으로 동작하는 반면, SCG는 Predicates(수행을 위한 사전 요구조건)와 Filter를 조합하여 동작한다.
- Zuul은 Web/WAS로 Tomcat을 사용하고, SCG는 Netty를 사용한다. Netty는 비동기 네트워킹을 지원하는 어플리케이션 프레임워크이다.

<br>

관련 Repository - [API-Gateway](https://github.com/minjun0124/nude-msa/tree/main/gateway)

<br>

build.gradle
``` gradle
dependencies {
	implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
}
```

<br>

**역할과 책임**
---

<br>

**<h2>Routing</h2>**

라우트는 목적지 URI, 조건자 목록과 필터의 목록을 식별하기 위한 고유 ID로 구성된다. 라우트는 모든 조건자가 충족됐을 때만 매칭된다


**<h2>Predicates</h2>**

각 요청을 처리하기 전에 실행되는 로직, 헤더와 입력된 값 등 다양한 HTTP 요청이 정의된 기준에 맞는지를 찾는다.

**<h2>Filter</h2>**

HTTP 요청 또는 나가는 HTTP 응답을 수정할 수 있게한다. 다운스트림 요청을 보내기전이나 후에 수정할 수 있다. 라우트 필터는 특정 라우트에 한정된다.

<br>
<br>

**Code**
---

<br>
<br>

application.yml 일부 (user-service 설정)

``` yml
server:
  port: 8000

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8101/eureka
spring:
  application:
    name: apigateway-service
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
  cloud:
    gateway:
      routes:
...
#   user-service POST, GET, PUT, DELETE method (AuthorizationHeaderFilter)
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user-service/**
            - Method=POST,GET,PUT,DELETE
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-service/(?<segment>.*), /$\{segment}
            - AuthorizationHeaderFilter
...
```


**<h2>discovery-service</h2>**



eureka yaml
``` yaml
server:
  port: 8101

spring:
  application:
    name: discoveryservice
eureka:
  client:
    fetch-registry: false
    register-with-eureka: false
    service-url:
      defaultZone: http://127.0.0.1:${server.port}/eureka/

```

AuthorizationHeaderFilter.java</br>
JWT의 유효성에 대해 검사한다. Redis Blacklist를 참조하여 로그아웃된 회원인지 확인

``` java
@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment env;
    private final RedisService redisService;

    public AuthorizationHeaderFilter(Environment env, RedisService redisService) {
        super(Config.class);
        this.env = env;
        this.redisService = redisService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            // Get Bearer Token
            String authorizationHeader = request.getHeaders().get(org.springframework.http.HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");

            if (!isJwtValid(jwt) && redisService.isBlack(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;

        String property = env.getProperty("jwt.secret");

        try {
            subject = Jwts.parserBuilder().setSigningKey(property)
                    .build().parseClaimsJws(jwt).getBody().getSubject();
        } catch (Exception exception) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;

    }

    // Mono, Flux -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);

        return response.setComplete();
    }

    public static class Config {
    }


}

```
