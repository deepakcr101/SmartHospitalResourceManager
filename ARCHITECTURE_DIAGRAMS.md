# Smart Hospital Resource Manager - Architecture Diagrams

## 1. System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                        REST API CLIENTS                             │
│              (Postman, Frontend, Mobile App)                        │
└────────────────────────────┬────────────────────────────────────────┘
                             │ HTTP/JSON
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      SPRING BOOT 4.0.2                              │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    PRESENTATION LAYER                       │   │
│  │  ┌─────────────────┐  ┌──────────────────┐                 │   │
│  │  │ Hospital        │  │ Verification     │  ┌────────────┐ │   │
│  │  │ Controller      │  │ Controller       │  │ Auth       │ │   │
│  │  │ /api/hospital   │  │ /api/verify      │  │ Controller │ │   │
│  │  └────────┬────────┘  └────────┬─────────┘  └────────────┘ │   │
│  └───────────┼──────────────────┼──────────────────────────────┘   │
│              │                  │                                  │
│  ┌───────────▼──────────────────▼──────────────────────────────┐   │
│  │                    BUSINESS LOGIC LAYER                     │   │
│  │  ┌──────────────────────┐  ┌─────────────────────────────┐ │   │
│  │  │ Scheduling Service   │  │ Impact Analysis Service     │ │   │
│  │  │ - Constraint Check   │  │ - Failure Propagation      │ │   │
│  │  │ - Doctor Selection   │  │ - Blast Radius Calc        │ │   │
│  │  │ - Room Allocation    │  └─────────────────────────────┘ │   │
│  │  └──────────────────────┘                                  │   │
│  │                                                              │   │
│  └────────────────────────┬─────────────────────────────────────┘   │
│                           │                                        │
│  ┌────────────────────────▼─────────────────────────────────────┐   │
│  │                 DATA ACCESS LAYER                            │   │
│  │  ┌─────────────┐  ┌──────────────┐  ┌────────────────────┐ │   │
│  │  │ Doctor Repo │  │ Room Repo    │  │ Equipment Repo     │ │   │
│  │  │ + Cypher    │  │ + Cypher     │  │ Patient Repo       │ │   │
│  │  │ Queries     │  │ Queries      │  │ Procedure Repo     │ │   │
│  │  └──────┬──────┘  └───────┬──────┘  └────────┬───────────┘ │   │
│  │         │                 │                  │              │   │
│  └─────────┼─────────────────┼──────────────────┼──────────────┘   │
│            │                 │                  │                  │
└────────────┼─────────────────┼──────────────────┼──────────────────┘
             │                 │                  │
     ┌───────▼─────────────────▼──────────────────▼────────┐
     │          SPRING DATA NEO4J (ORM/ODM Layer)          │
     └───────────────────────┬──────────────────────────────┘
                             │
          ┌──────────────────▼──────────────────┐
          │  NEO4J GRAPH DATABASE (5.x)        │
          │  ┌──────────────────────────────┐  │
          │  │ Nodes:                       │  │
          │  │  • Doctor                    │  │
          │  │  • Patient                   │  │
          │  │  • Procedure                 │  │
          │  │  • Equipment                 │  │
          │  │  • Room                      │  │
          │  │  • Shift (Future)            │  │
          │  │                              │  │
          │  │ Relationships:               │  │
          │  │  • CAN_PERFORM               │  │
          │  │  • REQUIRES                  │  │
          │  │  • SUITABLE_FOR              │  │
          │  │  • SCHEDULED_FOR             │  │
          │  │  • ASSIGNED_DOCTOR           │  │
          │  └──────────────────────────────┘  │
          └──────────────────────────────────────┘
```

---

## 2. Domain Model (Property Graph)

```
┌─────────────────────┐
│     DOCTOR          │
│                     │
│ • name              │
│ • department        │
│ • isActive          │
└──────────┬──────────┘
           │
           │ CAN_PERFORM (outgoing)
           │
           ▼
┌─────────────────────┐
│    PROCEDURE        │
│                     │
│ • name              │
│ • durationMinutes   │
└──────┬──────────────┘
       │
       │ REQUIRES (outgoing)
       │
       ▼
┌─────────────────────┐
│    EQUIPMENT        │
│                     │
│ • name              │
│ • type              │
│ • status            │◄─────────────────┐
└─────────────────────┘                  │
                                         │
