# SunDevil Connect

### ASU CSE 460 - Group Project

SunDevil Connect is a client-server campus platform designed to help ASU students discover, create, and
manage clubs and events. The system enables users to browse events, register for activities, and manage
club participation through an interactive desktop interface.

This project was developed collaboratively with Hank Atherton, with both contributors working together across
the full stack, including frontend development, backend services, and system design.

---

## ⬇️ Download & Install (Windows)

A pre-built Windows installer is available on the [Releases page](https://github.com/wgolden117/sundevil-connect/releases/tag/v1.0).

1. Go to the [v1.0 Release](https://github.com/wgolden117/sundevil-connect/releases/tag/v1.0)
2. Download `SunDevilConnect-1.0.exe`
3. Run the installer and follow the prompts
4. Launch from the desktop shortcut or Start Menu

> No additional software required — Java and all dependencies are bundled in the installer.

> ⚠️ This is a pre-release. Account creation is not yet supported. See the [release notes](https://github.com/wgolden117/sundevil-connect/releases/tag/v1.0) for login credentials.

---

## Features
- Club creation and management
- Event creation and registration
- Capacity tracking and enforcement
- Client-server communication using gRPC
- Interactive desktop UI built with JavaFX

---

## Prerequisites (for development only)
- JDK 21 (Eclipse Temurin recommended)
- Any Java IDE or text editor
- Git

---

## Getting Started

### 1. Clone the repository
```bash
git clone git@github.com:wgolden117/sundevil-connect.git      # SSH
git clone https://github.com/wgolden117/sundevil-connect.git  # HTTPS
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

## Building the Installer
To rebuild the Windows installer yourself:
```bash
.\build-and-run.bat
```
The installer will be generated in the `dist\` folder. Requires JDK 21 with `jpackage` and a local JavaFX SDK.

## Project Structure
- `shared/` - Protobuf definitions and generated stubs
- `server/` - Server-side business logic and gRPC service implementations
- `client/` - JavaFX client application

## Tech Stack
- Java 21
- JavaFX 21
- gRPC / Protobuf
- SQLite via JDBC
- jpackage (Windows installer)

## Design Documentation

Detailed system design artifacts are available in the `/docs` directory, including:

- Requirements and use case modeling
- UML diagrams (CRC, class, and use case diagrams)
- System architecture and design patterns
- Deployment and component diagrams

These documents outline the full software design process from requirements to system architecture.