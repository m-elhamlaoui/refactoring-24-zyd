# **Project Setup Instructions**

## **1. Clone the Repository**
First, clone the repository to your local machine:
```bash
git clone https://github.com/m-elhamlaoui/refactoring-24-zyd.git
cd refactoring-24-zyd
2. Prerequisites
Ensure you have the following installed on your system:

Java Development Kit (JDK): Version 8 or higher
Maven: For dependency management
Git: For version control
Database: (e.g., MySQL, PostgreSQL, or HSQLDB)
3. Create a .env File
You must create a .env file in the root of your project directory to store your environment variables. This file should contain the database connection details and other configurations.

3.1 Example .env File
dotenv
Copier le code
DB_URL=jdbc:mysql://localhost:3306/belote_db
DB_USERNAME=your_username
DB_PASSWORD=your_password
APP_PORT=8080
3.2 Steps to Create the .env File
Create a new file in the root of your project named .env.
Copy the example above and replace your_username and your_password with your actual database credentials.
4. Database Setup
Create a new database for the project:
For MySQL:
sql
Copier le code
CREATE DATABASE belote_db;
Ensure the .env file contains the correct database name, username, and password.
5. Install Dependencies
If your project uses Maven for dependency management, install the required dependencies:

bash
Copier le code
mvn clean install
6. Running the Project
6.1 Compile and Run
Compile and run the project from the terminal:

bash
Copier le code
javac -d bin src/**/*.java
java -cp bin Belote
6.2 Run from IDE
Open the project in your preferred IDE (e.g., Eclipse or IntelliJ IDEA).
Configure the .env file as part of your environment.
Run the Belote class as a Java application.
7. Project Features
Manage Tournaments: Create, update, and manage tournaments.
Manage Teams and Matches: Easily handle team registrations and match schedules.
User-Friendly GUI: The project includes a graphical interface for smooth interaction.
8. Contribution Guidelines
Fork the repository.
Create a new branch for your changes:
bash
Copier le code
git checkout -b feature-name
Commit your changes:
bash
Copier le code
git add .
git commit -m "Description of changes"
Push your branch and open a pull request:
bash
Copier le code
git push origin feature-name
9. Troubleshooting
Database Connection Issues
Ensure the database service is running and credentials in the .env file are correct.
Missing Dependencies
Run:
bash
Copier le code
mvn clean install
10. Contact
For further assistance, contact [Your Name] or raise an issue in the repository.

markdown
Copier le code

### **Explanation**
- Each section is properly formatted using `##` for main headings and `**` for emphasis.
- Examples and code are in code blocks (e.g., `bash`, `sql`, `dotenv`) for clarity.
- The file is now easy to read and provides a comprehensive guide for setting up and running your project.

Let me know if you need additional tweaks!
