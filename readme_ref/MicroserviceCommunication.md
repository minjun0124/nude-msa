# MicroserviceCommunication (마이크로 서비스 간 통신)

마이크로 서비스는 각각 다른 서비스 도메인의 데이터를 관리하고 처리한다. 하나의 기능을 수행하기 위해서 각각의 마이크로서비스 인스턴스들이 유기적으로 상호작용을 하고 적절한 데이터를 사용자에게 내려주는 방식을 취한다.
<br><br>
마이크로 서비스 통신은 동기 방식과 비동기 방식으로 처리할 수 있다. 동기 방식은 요청한 서비스의 응답을 기다리고 이후 프로세스를 이어 처리하는 방식이다. 반면 비동기 방식은 요청한 서비스의 응답을 기다리지 않는다. 필요한 서비스에 대해 요청하고 자신의 이후 프로세스를 이어간다.
<br><br><br>
**[동기 통신과 비동기 통신]**<br><br>
동기 통신과 비동기 통신은 다음과 같은 장단점이 있다.<br><br>
예를 들어 사용자가 주문을 요청하면 'Order-Service'는 주문 정보를 생성하고 'Item-Service'는 상품 재고를 확인하여 주문 승인 및 재고 차감을 해야하는 역할과 책임이 있다. 이를 **동기 통신**으로 구현하면 **구현과 트랜잭션 관리가 쉽다.**<br><br>
하지만 이벤트 등으로 **트래픽이 몰릴 때** 문제가 발생한다. 사용자의 주문을 생성하고 승인하는 데까지 오랜 시간이 소요되기 때문이다.<br><br>
**비동기 통신**을 사용하면 이런 문제점을 해결할 수 있다. 사용자가 주문을 요청할 시 주문 정보 생성을 완료하고 메세지 서비스를 이용해 'Item-Service'에 재고 차감을 요청한다. 그리고 다음과 같은 메시지를 출력한다. '요청하신 주문을 내용을 처리 중입니다. 나의 주문 내역을 확인해주세요.' 이후, 'Item-Service'가 재고를 확인하고 승인, 차감까지 모든 프로세스를 완료하면 주문의 상태를 '확인 중'에서 '주문 완료'로 변경한다.<br><br>
위와 같은 처리 방식에서 사용자는 주문 처리에 대해 재고 확인, 승인, 차감까지 기다리지 않고 주문을 마칠 수 있다. 이를 통해 사용자 경험을 개선할 수 있다.<br><br>
한편, 비동기 통신은 별도의 메세지 서비스를 사용해야 하며 트랜잭션 관리의 어려움이 있다. 만일 'Item-Service'에서 재고를 확인하고 차감, 승인하는 과정에서 오류가 생기거나 재고 부족으로 주문 요청을 거절해야 한다면 이미 생성된 주문을 제거할 필요가 있다. 즉, 요청 실패에 따른 보상 트랜잭션이 필요하다.<br>
물론 위의 경우에는 간단하게 'Item-Service'에 해당 주문 건의 주문 상태를 '주문 실패(재고 부족)'와 같이 변경하여 처리할 수 있지만, 특별한 핸들링이 필요할 경우 SAGA Pattern이나 Outbox Pattern 등을 활용하여 보상 트랜잭션을 구현할 필요가 있다. <br>
문제는 보상 트랜잭션의 처리 비용이 높으며 무분별한 적용은 복잡한 트랜잭션 관리를 야기한다는 것이다. 따라서 서비스 분리 전에 트랜잭션 처리를 고려하고 꼭 분리해야 할 필요가 있는지 고민해봐야 한다.

<br><br>

**동기 통신 (OpenFeign 활용)**
---

보통의 MSA 에서 각각의 서비스는 RESTful 한 API를 제공하는데, 이 때 각각의 서비스는 특정 서비스가 노출하는 Endpoint 에 API 호출을 통해서 데이터를 조작한다. Spring-Cloud 기반 MSA에서 대표적으로 RestTemplate과 Spring-Cloud-OpenFeign 2가지 통신은 방식을 사용한다.<br><br>

