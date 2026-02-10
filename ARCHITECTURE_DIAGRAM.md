# Architecture Diagrams

## 1. System Architecture Overview
```mermaid
graph TD
    Client[REST API Clients<br/>(Postman, Frontend, Mobile)] -->|HTTP/JSON| SpringBoot
    subgraph SpringBoot [Spring Boot 4.0.2 Application]
        direction TB
        subgraph Presentation [Presentation Layer]
            HC[HospitalController<br/>/api/hospital]
            VC[VerificationController<br/>/api/verify]
            AC[AuthController<br/>/auth]
        end
        subgraph Business [Business Logic Layer]
            SS[Scheduling Service]
            IAS[Impact Analysis Service]
        end
        subgraph DataAccess [Data Access Layer]
            DR[Doctor Repository]
            RR[Room Repository]
            ER[Equipment Repository]
        end
        
        Presentation --> Business
        Business --> DataAccess
    end
    
    DataAccess -->|Spring Data Neo4j| Neo4j[(Neo4j Graph Database<br/>Nodes & Relationships)]
```

## 2. Domain Model (Entity Relationship)
```mermaid
erDiagram
    DOCTOR {
        string name
        string department
        boolean isActive
    }
    PROCEDURE {
        string name
        int durationMinutes
    }
    EQUIPMENT {
        string name
        string type
        string status
    }
    ROOM {
        string name
        string type
        boolean isOccupied
    }
    PATIENT {
        string name
        string priority
    }

    DOCTOR ||--o{ PROCEDURE : CAN_PERFORM
    PROCEDURE ||--o{ EQUIPMENT : REQUIRES
    ROOM ||--o{ PROCEDURE : SUITABLE_FOR
    PATIENT ||--o{ PROCEDURE : SCHEDULED_FOR
    PATIENT ||--o{ DOCTOR : ASSIGNED_DOCTOR
    PATIENT ||--o{ ROOM : ALLOCATED_ROOM
```

## 3. Scheduling Algorithm Flow
```mermaid
sequenceDiagram
    participant Client
    participant API as HospitalController
    participant Service as SchedulingService
    participant DB as Neo4j Database

    Client->>API: POST /schedule (patient, procedure, priority)
    API->>Service: scheduleProcedure()
    
    Note over Service: Step 1: Verify Procedure
    Service->>DB: Find Procedure Node
    
    alt Procedure Not Found
        Service-->>API: Throw Exception (404)
        API-->>Client: 404 Procedure Not Found
    else Procedure Found
        Note over Service: Step 2: Check Equipment
        Service->>DB: Check REQUIRED Equipment Availability
        
        alt Equipment Unavailable
            Service-->>API: Throw Exception (409 Conflict)
            API-->>Client: 409 Equipment Busy/Maintenance
        else Equipment Available
            Note over Service: Step 3: Find Doctor
            Service->>DB: Match (d:Doctor)-[:CAN_PERFORM]->(p)
            
            alt No Doctor Available
                Service-->>API: Throw Exception (409)
                API-->>Client: 409 No Doctor Available
            else Doctor Found
                Note over Service: Step 4: Find Room
                Service->>DB: Match (r:Room)-[:SUITABLE_FOR]->(p)
                
                alt No Room Available
                    Service-->>API: Throw Exception (409)
                    API-->>Client: 409 No Room Available
                else Room Found
                    Note over Service: Step 5: Execute Transaction
                    Service->>DB: Create SCHEDULED_FOR relationship
                    Service->>DB: Create ASSIGNED_DOCTOR relationship
                    Service->>DB: Set Room.isOccupied = true
                    
                    Service-->>API: Return Scheduled Patient
                    API-->>Client: 200 OK (Patient Details)
                end
            end
        end
    end
```

## 4. Impact Analysis (Failure Propagation)
```mermaid
graph LR
    FailedNode(Failure: MRI Scanner 01)
    
    FailedNode -->|REQUIRES| P1[Procedure: MRI Scan Head]
    FailedNode -->|REQUIRES| P2[Procedure: MRI Scan Knee]
    
    P1 -->|SCHEDULED_FOR| Pat1[Patient: John Doe]
    P2 -->|SCHEDULED_FOR| Pat2[Patient: Jane Smith]
    
    classDef failure fill:#f96,stroke:#333,stroke-width:2px;
    class FailedNode failure;
```
