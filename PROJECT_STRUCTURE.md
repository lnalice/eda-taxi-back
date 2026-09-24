# EDA Taxi Back - 프로젝트 구조 분석

## 1. 개요

가상 택시 서비스를 시뮬레이션하는 **이벤트 기반 마이크로서비스(EDA)** 백엔드 프로젝트.
3개의 마이크로서비스가 **Apache Kafka**를 이벤트 브로커로 사용하여 비동기 통신한다.

| 항목 | 값 |
|---|---|
| 프레임워크 | Spring Boot 3.1.6 |
| 빌드 | Maven (모듈별 독립 `pom.xml` + `mvnw`) |
| Java | 21 (서비스), 17 (base-domain) |
| 이벤트 브로커 | Apache Kafka (`localhost:9092`) |
| 로컬 인프라 | `docker-compose.kafka.yml` (Zookeeper + Kafka) |
| 모니터링 | Spring Actuator + Micrometer Prometheus |

---

## 2. 디렉터리 구조

```text
eda-taxi-back/
├── base-domain/              # 공통 DTO / 이벤트 모델 라이브러리
├── user-service/             # 사용자 서비스 (콜 발행, 응답 수신)
├── taxi-platform/            # 플랫폼 서비스 (이벤트 중계, DLT 모니터링)
├── driver-service/           # 드라이버 서비스 (위치 발행, 배차 응답)
├── docker-compose.kafka.yml  # Kafka + Zookeeper 로컬 실행
├── .env.example              # 환경변수 템플릿
├── README.md                 # 원본 프로젝트 소개
└── PROJECT_STRUCTURE.md      # 이 문서
```

> 루트에 통합 `pom.xml`이 없으며, 각 폴더가 **독립적인 Maven 프로젝트**로 존재한다.
> `base-domain`을 `mvn install`로 로컬 저장소에 설치한 뒤, 나머지 서비스가 의존성으로 참조하는 구조이다.

---

## 3. 서비스 포트

| 서비스 | 기본 포트 | 환경변수 |
|---|---|---|
| driver-service | `8080` | `SERVER_PORT` |
| taxi-platform | `8081` | `SERVER_PORT` |
| user-service | `8082` | `SERVER_PORT` |

---

## 4. 모듈 상세

### 4.1. `base-domain` (공통 도메인 라이브러리)

다른 세 서비스가 Maven 의존성(`net.taxiMap:base-domain:0.0.1-SNAPSHOT`)으로 참조하는 공유 모듈.

**DTO**

| 클래스 | 필드 | 설명 |
|---|---|---|
| `User` | userID, currentLocation, departure, arrival | 사용자 정보 |
| `Driver` | driverID, currentLocation, status(`A`/`R`/`P`) | 드라이버 정보 및 상태 |
| `Location` | latitude, longitude | 위경도 좌표 |

**이벤트 (DTO)**

| 클래스 | 필드 | 발행 시점 |
|---|---|---|
| `CallEvent` | currentTime, user | 사용자가 택시 호출 |
| `CallResponseEvent` | currentTime, driver, user | 배차 결과를 사용자에게 전달 |
| `RideEvent` | currentTime, driver, user | 플랫폼이 드라이버에게 배차 요청 |
| `RideResponseEvent` | currentTime, driver, user | 드라이버가 배차 수락 |
| `TaxiLocationEvent` | currentTime, driver | 택시 실시간 위치 갱신 |
| `PickUpEvent` | currentTime, driver | 승객 탑승 |
| `DropOffEvent` | currentTime, driver | 승객 하차 |

**의존성**: `spring-boot-starter`, `lombok`

---

### 4.2. `user-service` (사용자 서비스)

사용자의 택시 호출 요청을 받아 이벤트를 발행하고, 배차 응답을 수신하는 서비스.

**패키지 구조**

```text
net.taxiMap.userservice/
├── UserServiceApplication.java          # @SpringBootApplication
├── UserController.java                  # REST 컨트롤러
├── config/
│   ├── KafkaTopicConfig.java            # 토픽 빈 (Call, CallResponse.DLT)
│   └── KafkaListenerConfig.java         # 공통 에러핸들러 + DLT 라우팅
└── kafka/
    ├── UserProducer.java                # CallEvent 발행
    └── UserConsumer.java                # CallResponseEvent 구독
```

