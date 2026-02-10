# Smart Hospital Resource & Dependency Management System

> **A decision-making engine for hospital resource coordination, built with Spring Boot and Neo4j.**

## 1. Problem Statement
Hospitals are not short of data; they are short of **clarity when constraints collide**.
Scheduling a procedure involves coordination between doctors, equipment, rooms, and time. Most systems reduce this to simple CRUD operations, making it hard to answer:
*   *Why couldn't this patient be scheduled?*
*   *Who gets impacted if an MRI machine goes down?*
*   *Can we automatically recover from a doctor's absence?*

This project models the hospital as a **network of dependencies** to solve these problems.

## 2. Technology Stack
*   **Java**: 21
*   **Framework**: Spring Boot 4.0.2
*   **Database**: Neo4j (Graph Database) (v5.x recommended)
*   **Build Tool**: Maven
*   **ORM**: Spring Data Neo4j
*   **Testing**: JUnit 5, Mockito, Testcontainers

## 3. Getting Started

### Prerequisites
*   **Java 21** SDK installed.
*   **Maven** installed (or use `mvnw`).
*   **Neo4j Database**:
    *   Locally installed (Desktop or Server) running on port `7687`.
    *   OR Docker container: `docker run -d -p 7474:7474 -p 7687:7687 -e NEO4J_AUTH=neo4j/password neo4j:latest`

### Installation & Run
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/smart-hospital-resource-manager.git
    cd smart-hospital-resource-manager
    ```

2.  **Configure Database**:
    Update `src/main/resources/application.properties`:
    ```properties
    spring.neo4j.uri=neo4j://localhost:7687
    spring.neo4j.authentication.username=neo4j
    spring.neo4j.authentication.password=your_password
    ```

3.  **Build the Project**:
    ```bash
    mvn clean install
    ```

4.  **Run the Application**:
    ```bash
    mvn spring-boot:run
    ```
    The application will start on **port 8081** (to avoid conflict with common 8080 services).

## 4. Architecture & Design
This system uses a **Graph Database (Neo4j)** because hospital relationships change faster and matter more than raw data.
For detailed architecture diagrams (System Overview, Domain Model, Scheduling Flow), see [ARCHITECTURE_DIAGRAM.md](ARCHITECTURE_DIAGRAM.md).

## 5. Key Features
*   **Graph-Based Modeling**: Entities (Doctors, Rooms, Equipment) are nodes; capabilities and assignments are relationships.
*   **Constraint-Based Scheduling**: Multi-stage validation (Equipment -> Doctor -> Room).
*   **Impact Analysis**: Query the graph to find "blast radius" of resource failures.
*   **Transactional Safety**: Atomic scheduling operations.

## 6. API Endpoints (Summary)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/hospital/schedule` | Schedule a procedure for a patient. |
| `GET` | `/api/hospital/impact-analysis` | Analyze impact of a resource failure. |
| `POST` | `/api/admin/add-doctor` | Onboard a new doctor. |
| `POST` | `/api/admin/add-equipment` | Register new equipment. |

---
*Created as a learning project for Advanced Agentic Coding.*