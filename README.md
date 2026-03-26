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
This generates the required Protobuf/gRPC stubs. **Required before anything else will compile!**
```bash
./gradlew build
```

## Running the project
### Quick start (both together)
```bash
./gradlew runAll
```
Note: `runAll` is for quick testing only, two consoles is preferred. Most IDEs can run two processes at the same time. 

### Development (recommended)
Run in two separate terminals:
```bash
# Terminal 1 - Server
./gradlew :server:run

# Terminal 2 - Client  
./gradlew :client:run
```
Always start the server before the client.

## Project Structure
- `shared/` - Protobuf definitions and generated stubs
- `server/` - Server-side business logic and gRPC service implementations
- `client/` - JavaFX client application

## Tech Stack
- Java 21
- JavaFX 21
- gRPC / Protobuf
- SQLite via JDBC
