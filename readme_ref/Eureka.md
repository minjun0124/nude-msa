# Eureka (Discovery Service)

마이크로서비스들의 정보를 레지스트리에 등록할 수 있도록 하고 마이크로서비스의 동적인 탐색과 로드밸런싱을 제공한다.

- Eureka는 Eureka Server와 Eureka Client로 구성
- Eureka Server는 Eureka Client에 해당하는 마이크로서비스들의 상태 정보가 등록되어있는 레지스트리를 소유
- Eureka Client의 서비스가 시작 될 때 Eureka Server에 자신의 정보를 등록한다. 등록된 후에는 30초마다 레지스트리에 ping을 전송하여 자신이 가용 상태임을 알리는데 일정 횟수 이상 ping이 확인되지 않으면 Eureka Server에서 해당 서비스를 레지스트리에서 제외시킨다. 

<br>

관련 Repository - [discovery-service](https://github.com/minjun0124/nude-msa/tree/main/discovery-service)

<br>

**Code**
---

<br>

**<h2>discovery-service</h2>**

---

<br>

dependecies
```
dependencies {
	implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
}
```

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

main
``` java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServiceApplication.class, args);
	}

}
```
<br>

**<h2>user-service</h2>**

---

eureka-client 예시

<br>

dependecies
```
dependencies {
	implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
}
```

user-service yaml</br>
``` yaml
eureka:
  instance:
    instance-id: ${spring.cloud.client.hostname}:${spring.application.instance_id:${random.value}}
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8101/eureka
```

main
``` java
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
```