┌──────────────────┐            ┌────────┴──────┐
│   PATIENT        │            │    ROOM       │
│                  │            │               │
│ • name           │            │ • name        │
│ • priority       │            │ • type        │
│                  │            │ • isOccupied  │
└────────┬─────────┘            └────────┬──────┘
         │                              │
         │ SCHEDULED_FOR               │ SUITABLE_FOR
         │ (outgoing)                  │ (outgoing)
         │                             │
         └──────────────┬──────────────┘
                        │
                        ▼
            (both point to PROCEDURE)
```

**Relationship Directions:**
- Doctor → **CAN_PERFORM** → Procedure (Skill mapping)
- Procedure → **REQUIRES** → Equipment (Asset dependency)
- Room → **SUITABLE_FOR** → Procedure (Space compatibility)
- Patient → **SCHEDULED_FOR** → Procedure (Booking)
- Patient → **ASSIGNED_DOCTOR** → Doctor (Resource assignment)

---

## 3. Scheduling Algorithm Flow

```
┌──────────────────────────────────────────────────────────────────┐
│  POST /api/hospital/schedule                                     │
│  Request: { patientName, procedureName, priority }               │
└────────────────────┬─────────────────────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │ STEP 1: Verify         │
        │ Procedure Exists?      │
        └────────┬───────────────┘
                 │
         ┌───────┴────────┐
         │ NO             │ YES
         ▼                ▼
    [FAIL]       ┌───────────────────────┐
     401         │ STEP 2: Check All     │
                 │ Required Equipment    │
                 │ Status = AVAILABLE?   │
                 └───────┬───────────────┘
                         │
                 ┌───────┴────────┐
                 │ NO             │ YES
                 ▼                ▼
             [FAIL]       ┌──────────────────────┐
              402         │ STEP 3: Find Capable │
                          │ Doctor               │
                          │ (Cypher Query)       │
                          │ isActive = true?     │
                          │ CAN_PERFORM?         │
                          └─────┬────────────────┘
                                │
                        ┌───────┴────────┐
                        │ NO             │ YES
                        ▼                ▼
                    [FAIL]       ┌──────────────────┐
                     403         │ STEP 4: Find     │
                                 │ Available Room   │
                                 │ (Cypher Query)   │
                                 │ isOccupied=false?│
                                 │ SUITABLE_FOR?    │
                                 └─────┬────────────┘
                                       │
                               ┌───────┴────────┐
                               │ NO             │ YES
                               ▼                ▼
                           [FAIL]       ┌──────────────┐
                            404         │ STEP 5:      │
                                        │ EXECUTE      │
                                        │ Schedule     │
                                        └──────┬───────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │ Mark Room   │
                                        │ isOccupied  │
                                        │ = true      │
                                        └──────┬──────┘
                                               │
                                               ▼
                                        ┌──────────────────┐
                                        │ Create Patient   │
                                        │ Link:            │
                                        │ • SCHEDULED_FOR  │
                                        │ • ASSIGNED_DOCTOR│
                                        └──────┬───────────┘
                                               │
                                               ▼
                                        ┌──────────────────┐
                                        │ @Transactional   │
                                        │ Commit All       │
                                        └──────┬───────────┘
                                               │
                                               ▼
                                           [SUCCESS]
                                            200 OK
                                        {Patient obj}
