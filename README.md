# Automated Parking & Ticketing System

REST API for parking spot management and vehicle ticketing built with Spring Boot.

## Stack
Java 17 · Spring Boot 3 · Spring Data JPA · H2 (In-Memory) · Lombok · Maven

---

H2 Console: `http://localhost:8080/h2-console` → JDBC URL: `jdbc:h2:mem:testdb`

---

## Spot Types & Pricing

| Type | Per Hour |
|---|---|
| COMPACT | ₹10 |
| LARGE | ₹20 |
| HANDICAPPED | ₹5 |

---

## API Reference

### Spots `/api/spots`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Create single spot |
| POST | `/bulk` | Create multiple spots |
| GET | `/` | Get all spots |
| GET | `/{id}` | Get by ID |
| GET | `/type/{spotType}` | Get by type |
| GET | `/available` | Get available spots |
| PUT | `/{id}` | Update spot |
| DELETE | `/{id}` | Delete spot |

### Tickets `/api/tickets`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/entry` | Vehicle entry — assigns spot, generates ticket |
| POST | `/exit` | Vehicle exit — calculates fee, frees spot |
| GET | `/{vehicleNumber}` | Get active ticket |

---

## Transaction Design

| Operation | Config | Reason |
|---|---|---|
| Vehicle Entry | `@Transactional(isolation = REPEATABLE_READ)` | Prevents race condition on spot assignment |
| Vehicle Exit | `@Transactional` | Atomically updates fee + ticket + spot availability |
| Create Bulk Spots | `@Transactional` | All spots saved or none — full rollback on failure |
| Update / Delete Spot | `@Transactional` | Atomic write operation |

---

## Tests — `ParkingTransactionTest`


| Test | Scenario |
|---|---|
| `vehicleEntry_success` | Happy path — spot assigned, ticket generated |
| `vehicleEntry_alreadyParked_throwsException` | Same vehicle enters twice — throws `IllegalArgumentException` |
| `vehicleEntry_noSpotAvailable_throwsException` | No free spot left — throws `IllegalArgumentException` |
| `vehicleExit_invalidTicket_throwsException` | Exit with fake ticket number — throws exception |
| `vehicleExit_alreadyExited_throwsException` | Double exit on same ticket — throws `IllegalArgumentException` |
| `vehicleEntry_concurrentRequests_onlyOneGetsSpot` | 2 threads race for 1 spot — exactly 1 succeeds, 1 fails |

> `@DirtiesContext` resets full Spring context before each test — guaranteed clean DB state.  
> Concurrency test uses `CountDownLatch` to fire both threads simultaneously, validating pessimistic lock holds under real race conditions.

---

## Key Design Decisions

- Minimum **1 hour** fee charged regardless of actual duration
- Ticket format: `TKT-{TypeInitial}-{yyyyMMdd}-{UUID[4]}` → e.g. `TKT-C-20240427-AB12`