**HTTP API**

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/CALL` | `User` JSON을 받아 `CallEvent` 발행 |

**Kafka 토픽**

| 방향 | 토픽 |
|---|---|
| Produce | `Call` |
| Consume | `CallResponse` |
| DLT 생성 | `CallResponse.DLT` |

**의존성**: `spring-boot-starter-web`, `spring-boot-starter-actuator`, `spring-kafka`, `base-domain`

---

### 4.3. `taxi-platform` (플랫폼 서비스)

이벤트를 수신해 변환/중계하는 **허브** 역할. DLT 모니터링과 Prometheus 메트릭 수집도 담당한다.

**패키지 구조**

```text
net.taxiMap.taxiplatform/
├── TaxiPlatformApplication.java                 # @SpringBootApplication
├── config/
│   ├── KafkaTopicConfig.java                    # 발행 토픽 + 전체 DLT 토픽 빈
│   └── KafkaListenerConfig.java                 # 에러핸들러 + DLT 전용 컨테이너 팩토리
├── kafka/
│   ├── TaxiPlatformConsumer.java                # TaxiLocation, PickUp, DropOff 구독
│   ├── RideProducer.java                        # RideEvent 발행
│   ├── CallResponseProducer.java                # CallResponseEvent 발행
│   ├── DltEventMonitorConsumer.java             # 전체 DLT 토픽 구독 + 메트릭/로깅
│   └── MessageProcessor/
│       ├── RideMessager.java                    # Call → Ride 변환/발행
│       └── CallResponseMessager.java            # RideResponse → CallResponse 변환/발행
├── controller/
│   ├── TaxiPlatformController.java              # (현재 주석 처리)
│   └── RideController.java                      # SSE 기반 실시간 이벤트 스트림
└── sseEmit/
    └── SseEmitters.java                         # SSE 연결 관리
```

**Kafka 토픽**

| 방향 | 토픽 |
|---|---|
| Consume | `Call`, `RideResponse`, `TaxiLocation`, `PickUp`, `DropOff` |
| Produce | `Ride`, `CallResponse` |
| DLT 생성 | `Call.DLT`, `RideResponse.DLT`, `TaxiLocation.DLT`, `PickUp.DLT`, `DropOff.DLT`, `CallResponse.DLT` |
| DLT 모니터 구독 | 위 전체 DLT + `Ride.DLT` |

**DLT 모니터링 (`DltEventMonitorConsumer`)**

- 모든 `.DLT` 토픽을 `byte[]`로 소비
- Kafka 헤더에서 원본 토픽, 예외 클래스, 예외 메시지, 원본 컨슈머 그룹을 파싱
- `kafka_dlt_events_total` Prometheus 카운터 메트릭 증가 (태그: `topic`, `source_topic`, `exception`)
- 구조화된 에러 로그 출력

**의존성**: `spring-boot-starter-web`, `spring-boot-starter-actuator`, `spring-kafka`, `base-domain`, `lombok`, `micrometer-registry-prometheus`

---

### 4.4. `driver-service` (드라이버 서비스)

택시 위치를 주기적으로 발행하고, 배차 요청에 대한 응답을 생성하는 서비스.

**패키지 구조**

```text
net.taxiMap.driverservice/
├── DriverServiceApplication.java                # @SpringBootApplication + @EnableScheduling
├── config/
│   ├── KafkaTopicConfig.java                    # 토픽 빈 (TaxiLocation, RideResponse, PickUp, DropOff, Ride.DLT)
│   └── KafkaListenerConfig.java                 # 공통 에러핸들러 + DLT 라우팅
├── schedule/
│   └── TaxiLocationService.java                 # 5초마다 driver.json 기반 위치 이벤트 발행
├── kafka/
│   ├── DriverConsumer.java                      # (현재 빈 클래스)
│   ├── producer/
│   │   ├── TaxiLocationProducer.java            # TaxiLocationEvent 발행
│   │   ├── RideResponseProducer.java            # RideResponseEvent 발행
│   │   ├── PickUpProducer.java                  # PickUpEvent 발행
│   │   └── DropOffProducer.java                 # DropOffEvent 발행
│   ├── controller/
│   │   ├── TaxiLocationController.java          # POST /DRIVER/GPS
│   │   ├── PickUpController.java                # POST /DRIVER/PICK-UP
│   │   └── DropOffController.java               # POST /DRIVER/DROP-OFF
│   └── MessageProcessor/
│       └── RideResponseMessager.java            # Ride → RideResponse 변환/발행
└── resources/
    └── data/
        ├── driver/driver.json                   # 시뮬레이션 드라이버 데이터
        └── route/bus10.json ~ bus22.json         # 경로 데이터 (13개)
```

**HTTP API**

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/DRIVER/GPS` | 드라이버 위치 수동 갱신 |
| POST | `/DRIVER/PICK-UP` | 승객 탑승 이벤트 발행 |
| POST | `/DRIVER/DROP-OFF` | 승객 하차 이벤트 발행 |

**스케줄러**: `TaxiLocationService`가 `@Scheduled(fixedDelay=5000)`으로 `driver.json`을 읽어 전체 드라이버 위치를 주기 발행