현재 프로젝트는 이 중 Feign을 활용하여 동기 통신을 구현하였다. Feign 은 Netflix 에서 개발된 Http client binder로 웹 서비스 클라이언트를 보다 쉽게 작성할 수 있다. 또 Feign은 interface 를 작성하고 annotation 을 선언 하기만 하면되기에 RestTemplate에 비해 코드를 축소할 수 있다. 가독성이나 유지보수 측면에서 더 나은 기능을 보여주기 때문에 Feign을 활용하였다.

<br>

아래는 회원가입시 장바구니 정보를 생성하는 간단한 예시이다.

<br>

**관련 Repository**</br>
[User-Service](https://github.com/minjun0124/nude-msa/tree/main/user-service)</br>
[Cart-Service](https://github.com/minjun0124/nude-msa/tree/main/cart-service)</br>

<br>

**Gradle**

``` gradle
dependencies {
	implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
}
```

</br>
</br>

**User-Service**
---

<br>

**UserService.java : signup**</br>
회원 가입 메소드 : 회원 정보를 생성한다. 추가로 Feign을 활용하여 Cart-Service와 통신하고 장바구니 데이터를 생성한다. <br>
CartServiceClient를 생성자 주입하고 관련 메소드를 사용한다.

```java
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final Environment env;

    // FeignClient Interface. 생성자 주입
    private final CartServiceClient cartServiceClient;

    /**
     * 회원가입을 수행하는 메소드
     */
    @Transactional
    public User signup(SignUpForm signUpForm) {
        // UserDto 로부터 username 을 가져와서 username 중복 가입을 막는다.
        if (userRepository.findOneWithAuthoritiesByUsername(
          signUpForm.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        log.info("SignUpForm address : " + signUpForm.getAddress().toString());

        // 권한 정보를 만든다.
        //빌더 패턴의 장점
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")         // 유저의 권한 정보
                .build();

        // 유저 정보를 만든다
        User user = User.builder()
                .username(signUpForm.getUsername())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .email(signUpForm.getEmail())
                .phone(signUpForm.getPhone())
                .address(signUpForm.getAddress())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // 유저, 권한 정보를 저장
        userRepository.save(user);

        /* FeignClient */
        cartServiceClient.createCart(user.getId());

        return user;
    }
```


</br>

**CartServiceClient.java** </br>


``` java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// yml 파일에 정의한 url에 mapping되어 요청을 처리한다.
@FeignClient(name = "cart-service")
public interface CartServiceClient {

    @PostMapping("/carts/create/{userId}")
    void createCart(@PathVariable("userId") Long userId);
}
```

**user-service.yml** <br>

``` yml
...

cart_service:
  url: http://cart-service/carts/%s
  exception:
    cart_create_fail: Fail to create Cart
```

</br></br>

**Cart-Service**
---
요청 정보를 기반으로 장바구니 데이터를 생성한다.

<br>

**CartController.java : createCart**</br>
``` java
@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/create/{userId}")
    public void createCart(@PathVariable("userId") Long userId){
        Cart cart = new Cart(userId, 0);
        cartService.createCart(cart);

        return;
    }

    ...

}
```

<br><br>

**ErrorDecoder**
---
Feign은 Microservice 에서 내부적으로 API 호출을 수행했을 때, 예외 처리를 핸들링하는 방법을 ErrorDecoder로 제공한다.<br> Service 에서 문제가 생기면 해당 에러를 반환한다.

</br>

**CartServiceClient.java** </br>
``` java
@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final Environment env;

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                break;
            case 404:
                if (methodKey.contains("createCart")) {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status())
                            , env.getProperty("cart_service.exception.cart_create_fail"));
                }
                break;
            default:
                break;
        }
        return null;
    }
}
```
<br>
<!-- 
**Circuit Breaker (Resilience4J)**
---
-->
<!-- 
***circuit breaker***는 ***fault tolerance library*** 시스템에서 사용되는 대표적인 패턴으로써 서비스에서 타 서비스 호출 시 에러가 계속 발생하게 되면 circuit를 열어서 메시지가 다른 서비스에 ***전파되는 것을 방지***하고 미리 정의해 놓은 fallback response를 보내어 서비스 장애가 전파되지 않도록 하는 패턴이다.
-->
<br>
<!-- Resilience4JConfig.java -->


<br>

**비동기 통신 (Kafka 활용)**
---

비동기 메시지를 사용하여 상호 간에 통신하는 방식을 **메시징**(Messaging)이라고 부른다. 마이크로서비스 환경에서 비동기 처리 시 보통 *Kafka*나 *RabbitMQ* 같은 **메시지 브로커**(Message Broker)를 사용하여 메시징을 구현한다.

<br>

RabbitMQ는 메세지 유실의 위험성이 있어 안정성이 중시되는 서비스라면 상대적으로 안정적인 Kafka를 선호한다.<br>
또 RabbitMQ는 20k/sec 처리를 보장하고 Kafka는 100k/sec 처리로 고성능 고가용성을 제공한다.<br>
따라서 Kafka를 활용한 Pub/Sub 패턴으로 비동기 통신을 구현해본다.

<br>

아래는 사용자의 주문 요청을 처리하는 예시이다.</br></br>

**관련 Repository**</br>
[Order-Service](https://github.com/minjun0124/nude-msa/tree/main/order-service)</br>
[Item-Service](https://github.com/minjun0124/nude-msa/tree/main/item-service)</br>

<br>

**Gradle**

``` gradle
dependencies {
	implementation 'org.springframework.kafka:spring-kafka'
}
```

<br>


**Order-Service**
---

'Order-Service'에서 주문을 생성하고 주문 상태를 '대기'로 설정한다.<br>
Publisher로서 메시지를 생성하고 'item-topic'에 메시지를 전송한다.<br>

<br>

**OrderController.java** </br>
``` java
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;

    @PostMapping("/{userId}")
    @Timed(value = "orders.insert", longTask = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity insertOrder(@RequestBody OrderInsertDto orderInsertDto
                                      , @PathVariable("userId") Long userId) {
        /* 주문 정보 생성 */
        orderService.insertOrder(orderInsertDto, userId);

        /* item-topic에 주문 상품 정보를 전달한다. */
        kafkaProducer.send("item-topic", orderInsertDto.getCodeList());

        return new ResponseEntity<>(HttpStatus.OK);
    }

  ...

}
```
<br><br>


**KafkaProducer.java** </br>
``` java
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public List<ItemRequest> send(String topic, List<ItemRequest> itemRequestList) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(itemRequestList);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer sent data from the Order microservice: " + itemRequestList);

        return itemRequestList;
    }
}
```
<br><br>

**Item-Service**
---

'Item-Service'는 Subscriber로서 'item-topic'을 구독한다.<br>
발행된 메시지를 가져와서 parsing하여 주문 아이템 리스트를 추출한다.<br>
추출한 데이터를 기반으로 재고 차감을 진행한다.</br>

</br>

**KafkaProducer.java** </br>

``` java
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ItemRepository itemRepository;

    @Transactional
    @KafkaListener(topics = "item-topic")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka Message: ->" + kafkaMessage);
        HashMap<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Collection<ItemRequest> itemRequestList = mapper.readValue(kafkaMessage,
                    new TypeReference<Collection<ItemRequest>>() {});
            for (ItemRequest request : itemRequestList) {
                Item item = itemRepository.findById(request.getItemCode()).orElse(null);
                if (item.getStock() < request.getQuantity()) {
                    throw new StockShortageException(
                      String.format("Item[%d] : 재고가 부족합니다.", item.getCode()));
                }
                item.decreaseStock(request.getQuantity());
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}
```

