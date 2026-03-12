# Chat Application

A JavaFX-based chat application with client-server architecture. Supports real-time messaging, contact avatars, and configurable settings via `.env`.  

---

## Features

- JavaFX frontend with **modern UI**  
- **Contact list** shows profile avatars instead of colored placeholders  
- Real-time messaging with server broadcast  
- Configuration via `.env` (server IP, port, database credentials)  
- Modular project structure (`lib` folder for dependencies)  

---

## Project Structure
```
Chat_Application/
├── src/ # Java source code
│ ├── client/ # JavaFX client code
│ ├── server/ # Server code
│ ├── gui/ # UI components
│ └── utils/ # Utilities (EnvLoader, models, etc.)
├── lib/ # External JARs (JavaFX, MySQL connector)
├── bin/ # Compiled classes
├── .env # Configuration file
└── README.md # This file
```
---

## Requirements

- Java 20+ (with modules support)  
- JavaFX SDK 23+  
- MySQL (for backend, optional if only frontend is used)  

---

## Setup

1. **Clone the repository**  

```bash
git clone <your-repo-url>
cd Chat_Application
```

2. **Install dependencies**
All required JARs are in the `lib/` folder:
- `javafx-controls.jar`
- `javafx-fxml.jar`
- `mysql-connector-j-X.X.X.jar (backend only)`

3. **Configure `.env`**

All required configuration is in the `.env` file. Update the server IP and port as needed:
```text
# Server config (update IP when it changes)
SERVER_ADDRESS=192.168.100.49
SERVER_PORT=3001

# Database config (backend)
DB_HOST=localhost
DB_PORT=3306
DB_NAME=chat
DB_USER=root
DB_PASSWORD=secret
```

> **Note:** The server address may change frequently (for example, when switchin networks). Always make sure the `SERVER_ADDRESS` matches with the current ip address before running client.
> **Tip:** If the client cannot conntect, check the `SERVER_ADDRESS` in `.env` and update it accordingly. 

## Compile & Run Backend
```bash
# Compile server sources
javac -d bin -cp "lib/mysql-connector-j-9.2.0.jar" src/server/*.java src/utils/*.java

# Run server
java -cp "bin;lib/mysql-connector-j-9.2.0.jar" server.ChatServer
```

## Compile & Run Frontend
```bash
# Compile all JavaFX sources
javac --module-path "lib" --add-modules javafx.controls,javafx.fxml -d bin src\client\*.java src\utils\*.java src\gui\*.java

# Run the client
java --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml -Dprism.order=sw -Dprism.forceGPU=false -cp bin client.ChatClien
```