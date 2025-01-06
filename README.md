# Belote Tournament Management System

## Overview
The **Belote Tournament Management System** is a software application designed to streamline the organization, management, and scoring of Belote tournaments. This project combines core software engineering principles with modern best practices to deliver a more secure, maintainable, and scalable application.

In its latest iteration, **Belote v2.0**, significant improvements have been made, including refactoring the codebase, integrating design patterns, adopting SOLID principles, enhancing database management, and addressing previous architectural issues.

## Key Features
- Centralized tournament management for Belote.
- Improved database handling using MySQL.
- Modular architecture adhering to industry best practices.
- Separation of concerns for cleaner, more maintainable code.
- Scoring system extensibility for various tournament types (e.g., League, Round Robin).

## Refactoring Highlights

### 1. Design Patterns
Refactoring focused heavily on incorporating proven design patterns to improve modularity and maintainability.

- **DAO Pattern**  
  - Abstracted database access logic for cleaner separation of concerns.  
  - Introduced prepared statements to safeguard against SQL injection attacks.

- **Singleton Pattern**  
  - Centralized database connection management to ensure resource efficiency and avoid redundant connections.

- **Factory Pattern**  
  - Simplified the creation of tournament-related objects.  
  - Ensured extensibility by adhering to the Open/Closed Principle.

- **Strategy Pattern**  
  - Decoupled scoring logic from tournament logic.  
  - Enabled easy switching between different scoring systems like League and Round Robin.

- **Builder Pattern**  
  - Simplified object creation using chainable setters, ensuring clean and readable instantiation of tournament objects.

### 2. SOLID Principles
The refactor adhered to SOLID principles to ensure the codebase remains scalable and maintainable:

- **Single Responsibility Principle (SRP)**  
  Each class is now responsible for only one aspect of the system.  
  *Example*: DAO classes handle database logic, while GUI classes handle presentation logic.

- **Open/Closed Principle (OCP)**  
  Incorporated Factory and Strategy patterns to make the system open for extension but closed for modification.

### 3. Database Management
- **Migration to MySQL**  
  Transitioned from HSQLDB to MySQL for better scalability, reliability, and data integrity.

- **Secure Configuration**  
  Database credentials are now stored in a `.env` file, ensuring security and separation of concerns.

- **Improved Resource Management**  
  Implemented `try-with-resources` to automatically close database connections and prevent resource leaks.

## Current Issues and Future Enhancements

### 1. Code Quality and Maintainability
- **Deprecated JDBC Driver**  
  The refactor updated to the modern `com.mysql.cj.jdbc.Driver` for compatibility and performance.

- **Hardcoded Paths**  
  SQL script paths and other configurations have been externalized for flexibility.

- **Logging and Exception Handling**  
  Introduced logging frameworks (e.g., Log4j) to replace console outputs, aiding debugging and user-friendly error reporting.

### 2. User Interface (GUI)
- **Tight Coupling**  
  Previously, the GUI class (`Fenetre.java`) handled both UI and database logic. This was refactored to separate concerns.

- **Localization**  
  Hardcoded strings were replaced with externalized resources, enabling easier localization and translation.

- **Future Work**  
  Further refactoring of the GUI into smaller, reusable components is needed to achieve complete adherence to the Single Responsibility Principle.

### 3. Tournament Management
- **Input Validation**  
  Validations were added to prevent invalid or malicious data entry.

- **Scoring Logic**  
  The Strategy pattern was implemented to handle different scoring algorithms efficiently.

- **SQL Injection Prevention**  
  Replaced raw SQL queries with prepared statements to secure data access.

### 4. Testing and Build Automation
- **Unit Testing**  
  Introduced JUnit-based testing to ensure the correctness of critical components.

- **Dependency Management**  
  Migrated to Maven for automated build processes and library management, replacing manual JAR management.

## System Architecture

### 1. Layers
The system follows a modular layered architecture:

- **Presentation Layer (GUI)**  
  Handles user interactions and displays tournament information.

- **Business Logic Layer**  
  Manages the rules and logic for tournament creation, scoring, and management.

- **Data Access Layer (DAO)**  
  Facilitates interaction with the MySQL database.

### 2. Key Components
- **Fenetre Class (GUI)**  
  Handles the graphical user interface for the application.

- **DAO Classes**  
  Handle database interactions for entities like tournaments, teams, and matches.

- **Tournoi Class**  
  Represents the tournament entity and manages tournament-specific logic.

## How to Run the Application

### Setup the Database
Import the `create.sql` script to set up the database schema:
```bash
mysql -u root -p belote_db < create.sql