```

**Error Response Mapping:**
- 401: Procedure not found
- 402: Equipment unavailable (MAINTENANCE, IN_USE)
- 403: No active doctor with skill
- 404: No suitable room available
- 500: Database/transaction error

---

## 4. Cypher Query Execution Examples

### Query 1: Find Available Doctor
```cypher
MATCH (d:Doctor)-[:CAN_PERFORM]->(p:Procedure)
WHERE p.name = "Open Heart Surgery" AND d.isActive = true
RETURN d LIMIT 1
```

**Execution Plan:**
1. Scan Doctor nodes
2. Filter: d.isActive = true
3. Traverse CAN_PERFORM relationship
4. Filter: p.name = "Open Heart Surgery"
5. Return first result

---

### Query 2: Find Available Room
```cypher
MATCH (r:Room)-[:SUITABLE_FOR]->(p:Procedure)
WHERE p.name = "Open Heart Surgery" AND r.isOccupied = false
RETURN r LIMIT 1
```

**Execution Plan:**
1. Scan Room nodes
2. Filter: r.isOccupied = false
3. Traverse SUITABLE_FOR relationship
4. Filter: p.name = "Open Heart Surgery"
5. Return first result

---

## 5. Testing Architecture

```
┌─────────────────────────────────────────────┐
│         UNIT TESTS (Layer 1)                │
│  ┌──────────────────────────────────────┐   │
│  │ SchedulingServiceTest                │   │
│  │ • Mock all repositories              │   │
│  │ • Test constraint logic              │   │
│  │ • No database needed                 │   │
│  │ • Fast (< 1 second per test)         │   │
│  └──────────────────────────────────────┘   │
│  Effort: 3-4 hours                          │
│  Coverage: 60-70%                           │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│      INTEGRATION TESTS (Layer 2)            │
│  ┌──────────────────────────────────────┐   │
│  │ SchedulingServiceIT                  │   │
│  │ • Real Neo4j (Docker container)      │   │
│  │ • Test Cypher queries                │   │
│  │ • Test relationships load            │   │
│  │ • ~10 seconds per test               │   │
│  └──────────────────────────────────────┘   │
│  Effort: 3-4 hours                          │
│  Coverage: +20% (relationships, queries)    │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│       CONTROLLER TESTS (Layer 3)            │
│  ┌──────────────────────────────────────┐   │
│  │ HospitalControllerIT                 │   │
│  │ • Full Spring context                │   │
│  │ • Test HTTP endpoints                │   │
│  │ • Verify response codes              │   │
│  │ • ~2-3 seconds per test              │   │
│  └──────────────────────────────────────┘   │
│  Effort: 2-3 hours                          │
│  Coverage: +10% (HTTP layer)                │
└─────────────────────────────────────────────┘

Overall Coverage Target: > 80%
```

---

## 6. Security Architecture (Phase 3)

```
┌──────────────────────────────────────────────────────┐
│              HTTP Request                            │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
        ┌──────────────────────────────┐
        │ Spring Security Filter Chain │
        └──────────────┬───────────────┘
                       │
                       ▼
        ┌──────────────────────────────┐
        │ BasicAuthenticationFilter    │
        │ Extract: username:password   │
        └──────────────┬───────────────┘
                       │
                       ▼
        ┌──────────────────────────────┐
        │ UserDetailsService           │
        │ Load: admin, staff users     │
        └──────────────┬───────────────┘
                       │
                       ▼
        ┌──────────────────────────────┐
        │ PasswordEncoder (BCrypt)     │
        │ Verify: password match       │
        └──────────────┬───────────────┘
                       │
                    Success
                       │
                       ▼
        ┌──────────────────────────────┐
        │ SecurityContext              │
        │ Principal: user              │
        │ Authorities: [ROLE_STAFF]    │
        └──────────────┬───────────────┘
                       │
                       ▼
        ┌──────────────────────────────┐
        │ @PreAuthorize Check          │
        │ Does user have required      │
        │ role for this endpoint?      │
        └──────────────┬───────────────┘
                       │
           ┌───────────┴───────────┐
           │ YES                   │ NO
           ▼                       ▼
      [PROCEED]              [403 FORBIDDEN]
       Handler                or 401 UNAUTHORIZED
        executes

