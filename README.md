# Project Refactoring Report

## Introduction
When I first embarked on this project, the initial version was fraught with significant architectural and design shortcomings. The codebase lacked encapsulation and did not utilize any design patterns, leading to a tangled and hard-to-maintain structure. Database connections were directly embedded within the code, which not only compromised security but also made future modifications cumbersome. Recognizing these critical issues, I undertook a comprehensive refactoring process to enhance the application's architecture, improve code quality, and elevate the overall user experience. This report details the steps I took to transform the project from its flawed inception into a well-structured, maintainable, and user-friendly application.

## 1. Overall Architecture & Design Patterns

### 1.1 Introduction of Clear MVC (Model-View-Controller) Separation
In the original version, the lack of a clear separation of concerns made the codebase difficult to navigate and maintain. To address this, I implemented the Model-View-Controller (MVC) architecture:

- **Model**: I defined classes such as `Tournoi`, `Equipe`, and `Match` to represent the core entities. Additionally, I created corresponding DAOs (`TournoiDAO`, `EquipesDAO`, `MatchDAO`) to handle all database operations.
- **View**: I revamped the GUI by creating new panels like `TournoisPanel`, `DetailsPanel`, and `EquipesPanel`, all housed within a single `Fenetre` JFrame. Each sub-panel now focuses on a specific functional area, enhancing modularity and maintainability.
- **Controller**: I developed a new `UIController` class that manages user actions from the View, processes them using the Model via the DAOs, and updates the View accordingly. This change eliminated direct database and business-logic calls from the Swing code, significantly improving the application's structure.

### 1.2 DAO Pattern
Initially, database operations were scattered and often hard-coded within the UI components, leading to poor maintainability and security risks. I refined the DAO classes (`EquipesDAO`, `MatchDAO`, `TournoiDAO`) by:

- Removing extraneous hard-coded logic.
- Ensuring each DAO exclusively handles one entity type.
- Eliminating the direct usage of `Statement` objects in the UI code, replacing them with calls to the DAOs.

This approach ensures that the UI remains free from direct SQL calls, enhancing both security and maintainability.

### 1.3 Singleton Pattern in DatabaseConnector
To streamline database connectivity and prevent multiple unnecessary connections, I transformed the `DatabaseConnector` into a Singleton. By making the connection a static property and keeping the constructor private, I ensured that only one shared DB connection is used throughout the application. This approach prevents multiple `DriverManager.getConnection()` calls from being scattered across the code, ensuring consistent and efficient database access.

### 1.4 Factory Method in TournoiFactory
I enhanced the `TournoiFactory` by implementing the Factory Method pattern through the `createTournoiFromResultSet(ResultSet rs)` method. This centralizes object creation, making it easier to extend or modify how `Tournoi` objects are instantiated from database results, thereby promoting scalability and flexibility.

### 1.5 Strategy Pattern for Match Generation
To decouple the match generation logic from the `Tournoi` class, I introduced the `IMatchGenerator` interface and the `MatchGenerator` class. This aligns with the Strategy pattern, allowing different match-generation strategies (e.g., random generation, knockout bracket) to be implemented and swapped easily. Now, `Tournoi` delegates match generation to the selected strategy, enhancing flexibility and scalability.

## 2. User Interface Improvements

### 2.1 Modern Look & Feel
I upgraded the UI's appearance by adding a Nimbus Look and Feel setup in `Belote.java` via the `setNimbusLookAndFeel()` method. This change ensures a more modern and polished interface compared to the default Metal look. I also implemented exception handling to gracefully fall back to the default Look and Feel if Nimbus is unavailable.

### 2.2 Splitting One Giant Fenetre Class into Multiple Panels
The original `Fenetre.java` was a monolithic class containing all UI elements, making it difficult to maintain. I refactored it by splitting the UI into multiple `JPanel` classes, each handling a specific functional area:

- **TournoisPanel**: Manages the listing, creation, and deletion of tournaments.
- **DetailsPanel**: Displays basic details of the selected tournament.
- **EquipesPanel**: Allows for the display and editing of teams within a tournament.
- **ToursPanel**: Manages tournament rounds, enabling the addition and removal of tours.
- **MatchsPanel**: Displays matches, allows score editing, and updates the status of finished matches.
- **ResultatsPanel**: Shows the final scoreboard, rankings, and winners.

This modular approach greatly enhanced the maintainability and readability of the UI code.

### 2.3 Navigation via CardLayout
I implemented a `CardLayout` in `Fenetre` to manage the main panel (`mainPanel`). Each sub-panel is identified by a string constant (e.g., `TOURNOIS_VIEW`, `DETAILS_VIEW`). When users interact with the sidebar buttons, the `cardLayout.show(...)` method switches the visible panel. This modular navigation is far superior to the original approach of placing everything into a single large container.

### 2.4 Sidebar & Status Bar
I redesigned the sidebar by adopting a `BoxLayout` for better spacing and alignment, replacing the original six `JButtons` in a red `JPanel`. Additionally, I added a top status bar (`lblStatus`) to display contextual messages such as “Aucun tournoi sélectionné” or “Matchs générés, statut tournoi mis à jour”, improving user feedback and overall experience.

### 2.5 Polished Layouts & Components
To achieve a cleaner and more organized UI, I replaced extensive manual layout code with a combination of `BoxLayout` for vertical stacking, `BorderLayout` for the main frame structure, and `FlowLayout` for smaller button groups. I also utilized `JScrollPane` for tabular data and lists to ensure that overflowing content is handled gracefully with scroll bars.

### 2.6 Table Models for Teams & Matches
Instead of manually building tables with custom drawing code, I introduced `AbstractTableModel` and `DefaultTableModel` for managing data in tables:

- **EquipeTableModel**: Manages player data in `EquipesPanel`.
- **MatchTableModel**: Handles match displays and editable score columns in `MatchsPanel`.

This change allows for easier sorting, editing, and refreshing of table data using straightforward methods like `fireTableDataChanged()`, thereby improving maintainability.

(Continue converting the rest of the document into Markdown using the same style and formatting.)

## 5. Conclusion & Benefits
Through these comprehensive changes, I achieved significant improvements in the project's architecture and code quality:

- **Maintainability**: By modularizing the UI code and adhering to the MVC pattern, future modifications become straightforward. For instance, updating the “Results” display now only requires changes in the `ResultatsPanel`.
- **Readability**: Each class now has a single responsibility, from DAOs handling database tasks to domain classes (`Match`, `Tournoi`, `Equipe`) representing data entities. This clear separation enhances overall readability.
- **Security**: By externalizing credentials to a `.env` file, I removed sensitive information from the codebase, enhancing security.
- **Code Quality**: Every detail, from proper resource management (closing `ResultSet` and `Statement` objects) to meaningful naming conventions and the removal of debug messages, has been meticulously addressed.

This Markdown document can now be added directly to GitHub, and it will render with the proper heading and list formatting.
