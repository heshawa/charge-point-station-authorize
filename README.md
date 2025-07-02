
# 🛡️ Charge Point Station Authorization Service

This Kotlin-based Spring Boot microservice handles the **authorization of EV charging sessions** by validating station, client, and vehicle permissions before allowing a charge to proceed. It receives callback requests from stations and processes them to approve or reject charging attempts.

---

## 📌 Core Responsibilities

- ✅ Validate access control to EV charging stations
- 🔄 Process callbacks from charging stations
- 🔐 Determine if a vehicle is authorized to use a requested station
- 🧠 Maintain service-level request context for audit and traceability

---

## 🛠️ Tech Stack

| Layer         | Technology                    |
|--------------|-------------------------------|
| Language      | Kotlin (JVM 17)               |
| Framework     | Spring Boot 3.x               |
| Build Tool    | Gradle                        |
| DB Access     | Spring Data JPA               |
| Validation    | Jakarta Bean Validation       |
| Concurrency   | Kotlin Coroutines             |
| Configuration | `application.properties`      |
| DB (Dev)      | H2 (in-memory)                |

---

## 🚀 Getting Started

### ✅ Prerequisites

- JDK 17+
- Gradle
- Kafka (optional)

### 🧪 Clone & Build

```bash
git clone https://github.com/heshawa/charge-point-station-authorize.git
cd charge-point-station-authorize
./gradlew clean build
```

### ▶️ Run the App

```bash
./gradlew bootRun
```

App will be accessible at:
```
http://localhost:8081
```

---

## 🔗 API Endpoint

Base path: `/chargepoint/v1/api/station`

### 🔁 POST `/callback`

Used by stations to request authorization before charging.

**Request Body** (`CallbackRequestBody.kt`)
```json
{
  "driver_token":"c85ac7bc-1d92-4b5a-8884-a05990966d55",
  "station_id":"23dcc323-6a07-4320-8368-d809db1f2b1f",
  "status":"allowed"
}
```

**Response**
```json
{
  "success": false
}
```

✅ Response includes `ChargingApprovalStatus` and internal `RequestStatus`.

---

## 🧠 Service Layer Overview

- `StationAuthorizationService`: Validates if a given vehicle/client is authorized
- `StationACLService`: Handles access control logic and rule evaluation
- `ServiceRequestContext`: Maintains thread-local request data for audit tracking

---

## ⚙️ Configuration Example

`src/main/resources/application.properties`
```properties
server.port=8081
spring.kafka.consumer.group.id=my-consumer-group
kafka.topics.charge-point-server-request=charge-point-service-request
spring.kafka.bootstrap-servers=pkc-921jm.us-east-2.aws.confluent.cloud:9092
```

---

## 🔐 TODO & Improvements

- Add Swagger/OpenAPI documentation
- Persist access control policies to database
- Unit and integration tests
- Add Docker support for easier deployment
- Add Kafka event listeners for audit trail

---

## 👨‍💻 Maintainer

Developed and maintained by [@heshawa](https://github.com/heshawa)

---