┌─────────────────────────────────────────────────┐
│         Endpoint Protection Matrix              │
├──────────────────────┬──────────────┬────────┐
│ Endpoint             │ Role Required│ Public?│
├──────────────────────┼──────────────┼────────┤
│ POST /schedule       │ STAFF, ADMIN │ No     │
│ GET /verify/*        │ STAFF, ADMIN │ No     │
│ POST /admin/add-equip│ ADMIN        │ No     │
│ GET /health          │ None         │ Yes    │
│ GET /swagger-ui      │ None         │ Yes    │
└──────────────────────┴──────────────┴────────┘
```

---

## 7. Data Flow - Successful Schedule

```
REST Client                Application                 Neo4j
    │                           │                        │
    │  POST /schedule           │                        │
    ├──────────────────────────>│                        │
    │  {patientName,            │                        │
    │   procedureName,          │                        │
    │   priority}               │                        │
    │                           │                        │
    │                           │ Query: Procedure       │
    │                           ├───────────────────────>│
    │                           │<───────────────────────┤
    │                           │ Procedure obj          │
    │                           │                        │
    │                           │ Query: Equipment       │
    │                           │ (via REQUIRES rel)     │
    │                           ├───────────────────────>│
    │                           │<───────────────────────┤
    │                           │ [Equipment {status}]   │
    │                           │                        │
    │                           │ Cypher: Doctor         │
    │                           │ (with CAN_PERFORM)     │
    │                           ├───────────────────────>│
    │                           │<───────────────────────┤
    │                           │ Doctor obj             │
    │                           │                        │
    │                           │ Cypher: Room           │
    │                           │ (with SUITABLE_FOR)    │
    │                           ├───────────────────────>│
    │                           │<───────────────────────┤
    │                           │ Room obj               │
    │                           │                        │
    │                           │ Update: Room           │
    │                           │ (isOccupied = true)    │
    │                           ├───────────────────────>│
    │                           │<───────────────────────┤
    │                           │ Success                │
    │                           │                        │
    │                           │ Create: Patient Node   │
    │                           │ Create: SCHEDULED_FOR  │
    │                           │ Create: ASSIGNED_DOCTOR│
    │                           ├───────────────────────>│
    │                           │<───────────────────────┤
    │                           │ Patient created        │
    │                           │                        │
    │  200 OK                   │                        │
    │<──────────────────────────┤                        │
    │  {                        │                        │
    │    "id": 123,             │                        │
    │    "name": "John Doe",    │                        │
    │    "priority": "HIGH",    │                        │
    │    "scheduledProcedures": │                        │
    │      ["Open Heart Surg"]  │                        │
    │  }                        │                        │
    │                           │                        │
```

---

## 8. Future: Impact Analysis Flow

```
┌──────────────────────────────────────────┐
│ GET /api/admin/impact-analysis?          │
│     equipment=MRI%20Scanner%2001          │
└────────────────┬─────────────────────────┘
                 │
                 ▼
    ┌────────────────────────┐
    │ ImpactAnalysisService  │
    │ Input: Equipment name  │
    └────────┬───────────────┘
             │
             ▼
┌───────────────────────────────────────────┐
│ Cypher: Reverse Graph Traversal           │
│                                            │
│ MATCH (e:Equipment {name: "MRI..."})      │
│     <-[:REQUIRES]-(proc:Procedure)        │
│     <-[:SCHEDULED_FOR]-(pat:Patient)      │
│ RETURN e, proc, pat                       │
└────────┬────────────────────────────────┘
         │
         ▼
    ┌────────────────────┐
    │ Collect Results:   │
    │ • Equipment        │
    │ • Procedures       │
    │ • Patients         │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────────────────┐
    │ Generate Impact Report:        │
    │ {                              │
    │   failedResource: "MRI...",    │
    │   affectedProcedures: [...],   │
    │   affectedPatients: [{...}],   │
    │   totalImpact: 5               │
    │ }                              │
    └────────┬───────────────────────┘
             │
             ▼
    200 OK with Report
```

---

## 9. Build & Deployment Pipeline

```
┌──────────────────────────────────────┐
│ git push to main                     │
└────────┬─────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│ Maven Build (pom.xml)                │
│ • Compile source code                │
│ • Run unit tests                     │
│ • Run integration tests              │
│ • Generate WAR/JAR                   │
└────────┬─────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│ Docker Build                         │
│ • Create image with JDK 21           │
│ • Copy JAR                           │
│ • Expose port 8080                   │
└────────┬─────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│ Docker Compose (Dev/Test)            │
│ • Container 1: Spring App (8080)     │
│ • Container 2: Neo4j (7687)          │
│ • Shared network                     │
└────────┬─────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│ Cloud Deployment                     │
│ • Azure Container Instances          │
│ • OR Azure App Service               │
│ • OR Kubernetes (AKS)                │
└────────┬─────────────────────────────┘
         │
         ▼
    Live API
```

---

## 10. Module Dependencies (Dependency Graph)

```
SmartHospitalResourceManagerApplication
├── Spring Boot
│   ├── Spring Web
│   ├── Spring Data Neo4j
│   ├── Spring Security (Phase 3)
│   └── Validation
│
├── Database
│   ├── Neo4j Driver
│   └── Spring Data Neo4j ORM
│
├── Utilities
│   └── Lombok
│
└── Testing (Phase 2)
    ├── JUnit 5
    ├── Mockito
    ├── Testcontainers
    └── Spring Boot Test
```

---

**Legend:**
- `◄─────►` Bidirectional relationship (for clarity)
- `──────►` Unidirectional (actual graph relationships)
- `[FAIL]` Error state
- `[SUCCESS]` Success state
- Time: Horizontal arrows show request/response flow
- Layers: Vertical arrows show call hierarchy

All diagrams are conceptual. Actual implementation may vary a little based on final design decisions.
