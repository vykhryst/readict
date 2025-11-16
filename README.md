# Readict — book discovery & personal library

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg)](https://www.postgresql.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539.svg)](https://swagger.io/)
[![Build](https://img.shields.io/badge/Build-Maven-lightgrey.svg)](https://maven.apache.org/)

Readict is a full-stack web application designed for book lovers. It provides a comprehensive platform for users to discover new books, track their reading progress, and manage their personal library.

Users can browse a rich catalog, add books to custom shelves (like "Want to Read," "Currently Reading," and "Read"), write reviews, and give star ratings.

The feature of Readict is its recommendation engine, which uses collaborative filtering  to suggest new books based on a user's ratings and their stated favorite genres..

- 📹 [Video demo](https://drive.google.com/file/d/1bedo76nXqX_2bidiH1w1C6Y6VQ_QgPLg/view?usp=sharing)

![Readict Homepage Screenshot](https://github.com/user-attachments/assets/f101c59f-dc3d-4ce5-8c9e-391bfabe22cc)

## Features

- **Browse & Search** books by title, genre, series, or author with server-side paging & sorting.
- **Rich Book Pages** with annotation, author/series links, ratings, reviews, and metadata.
- **User Library** shelves: _Read_, _Reading_, _Want to read_ (add/remove/move with instant feedback).
- **Ratings & Reviews** with per-user rating CRUD and reviews (list, add, delete).
- **Hybrid Recommendations** combining mean-centered cosine user-similarity (CF) with genre match weighting, persisted per user and refreshed via events/scheduler.
- **Responsive UI** with fast client-side routing.
- **Admin endpoints** for catalog curation (authors/series/tropes/etc).
- **Session-based auth** (JSESSIONID).

![Readict Library Screenshot](https://github.com/user-attachments/assets/e35bd9e0-e0d3-49d1-a78c-333fdf7233ea)

## Tech Stack

The project is a full-stack monorepo containing both the backend and frontend.

**Backend**
- Java 17, Spring Boot 3.4 (Web, Data JPA, Validation, Security, Cache)
- PostgreSQL 14+, Caffeine cache, MapStruct, Springdoc OpenAPI
- Testing: JUnit 5 + Mockito

**Frontend**
- React 19, React Router 7, Axios, Bootstrap 5, Font Awesome, NPM

## Quickstart

To get a local copy up and running, follow these simple steps.

### Prerequisites

You will need the following tools installed on your system:
* [Java 17 (or newer)](https://www.oracle.com/java/technologies/downloads/)
* [Maven](https://maven.apache.org/download.cgi)
* [Node.js (v16 or newer)](https://nodejs.org/en)
* [PostgreSQL](https://www.postgresql.org/download/)

### 1. Backend Setup (Spring Boot)

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/vykhryst/readict.git](https://github.com/vykhryst/readict.git)
    cd readict
    ```

2.  **Set up PostgreSQL Database:**
    * Create a new PostgreSQL database.
    * The application will look for the database credentials in environment variables. Set the following variables in your environment (or in your IDE's run configuration):
        * `DB_URL`: The JDBC URL (e.g., `jdbc:postgresql://localhost:5432/your_db_name`)
        * `DB_USER`: Your PostgreSQL username
        * `DB_PASSWORD`: Your PostgreSQL password

3.  **Initialize Database (Optional but Recommended):**
    * To set up the required tables, run the `backend/src/main/resources/sql/schema.sql` file against your database.
    * To populate the database with sample data, run `backend/src/main/resources/sql/dump.sql`.

4.  **Run the Backend:**
    * Navigate to the `backend` directory:
        ```sh
        cd backend
        ```
    * Run the application using the Maven wrapper:
        ```sh
        # On macOS/Linux
        ./mvnw spring-boot:run
        
        # On Windows
        .\mvnw.cmd spring-boot:run
        ```
    * The backend server will start on `http://localhost:8080`.

### 2. Frontend Setup (React)

1.  **Open a new terminal window.**

2.  **Navigate to the `frontend` directory:**
    ```sh
    cd readict/frontend
    ```

3.  **Install NPM packages:**
    ```sh
    npm install
    ```

4.  **Run the Frontend:**
    ```sh
    npm start
    ```
    * The React development server will start and open the app in your browser at `http://localhost:3000`.

## API Documentation

The backend API is documented using Springdoc (Swagger). Once the backend server is running, you can access the interactive API documentation at:
**[http://localhost:8080/](http://localhost:8080/swagger-ui/index.html)**

## Author

Oksana Vykhryst - [vykhryst (GitHub)](https://github.com/vykhryst)