**Kafka 토픽**

| 방향 | 토픽 |
|---|---|
| Produce | `TaxiLocation`, `RideResponse`, `PickUp`, `DropOff` |
| Consume | `Ride` |
| DLT 생성 | `Ride.DLT` |

**의존성**: `spring-boot-starter-web`, `spring-boot-starter-actuator`, `spring-kafka`, `base-domain`, `gson`

---

## 5. 이벤트 흐름

### 5.1. 콜 → 배차 → 응답

```text
[Client]
   │ POST /CALL (User JSON)
   ▼
[user-service]
   │ produce → 토픽: Call (CallEvent)
   ▼
[taxi-platform] RideMessager
   │ consume ← 토픽: Call
   │ 가장 가까운 드라이버 선정 (현재 샘플 데이터)
   │ produce → 토픽: Ride (RideEvent)
   ▼
[driver-service] RideResponseMessager
   │ consume ← 토픽: Ride
   │ produce → 토픽: RideResponse (RideResponseEvent)
   ▼
[taxi-platform] CallResponseMessager
   │ consume ← 토픽: RideResponse
   │ produce → 토픽: CallResponse (CallResponseEvent)
   ▼
[user-service] UserConsumer
   │ consume ← 토픽: CallResponse
   └ 사용자 화면 갱신 지점
```

### 5.2. 위치 / 상태 이벤트 (지도 갱신용)

```text
[driver-service]
   │ @Scheduled(5s) → 토픽: TaxiLocation (TaxiLocationEvent)
   │ POST /DRIVER/PICK-UP → 토픽: PickUp (PickUpEvent)
   │ POST /DRIVER/DROP-OFF → 토픽: DropOff (DropOffEvent)
   ▼
[taxi-platform] TaxiPlatformConsumer
   └ consume ← TaxiLocation, PickUp, DropOff
     → 지도 상태 갱신 / DB 업데이트 지점
```

### 5.3. 실패 이벤트 흐름 (DLT)

```text
[any consumer] 메시지 처리 실패
   │ 지수 백오프 재시도 (기본 3회, 1s → 2s → 4s)
   │ 최종 실패 시
   ▼
[원본토픽.DLT] (예: Call.DLT, Ride.DLT 등)
   ▼
[taxi-platform] DltEventMonitorConsumer
   ├ 헤더 파싱 (원본 토픽, 예외 클래스, 메시지, 컨슈머 그룹)
   ├ ERROR 로그 구조화 출력
   └ kafka_dlt_events_total 메트릭 증가
```

---

## 6. Kafka 토픽 전체 목록

| 토픽 | 발행자 | 소비자 |
|---|---|---|
| `Call` | user-service | taxi-platform |
| `Ride` | taxi-platform | driver-service |
| `RideResponse` | driver-service | taxi-platform |
| `CallResponse` | taxi-platform | user-service |
| `TaxiLocation` | driver-service | taxi-platform |
| `PickUp` | driver-service | taxi-platform |
| `DropOff` | driver-service | taxi-platform |
| `Call.DLT` | taxi-platform (에러 시) | taxi-platform (DLT 모니터) |
| `Ride.DLT` | driver-service (에러 시) | taxi-platform (DLT 모니터) |
| `RideResponse.DLT` | taxi-platform (에러 시) | taxi-platform (DLT 모니터) |
| `CallResponse.DLT` | user-service (에러 시) | taxi-platform (DLT 모니터) |
| `TaxiLocation.DLT` | taxi-platform (에러 시) | taxi-platform (DLT 모니터) |
| `PickUp.DLT` | taxi-platform (에러 시) | taxi-platform (DLT 모니터) |
| `DropOff.DLT` | taxi-platform (에러 시) | taxi-platform (DLT 모니터) |

---

## 7. 운영 설정

### 7.1. 공통 Kafka Producer 설정 (전체 서비스)

| 설정 | 값 | 목적 |
|---|---|---|
| `acks` | `all` | 모든 ISR 복제본 확인 후 응답 |
| `retries` | `10` | 일시적 장애 시 재전송 |
| `enable.idempotence` | `true` | 중복 메시지 방지 |
| `max.in.flight.requests.per.connection` | `5` | idempotence 호환 최대 동시 요청 |

### 7.2. 공통 Kafka Consumer 설정 (전체 서비스)

| 설정 | 값 | 목적 |
|---|---|---|
| Key/Value Deserializer | `ErrorHandlingDeserializer` | 역직렬화 실패 시에도 컨슈머 중단 방지 |
| `enable-auto-commit` | `false` | 수동 오프셋 커밋 |
| `ack-mode` | `record` | 메시지 단위 커밋 |
| `missing-topics-fatal` | `false` | 토픽 미존재 시 기동 실패 방지 |
| `auto-offset-reset` | `earliest` | 그룹 최초 구독 시 처음부터 소비 |

