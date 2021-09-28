# SpringConfig

Spring Cloud Config는 분산된 환경에서 설정 파일을 외부로 분리할 수 있도록 해준다. 개발/테스트 환경 그리고 운영 환경에서까지 모든 환경 구성을 간편하게 관리할 수 있다. 설정을 위한 별도의 서버를 구성하기 때문에 실행 중인 애플리케이션이 서버에서 설정 정보를 받아와 갱신하는 방식이다. 즉 실행 중에 설정값 변경이 필요해지면, 설정 서버만 변경하고 애플리케이션은 갱신하도록 해주기만 하면 된다. 따라서 **설정이 바뀔 때마다 빌드와 배포가 필요 없는 구조**이다.

설정 파일을 수정하고 이를 반영하기 위해서 RabbitMQ와 Spring-Bus-AMQP(동적으로 Config 변경을 적용하기 위한 Message Queue Handler)를 사용하였다.<br>
Publisher로서 Config Server, Subscriber로서 마이크로 서비스를 등록하고 http[s]://{microservice host}/actuator/refresh를 POST로 요청한다. Config 변경 정보를 RabbitMQ에 전송하여 각 마이크로 서비스의 Config를 동적 반영한다.<br>

+)bus-refresh를 사용하면 연결된 모든 마이크로 서비스에 변동된 설정 사항을 반영한다.


<br>

**관련 Repository**</br>
[Config-Server](https://github.com/minjun0124/nude-msa/tree/main/config-server)</br>
[User-Service (Micro-Service 예시)](https://github.com/minjun0124/nude-msa/tree/main/user-service)</br></br><br>

**Config-Server**
---

<br>

**ConfigServerApplication.java**
``` java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
```
</br>

**application.yml**

- rabbitmq 서버 정보 설정
- 설정 정보가 저장되는 저장소 연동 (git)
- search-locations : 각 마이크로 서비스의 설정 파일을 저장, 관리.


``` yml
server:
  port: 8888

spring:
  application:
    name: config-service
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: file:///${user.home}/Desktop/mjlee/git_workspace/nude-msa/git-local-repo
        git:
#          uri: file:///Users/MJLEE/Desktop/mjlee/git_workspace/nude-msa/git-local-repo
          uri: https://github.com/minjun0124/nude-cloud-config
#          username: [user name]
#          password: [password]

management:
  endpoints:
    web:
      exposure:
        include: health, busrefresh
```
<br><br>

**User-Service**
---

<br>

**application.yml**

- rabbitmq 서버 정보 설정
- actuator/bus-refresh 를 위한 설정

``` yml
server:
  port: 0

spring:

...

  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest

...

management:
  endpoints:
    web:
      exposure:
        include: refresh, health, beans, httptrace, busrefresh, info, metrics, prometheus

```

<br>

**bootstrap.yml**

- Config 서버의 uri를 설정한다.
- name 설정을 통해 해당 마이크로 서비스에 적용될 설정 파일을 지정한다.

``` yml
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: user-service
```

<br>
<br>

**설정 파일**
---

설정 파일의 위치는 위에 Config Server의 yml파일에서 설정했다.<br>
file:///${user.home}/Desktop/mjlee/git_workspace/nude-msa/git-local-repo

<br>

**application.yml** <br>
Config Server에서 관리하는 application.yml은 연결된 마이크로 서비스들에 공통적으로 적용된다.<br>
여기서는 jwt의 header값, secret key(암호화 됨), AccessToken과 RefreshToken의 유효 시간 값을 관리한다.

``` yml
jwt:
  header: Authorization
  secret: '{cipher}AQASTXXjZft6XFC3jXsgDPLcITJlz7/ZAD4q7NUuqkdsX38qm7/cNcERnvsFAT8ht59obUS0zygC7KmpbO98Au5IqlBH3rpBMFkyShmqVYcMk7sVHm+/r2nclmRkHFnWKRd+rL6TkiZbPhHsqvlkIyz//+sFrZS6Pw3TmL6u3o+bSb7qz4So3tvZzF7PNcTgYOaUJ1zJAEOu7jocrqJHZemx2BkQb2tTTdsqENaxEt2JCq5ToOhxpuruyQgPNYoaRqKzqFsOvwQabUySVfiq1zf6IeWRtH/Uq6KbsVNqZBmHgCKU9AXWe8LyEa+eTT79mWfEJ5XHU7xGw1Z1ZWXjoNmTpNMJ8Z50S2SUaIjiJv6V4WnC0A0G7ntZf6VGH1Dq78qbTAra33k9EATaTQrKrkyZ2KBGHjIjTiNBEo3pLJG40oH7gMn8hKDHyLNo57xOIhKpC6UrgLtihiI2dxoRnbXIxgwWYsECDbgOYkFK4eT4PcFsiIxzcuy84BUWtckgj6SPdB+ottQjnQDklKhgy5VBb9W6+pyifA8zN3Die2mttA=='
  access-token-validity-in-seconds: 1800
  refresh-token-validity-in-seconds: 604800

```

**user-service.yml**

별도의 이름을 설정하여 각 마이크로 서비스마다 설정 파일을 관리할 수 있도록 한다.<br>
여기서는 Database 연결정보와 OpenFeign 활용을 위한 EndPoint를 설정했다.<br>
설정 파일을 user-service-dev, user-service-prod 와 같이 나눠서 관리하면 서비스 환경과 개발 환경의 설정을 쉼게 이원화할 수 있다.

``` yml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/nude_user
    driver-class-name: org.mariadb.jdbc.Driver
    username: root
    password: '{cipher}AQBn8BElntLw1W398K7WwLu5UGnzy/diTt4qSQJtzEMZpTJv3Nsv9RBc0iBRZOcbuh8oeV16Eja8lYj5D4GSJwClEMcBjKoECu2fe5BEHiF+v7+GWniIfWNVQd3B8M/q2/1FWFdO0nI45P/1KHJOi5NgxYMyNKPsIPH1wZbEfyL3g/yCJLXGlySFIF3v4SRoWa+17Zwu9QIwYKkZ/kKo+xD48eYy6Mt8ai609x2LSIrhILZkUAlk8mA5rLK0vtQHwvsty5HbxSWll9ess7XJ3oCs8ivaCpjGw1JE1K2gR8j+2OjNSCmzWLmhAih5HIUKce+anx9moNpBJHs+/iPjQ5SeUYUH2ujXeL7fW2a8K5X61tHY5kLUkLAVtJu0T48dS0o='

cart_service:
  url: http://cart-service/carts/%s
  exception:
    cart_create_fail: Fail to create Cart
```

<br><br><br>
