# nude-msa

본 프로젝트는 SSR에서 RESTful, CSR 을 거쳐 MSA 순서로 전환하며 아키텍처 및 스프링 프로젝트 학습을 목적으로 합니다.

<h3>🗂관련 저장소</h3>

- SafeFood (SafeFood Version.1)
	- SSR, JPA 적용
    - Link : https://github.com/minjun0124/SafeFood
- Nutrition-Designer (SafeFood Version.2)
	- CSR, RESTful 적용
    - Link : https://github.com/minjun0124/NUDE 
- Nutrition-Designer-MSA (SafeFood Version.3)
	- MSA 전환
    - Link : https://github.com/minjun0124/nude-msa (현재 위치)


<br>
<br>

💻프로젝트 기획 배경 및 목표
---

**기존 [Nutrition-Designer](https://github.com/minjun0124/NUDE) RESTful 서비스의 MSA 전환**

<br>
<br>

✏학습 목표
---
각 서비스에 최적화된 언어와 DB를 선택하여 이기종으로 개발되며 MSA의 인기가 증가하고 있다.</br>
기존 RESTful 기반으로 설계한 NUDE(Safe-Food Version.2) 프로젝트에 MSA 전환을 적용하고</br>
MSA의 특징과 장단점을 학습한다.</br>
그 외 분산추적, 모니터링을 위해 필요한 부가 서비스를 추가하여 운용해본다.</br>

<br>

🛠개발 환경
---
- Java 11
- SpringBoot 2.4.9
- Spring-Cloud
- Spring-Security 5.5
- Spring-Data-JPA
- Lombok
- JWT
- MariaDB
- Redis
- Kafka
- RabbitMQ
- Sleuth, Zipkin
- Micrometer
- Prometheus
- Grafana
- Docker

<br>

📰Project Architecture
---

<br>

<img src="/readme_ref/NUDE-MSA-ARCH.PNG" title="MSA_ARCH" alt="MSA_ARCH"></img>

<br>

⚙주요 적용 내용 및 학습 내용
---

🖱 :: 상세보기
- [Cloud Native Architecture](./readme_ref/CloudNative.md)
- [Eureka Service Discovery]()
- [API Gateway :: Routing, Load Balancer, Filter]()
- [Spring-Security + JWT :: Sliding Session (AccessToken + RefreshToken)]()
- [Redis :: Token Storage]()
- [Spring-Cloud-Config + RabbitMQ + Actuator (busrefresh) :: Config 동기화]()
- [Microservice 간의 통신 :: 동기(OpenFeign) / 비동기 (Kafka)]()
- [Kafka :: Pub / Sub]()
- [Resilience4J :: Fault Tolerance]()
- [Sleuth + Zipkin :: 분산 추적]()
- [Prometheus + Grafana :: 모니터링]()
- [Docker :: 컨테이너 가상화]()

<br>

# 💡Discussion

<h3>프로젝트 수행 중 느낀 점, 기술적인 내용 정리</h3>
<br>

[ 01. Domain 분리 & 데이터 동기화 ]
---



<br>

[ 02. Auth-Server ]
---