### 7.3. 에러 처리 / 재시도 정책 (전체 서비스)

| 설정 | 기본값 | 환경변수 |
|---|---|---|
| 최대 재시도 횟수 | 3 | `KAFKA_RETRY_MAX_ATTEMPTS` |
| 초기 간격 | 1,000ms | `KAFKA_RETRY_INITIAL_INTERVAL_MS` |
| 배수 | 2.0 | `KAFKA_RETRY_MULTIPLIER` |
| 최대 간격 | 10,000ms | `KAFKA_RETRY_MAX_INTERVAL_MS` |

재시도 소진 시 `DeadLetterPublishingRecoverer`가 원본 토픽명 + `.DLT` 토픽으로 메시지를 라우팅한다.

### 7.4. 헬스체크 / 모니터링

| 서비스 | 엔드포인트 | 비고 |
|---|---|---|
| 전체 | `/actuator/health` | liveness + readiness probe 활성화 |
| 전체 | `/actuator/info` | 앱 정보 |
| taxi-platform | `/actuator/prometheus` | Prometheus 스크랩용 메트릭 |
| taxi-platform | `/actuator/metrics/kafka_dlt_events_total` | DLT 실패 카운터 |

### 7.5. Graceful Shutdown (전체 서비스)

- `server.shutdown=graceful`
- `spring.lifecycle.timeout-per-shutdown-phase=20s`

---

## 8. 로컬 실행 방법

### 8.1. Kafka 기동

```bash
docker compose -f docker-compose.kafka.yml up -d
```

### 8.2. base-domain 설치 (최초 1회)

```bash
cd base-domain
./mvnw clean install -DskipTests
```

### 8.3. 서비스 기동

```bash
# 터미널 3개에서 각각 실행
cd user-service   && ./mvnw spring-boot:run
cd taxi-platform  && ./mvnw spring-boot:run
cd driver-service && ./mvnw spring-boot:run
```

### 8.4. 테스트 호출

```bash
# 콜 요청
curl -X POST http://localhost:8082/CALL \
  -H "Content-Type: application/json" \
  -d '{"userID":"u001","currentLocation":{"latitude":37.5,"longitude":127.0},"departure":{"latitude":37.5,"longitude":127.0},"arrival":{"latitude":37.55,"longitude":127.05}}'
```

### 8.5. 헬스체크 확인

```bash
curl http://localhost:8080/actuator/health   # driver-service
curl http://localhost:8081/actuator/health   # taxi-platform
curl http://localhost:8082/actuator/health   # user-service
```

---

## 9. 환경변수 목록 (`.env.example`)

| 변수 | 기본값 | 설명 |
|---|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka 브로커 주소 |
| `USER_SERVICE_CONSUMER_GROUP` | `user-service` | user-service 컨슈머 그룹 |
| `DRIVER_SERVICE_CONSUMER_GROUP` | `driver-service` | driver-service 컨슈머 그룹 |
| `TAXI_PLATFORM_CONSUMER_GROUP` | `taxi-platform` | taxi-platform 컨슈머 그룹 |
| `TAXI_PLATFORM_DLT_CONSUMER_GROUP` | `taxi-platform-dlt-monitor` | DLT 모니터 컨슈머 그룹 |
| `TOPIC_CALL` | `Call` | 콜 토픽명 |
| `TOPIC_CALL_RESPONSE` | `CallResponse` | 콜 응답 토픽명 |
| `TOPIC_TAXI_LOCATION` | `TaxiLocation` | 택시 위치 토픽명 |
| `TOPIC_RIDE` | `Ride` | 배차 요청 토픽명 |
| `TOPIC_RIDE_RESPONSE` | `RideResponse` | 배차 응답 토픽명 |
| `TOPIC_PICK_UP` | `PickUp` | 탑승 토픽명 |
| `TOPIC_DROP_OFF` | `DropOff` | 하차 토픽명 |
| `TOPIC_*_DLT` | `*.DLT` | 각 토픽의 Dead Letter Topic |
| `KAFKA_RETRY_MAX_ATTEMPTS` | `3` | 최대 재시도 횟수 |
| `KAFKA_RETRY_INITIAL_INTERVAL_MS` | `1000` | 첫 재시도 간격 |
| `KAFKA_RETRY_MULTIPLIER` | `2.0` | 재시도 간격 배수 |
| `KAFKA_RETRY_MAX_INTERVAL_MS` | `10000` | 재시도 최대 간격 |
| `SERVER_PORT` | 서비스별 상이 | 포트 오버라이드 |
