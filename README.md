# OnlineBookStore

# 온라인 서점

- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [온라인서점](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 서비스 시나리오

기능적 요구사항
1. 고객이 도서를 주문한다.
2. 고객이 주문을 취소할 수 있다.
3. 주문이 성공하면 배송을 시작한다.
4. 주문이 취소되면 배송을 취소한다.
5. 관리자가 신규도서를 등록한다.
6. 관리자가 도서 재고를 추가한다.
7. 고객은 회원가입을 한다.
8. 도서 주문 실적에 따라 고객의 마일리지 및 등급을 관리한다.
9. 신규 도서가 등록되면 기존 고객에게 알려준다. 
10. 도서가 재입고되면 재고부족으로 못 구매한 고객에게 알려준다. 


비기능적 요구사항
1. 트랜잭션
    1. 주문 시 재고가 부족할 경우 주문이 되지 않는다. (Sync 호출)
1. 장애격리
    1. 고객/마케팅/배달 관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    2. 고객시스템이 과중되면 사용자를 잠시동안 받지 않고 재접속하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 고객이 마이페이지 통해 주문상태를 확인할 수 있어야 한다  CQRS
    2. 신규 도서가 등록되면 가입회원들에게 알림을 줄 수 있어야 한다  Event driven


# 분석/설계


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:


### 이벤트 도출
![image](https://user-images.githubusercontent.com/20077391/118743015-7868d300-b88c-11eb-9460-cca173f9495b.png)

### 부적격 이벤트 탈락
![image](https://user-images.githubusercontent.com/20077391/118743271-fe851980-b88c-11eb-846a-b429ae67849c.png)


### 완성된 1차 모형

![image](https://user-images.githubusercontent.com/9324206/118837153-8146bc80-b8ff-11eb-8062-360768763a8d.png)

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

![image](https://user-images.githubusercontent.com/9324206/118841865-a63d2e80-b903-11eb-90ed-e044f2feb128.png)

    1. 고객이 도서를 주문한다.
    2. 고객이 주문을 취소할 수 있다.
    3. 주문이 성공하면 배송을 시작한다.
    4. 주문이 취소되면 배송을 취소한다.

![image](https://user-images.githubusercontent.com/9324206/118842116-e13f6200-b903-11eb-899b-b415d084e314.png)

    1. 관리자가 신규도서를 등록한다.
    2. 신규 도서가 등록되면 기존 고객에게 알려준다.
    3. 관리자는 도서 재고를 추가한다.
    4. 도서 재고가 추가되면 재고부족으로 못 구매한 고객에게 알려준다.
    
![image](https://user-images.githubusercontent.com/9324206/118843424-02ed1900-b905-11eb-9f30-502574dc47cc.png)

    1. 고객은 회원가입을 한다.
    2. 도서 주문 실적에 따라 고객의 마일리지 및 등급을 관리한다.
    
    
### 비기능 요구사항에 대한 검증

![image](https://user-images.githubusercontent.com/9324206/118844711-249ad000-b906-11eb-9e37-42863a2b27ca.png)

    1. 신규 주문이 들어올 시 재고를 Sync 호출을 통해 확인하여 결과에 따라 주문 성공 여부가 결정.
    2. 고객/마케팅/배달 각각의 기능은 Async (event-driven) 방식으로 통신, 장애 격리가 가능.
    3. MyPage 를 통해 고객이 주문의 상태를 확인.

## 헥사고날 아키텍처 다이어그램 도출

![image](https://user-images.githubusercontent.com/84316082/120965636-238bee80-c7a0-11eb-80b4-f22239207caa.png)
    
    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd gateway
mvn spring-boot:run

cd Book
mvn spring-boot:run 

cd customer
mvn spring-boot:run  

cd CustomerCenter
mvn spring-boot:run  

cd Delivery
mvn spring-boot:run  

cd Order
mvn spring-boot:run  
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 Order 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 

```
package onlinebookstore;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.Date;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long orderId;
    private Long bookId;
    private Integer qty;
    private Integer price;
    private Integer paymentId;
    private Long customerId;
    private Date orderDt;
    private String status;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public Date getOrderDt() {
        return orderDt;
    }

    public void setOrderDt(Date orderDt) {
        this.orderDt = orderDt;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (MySQL or h2) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package onlinebookstore;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="orders", path="orders")
public interface OrderRepository extends PagingAndSortingRepository<Order, Long>{


}

```
- 적용 후 REST API 의 테스트
```
# Order 서비스의 주문처리
http POST localhost:8088/orders bookId=3 qty=1 customerId=3

# Book 서비스의 재고 등록처리
http POST localhost:8088/books title="Book Title" stock=30

# 주문 상태 확인
http GET localhost:8088/orders/1

```

# GateWay 적용
API GateWay를 통하여 마이크로 서비스들의 집입점을 통일할 수 있다.
다음과 같이 GateWay를 적용하였다.

``` (gateway) application.yaml

server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: CustomerCenter
          uri: http://localhost:8081
          predicates:
            - Path= /myPages/**
        - id: Book
          uri: http://localhost:8082
          predicates:
            - Path=/books/** 
        - id: Order
          uri: http://localhost:8083
          predicates:
            - Path=/orders/** 
        - id: Delivery
          uri: http://localhost:8084
          predicates:
            - Path=/deliveries/** 
        - id: customer
          uri: http://localhost:8085
          predicates:
            - Path=/customers/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: customercenter
          uri: http://customercenter:8080
          predicates:
            - Path= /marketingTargets/**,/outOfStockOrders/**,/myPages/**
        - id: Book
          uri: http://Book:8080
          predicates:
            - Path=/books/** 
        - id: Order
          uri: http://Order:8080
          predicates:
            - Path=/orders/** 
        - id: Delivery
          uri: http://Delivery:8080
          predicates:
            - Path=/deliveries/** 
        - id: customer
          uri: http://customer:8080
          predicates:
            - Path=/customers/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080


```

# CQRS
Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능하게 구현해 두었다.
본 프로젝트에서 View 역할은 CustomerCenter 서비스가 수행한다.

- 주문(ordered) 실행 후 myPage 화면

![증빙2] ![image](https://user-images.githubusercontent.com/20077391/120961319-91341c80-c798-11eb-8081-efec0fff119f.png)


- 주문취소(OrderCancelled) 후 myPage 화면

![증빙3] ![image](https://user-images.githubusercontent.com/20077391/120961678-3d760300-c799-11eb-829c-16f296d61f27.png)


위와 같이 주문을 하게되면 Order -> Book -> Order -> Delivery 로 주문이 Assigend 되고

주문 취소가 되면 Status가 "Delivery Cancelled" Update 되는 것을 볼 수 있다.

또한 Correlation을 key를 활용하여 orderId를 Key값을 하고 원하는 주문하고 서비스간의 공유가 이루어 졌다.

위 결과로 서로 다른 마이크로 서비스 간에 트랜잭션이 묶여 있음을 알 수 있다.


## 폴리글랏 퍼시스턴스

Book, CustomerCenter, Customer, Delivery는 MySQL 을 이용하며, Order는 H2 DB를 File Mode로 이용한다.

```
# (Book) application.yml

spring:
  profiles: default
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/bookdb?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: bookAdmin
    password: book
...

# (Order) application.yml

spring:
  profiles: default
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:file:/data/orderdb
    username: sa
    password: 
```

## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 주문(Order)->책 재고 확인(Book) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 재고 확인 서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# (Order) BookService.java


package onlinebookstore.external;

@FeignClient(name="Book", url="${api.url.book}", fallbackFactory = BookServiceFallbackFactory.class)
public interface BookService {

    @RequestMapping(method= RequestMethod.GET, path="/books/chkAndModifyStock")
    public boolean chkAndModifyStock(@RequestParam("bookId") Long bookId,
                                        @RequestParam("qty") int qty);

}

# (Order) BookServiceFallbackFactory.java FallBack 처리 

package onlinebookstore.external;

@Component
public class BookServiceFallbackFactory implements FallbackFactory<BookService> {

    @Override
    public BookService create(Throwable cause) { 
        return new BookService() {
            @Override
            public boolean chkAndModifyStock(Long bookId, int qty) {
                // HystrixTimeoutException 일 경우 Book 재고 회복
                if ( cause instanceof com.netflix.hystrix.exception.HystrixTimeoutException ) {
                    // kafka에 이벤트 발생(Book 재고 회복)
                    ChkAndModifyStockFallBacked chkAndModifyStockFallBacked = new ChkAndModifyStockFallBacked();
                    chkAndModifyStockFallBacked.setBookId(bookId);
                    chkAndModifyStockFallBacked.setQty(qty);
                    chkAndModifyStockFallBacked.publish();
                    System.out.println("** PUB :: ChkAndModifyStockFallBacked (by HystrixTimeoutException)");

                // Hystrix circuit OPEN 일 경우 Book 재고 회복 불필요
                } else {
                    System.out.println("####### BookServiceFallbacked kind ########");
                    System.out.println("####### " + cause.getMessage());
                }

                return false;
            }
        };
    }
}
```

- 주문을 받은 직후 재고(Book) 확인을 요청하도록 처리
```
# BookController.java

package onlinebookstore;

 @RestController
 public class BookController {
     @Autowired  BookRepository bookRepository;

     @RequestMapping(value = "/books/chkAndModifyStock",
             method = RequestMethod.GET,
             produces = "application/json;charset=UTF-8")
     public boolean chkAndModifyStock(@RequestParam("bookId") Long bookId,
                                      @RequestParam("qty")  int qty)
             throws Exception {
             
         boolean status = false;
         Optional<Book> bookOptional = bookRepository.findByBookId(bookId);
         if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            // 현 재고보다 주문수량이 적거나 같은경우에만 true 회신
            if( book.getStock() >= qty){
                status = true;
                book.setStockBeforeUpdate(book.getStock());
                book.setStock(book.getStock() - qty); // 주문수량만큼 재고 감소
                bookRepository.save(book);
         }
      }

      return status;
  }

```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 재고 관리 시스템이 장애가 나면 주문도 못받는다는 것을 확인:


```
# 책 재고 관리 (Book) 서비스를 잠시 내려놓음 (ctrl+c)

#주문처리
http POST localhost:8088/orders bookId=1 qty=10 customerId=1   #Fail
http POST localhost:8088/orders bookId=2 qty=20 customerId=2   #Fail

#재고 관리 서비스 재기동
cd Book
mvn spring-boot:run

#주문처리
http POST localhost:8088/orders bookId=1 qty=10 customerId=1   #Success
http POST localhost:8088/orders bookId=2 qty=20 customerId=2   #Success
```

- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커, 폴백 처리는 운영단계에서 설명한다.)




## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

주문이 이루어진 후에 배송 시스템으로 이를 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여 배송 시스템의 처리를 위하여 주문이 블로킹 되지 않도록 처리한다.
 
- 이를 위하여 주문이력에 기록을 남긴 후에 곧바로 주문이 완료되었다는 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
package onlinebookstore;

@Entity
@Table(name="Order_table")
public class Order {

 ...
    @PostPersist
    public void onPostPersist(){
        if(this.status.equals("Ordered"))
        {
            Ordered ordered = new Ordered();
            BeanUtils.copyProperties(this, ordered);
            ordered.publishAfterCommit();
            System.out.println("** PUB :: Ordered : orderId="+this.orderId);
        }
        else
        {
            OutOfStocked outOfStocked = new OutOfStocked();
            BeanUtils.copyProperties(this, outOfStocked);
            outOfStocked.publish();
            System.out.println("** PUB :: OutOfStocked : orderId="+this.orderId);
        }
    }

}
```
- 배송관리 서비스에서는 주문 완료 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
package onlinebookstore;

...

@Service
public class PolicyHandler{

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_Delivery(@Payload Ordered ordered){

        if(!ordered.validate()) return;

        System.out.println("\n\n##### listener Delivery : " + ordered.toJson() + "\n\n");

        Delivery delivery = new Delivery();
        
        delivery.setOrderid(ordered.getOrderId());
        delivery.setDeliverystatus("Order-Delivery");         
        
        deliveryRepository.save(delivery);
            
    }
}

```

배송 시스템은 주문/재고관리와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 배송 시스템이 유지보수로 인해 잠시 내려간 상태라도 주문을 받는데 문제가 없다:
```
# 배송관리 서비스 (Delivery) 를 잠시 내려놓음 (ctrl+c)

#주문처리
http POST localhost:8088/orders bookId=1 qty=10 customerId=1   #Success
http POST localhost:8088/orders bookId=2 qty=20 customerId=2   #Success

#주문상태 확인
http localhost:8088/orders     # 주문상태 안바뀜 확인

#배송 서비스 기동
cd Delivery
mvn spring-boot:run

#주문상태 확인
http localhost:8080/orders     # 모든 주문의 상태가 "Delivery Started"로 확인
```


# 운영

# Deploy / Pipeline

- git에서 소스 가져오기
```
git clone https://github.com/aramidhwan/OnlineBookStore.git
```
- Build 하기
```
cd /book
mvn package

cd ../customer
mvn package

cd ../customercenter
mvn package

cd ../order
mvn package

cd ../delivery
mvn package

cd ../gateway
mvn package

```

- Docker Image build/Push/
```

cd ../gateway
docker build -t skccteam2acr.azurecr.io/gateway:latest .
docker push skccteam2acr.azurecr.io/gateway:latest

cd ../book
docker build -t skccteam2acr.azurecr.io/book:latest .
docker push skccteam2acr.azurecr.io/book:latest

cd ../customer
docker build -t skccteam2acr.azurecr.io/customer:latest .
docker push skccteam2acr.azurecr.io/customer:latest

cd ../customercenter
docker build -t skccteam2acr.azurecr.io/customercenter:latest .
docker push skccteam2acr.azurecr.io/customercenter:latest

cd ../order
docker build -t skccteam2acr.azurecr.io/order:latest .
docker push skccteam2acr.azurecr.io/order:latest

cd ../delivery
docker build -t skccteam2acr.azurecr.io/delivery:latest .
docker push skccteam2acr.azurecr.io/delivery:latest


```

- yml파일 이용한 deploy
```
kubectl apply -f deployment.yml

- OnlineBookStore/Order/kubernetes/deployment.yml 파일 
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order
  namespace: onlinebookstore
  labels:
    app: order
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order
  template:
    metadata:
      labels:
        app: order
    spec:
      containers:
        - name: order
          image: skccteam2acr.azurecr.io/order:latest
          volumeMounts:
            - mountPath: "/data"
              name: order-vol
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
          env:
            - name: configmap
              valueFrom:
                configMapKeyRef:
                  name: resturl
                  key: url
          resources:
            requests:
              cpu: 300m
              # memory: 256Mi
            limits:
              cpu: 500m
              # memory: 256Mi
      volumes:
      - name: order-vol
        persistentVolumeClaim:
          claimName: orderh2-pvc
```	  

- deploy 완료

![image](https://user-images.githubusercontent.com/20077391/120963003-cc841a80-c79b-11eb-81ff-015a63cdf7ec.png)


# ConfigMap 
- 시스템별로 변경 가능성이 있는 설정들을 ConfigMap을 사용하여 관리

- application.yml 파일에 ${configmap} 설정


![image](https://user-images.githubusercontent.com/20077391/120963090-f0dff700-c79b-11eb-88b4-247efe73a301.png)


- ConfigMap 생성

```
kubectl create configmap resturl --from-literal=url=http://Book:8080

```

   ![image](https://user-images.githubusercontent.com/20077391/120963390-76fc3d80-c79c-11eb-98d5-cd14dccf8ed1.png)


- ConfigMap 사용(/Order/src/main/java/onlinebookstore/external/BookService.java) 


![image](https://user-images.githubusercontent.com/20077391/120964977-24705080-c79f-11eb-8e5b-be9f8e6d2128.png)


- Deployment.yml 에 ConfigMap 적용

![image](https://user-images.githubusercontent.com/20077391/120965103-58e40c80-c79f-11eb-8abd-d3a98048166e.png)


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 주문(Order)-->재고(Book) 확인 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 주문 요청에 대한 재고확인이 3초를 넘어설 경우 Circuit Breaker 를 통하여 장애격리.

- Hystrix 를 설정:  FeignClient 요청처리에서 처리시간이 3초가 넘어서면 CB가 동작하도록 (요청을 빠르게 실패처리, 차단) 설정
                    추가로, 테스트를 위해 1번만 timeout이 발생해도 CB가 발생하도록 설정
```
# application.yml
```
![image](https://user-images.githubusercontent.com/20077391/120970089-ed516d80-c7a5-11eb-8abb-d57cdbf77065.png)

- 피호출 서비스(책재고:Book)에서 테스트를 위해 bookId가 2인 주문건에 대해 sleep 처리
```
# (Book) BookController.java (Entity)
```

![image](https://user-images.githubusercontent.com/20077391/120971537-b54b2a00-c7a7-11eb-9595-8fa8cb444be5.png)




* 서킷 브레이커 동작 확인:

bookId가 1번 인 경우 정상적으로 주문 처리 완료
```
# http POST http://52.141.32.129:8080/orders bookId=1 customerId=4 qty=1
```
![image](https://user-images.githubusercontent.com/20077391/120970620-a152f880-c7a6-11eb-843a-855d85678638.png)

bookId가 2번 인 경우 CB에 의한 timeout 발생 확인
```
# http POST http://52.141.32.129:8080/orders bookId=2 customerId=4 qty=1
```
![image](https://user-images.githubusercontent.com/20077391/120970699-bcbe0380-c7a6-11eb-8c71-ad71101ca1dc.png)

time 아웃이 연달아 2번 발생한 경우 CB가 OPEN되어 Book 호출이 아예 차단된 것을 확인 (테스트를 위해 circuitBreaker.requestVolumeThreshold=1 로 설정)

![image](https://user-images.githubusercontent.com/20077391/120970889-fabb2780-c7a6-11eb-9ab9-e44700c270a7.png)


일정시간 뒤에는 다시 주문이 정상적으로 수행되는 것을 알 수 있다.

![image](https://user-images.githubusercontent.com/20077391/120973450-ea587c00-c7a9-11eb-863b-f15dda3bdaa9.png)


- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌.


### 오토스케일 아웃
주문 서비스가 몰릴 경우를 대비하여 자동화된 확장 기능을 적용하였다.

- 주문서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 테스트를 위해 CPU 사용량이 50프로를 넘어서면 replica 를 3개까지 늘려준다:
```
Order의 hpa.yml

```
![image](https://user-images.githubusercontent.com/20077391/120973949-8aaea080-c7aa-11eb-80ce-eccb3c8cbc0d.png)

- 100명이 60초 동안 주문을 넣어준다.
```
siege -c100 -t60S -r10 --content-type "application/json" 'http://52.141.32.129:8080/orders POST {"bookId":"1","customerId":"1","qty":"1"}

```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
kubectl get deploy -l app=order -w
```
- 어느정도 시간이 흐른 후 스케일 아웃이 벌어지는 것을 확인할 수 있다:

![image](https://user-images.githubusercontent.com/20077391/120974885-9babe180-c7ab-11eb-9a84-07bfb408ed34.png)

- siege 의 로그를 보면 오토스케일 확장이 일어나며 주문을 100% 처리완료한 것을 알 수 있었다.
```
** SIEGE 4.0.4
** Preparing 100 concurrent users for battle.
The server is now under siege...
Lifting the server siege...
Transactions:                   2904 hits
Availability:                 100.00 %        
Elapsed time:                  59.64 secs     
Data transferred:               0.90 MB       
Response time:                  2.02 secs     
Transaction rate:              48.69 trans/sec
Throughput:                     0.02 MB/sec   
Concurrency:                   98.52
Successful transactions:        2904
Failed transactions:               0
Longest transaction:           13.62
Shortest transaction:           0.11

```


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://localhost:8081/orders POST {"item": "chicken"}'

** SIEGE 4.0.5
** Preparing 100 concurrent users for battle.
The server is now under siege...

HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
:

```

- 새버전으로의 배포 시작
```
kubectl set image ...
```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인
```
Transactions:		        3078 hits
Availability:		       70.45 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```
배포기간중 Availability 가 평소 100%에서 70% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함:

```
# deployment.yaml 의 readiness probe 의 설정:


kubectl apply -f kubernetes/deployment.yaml
```

- 동일한 시나리오로 재배포 한 후 Availability 확인:
```
Transactions:		        3078 hits
Availability:		       100 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.


# 신규 개발 조직의 추가

  ![image](https://user-images.githubusercontent.com/487999/79684133-1d6c4300-826a-11ea-94a2-602e61814ebf.png)


## 마케팅팀의 추가
    - KPI: 신규 고객의 유입률 증대와 기존 고객의 충성도 향상
    - 구현계획 마이크로 서비스: 기존 customer 마이크로 서비스를 인수하며, 고객에 음식 및 맛집 추천 서비스 등을 제공할 예정

## 이벤트 스토밍 
    ![image](https://user-images.githubusercontent.com/487999/79685356-2b729180-8273-11ea-9361-a434065f2249.png)


## 헥사고날 아키텍처 변화 

![image](https://user-images.githubusercontent.com/487999/79685243-1d704100-8272-11ea-8ef6-f4869c509996.png)

## 구현  

기존의 마이크로 서비스에 수정을 발생시키지 않도록 Inbund 요청을 REST 가 아닌 Event 를 Subscribe 하는 방식으로 구현. 기존 마이크로 서비스에 대하여 아키텍처나 기존 마이크로 서비스들의 데이터베이스 구조와 관계없이 추가됨. 

## 운영과 Retirement

Request/Response 방식으로 구현하지 않았기 때문에 서비스가 더이상 불필요해져도 Deployment 에서 제거되면 기존 마이크로 서비스에 어떤 영향도 주지 않음.

* [비교] 결제 (pay) 마이크로서비스의 경우 API 변화나 Retire 시에 app(주문) 마이크로 서비스의 변경을 초래함:

예) API 변화시
```
# Order.java (Entity)

    @PostPersist
    public void onPostPersist(){

        fooddelivery.external.결제이력 pay = new fooddelivery.external.결제이력();
        pay.setOrderId(getOrderId());
        
        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제(pay);

                --> 

        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제2(pay);

    }
```

예) Retire 시
```
# Order.java (Entity)

    @PostPersist
    public void onPostPersist(){

        /**
        fooddelivery.external.결제이력 pay = new fooddelivery.external.결제이력();
        pay.setOrderId(getOrderId());
        
        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제(pay);

        **/
    }
```
