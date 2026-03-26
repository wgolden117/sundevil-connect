# SunDevil Connect

### ASU CSE 460 - Group Project

A campus club and event management platform for ASU students.

## Prerequisites
- JDK 21 (Eclipse Temurin recommended)
- Any Java IDE or text editor
- Git

## Getting Started

### 1. Clone the repository
```bash
git clone git@github.com:hdathert/sundevil-connect.git      # SSH
git clone https://github.com/hdathert/sundevil-connect.git  # HTTPS
cd sundevil-connect
```

### 2. Build the project
```bash
./gradlew build
```

### 3. Running the project
Start both server and client with the combined Gradle task:
```bash
./gradlew runAll
```

Or run them separately in two terminals:
```bash
# Terminal 1 - Server
./gradlew :server:run

# Terminal 2 - Client  
./gradlew :client:run
```

## Project Structure
- `shared/` - Protobuf definitions and generated stubs
- `server/` - Server-side business logic and gRPC service implementations
- `client/` - JavaFX client application

## Tech Stack
- Java 21
- JavaFX 21
- gRPC / Protobuf
- SQLite via JDBC