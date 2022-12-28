# hotdeals-backend

![GitHub top language](https://img.shields.io/github/languages/top/halildurmus/hotdeals-backend?style=for-the-badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](https://github.com/halildurmus/hotdeals-backend/blob/master/LICENSE)
![Visits](https://visitor-badge.glitch.me/badge?page_id=halildurmus.hotdeals-backend)

> The **Backend** for my **[hotdeals](https://github.com/halildurmus/hotdeals)** app.

## Table of Contents

* [Features](#features)
* [Documentation](#documentation)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)
* [Roadmap](#roadmap)
* [Code Contributors](#code-contributors)
* [Contributing](#-contributing)
* [Author](#author)
* [License](#-license)

## Features

- REST API
- CRUD (users, deals, comments, categories, stores, reports)
- MongoDB
- Caching using Redis
- Elasticsearch for search operations
- Authentication (using Firebase Authentication)
- Role based access control (using Firebase Authentication and Spring Security)
- Request validation

## Documentation

The API documentation can be found **[here](https://hotdeals-backend.herokuapp.com/swagger-ui)**.

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

- Java 11+
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- You need to create a [Firebase](https://firebase.google.com) account and
  setup [Firebase Authentication](https://firebase.google.com/products/auth)
  and [Firebase Cloud Messaging](https://firebase.google.com/products/dynamic-links) services.

### Installation

1. Clone the repo using: `git clone https://github.com/halildurmus/hotdeals-backend.git`
2. In the `src/main/resources` directory, open the `application.yaml` file and change the env
   variables to fit your environment.
3. Start the **Docker Desktop**.
4. In the project root directory, run `docker-compose up -d` to start the containers for **MongoDB,
   Redis and Elasticsearch**. This may take a few minutes the first time as the container images
   need to be downloaded.
5. Download the Firebase service account file from the
   [Firebase Console](https://console.firebase.google.com) (Project Settings -> Service accounts)
   and copy it into the `src/main/resources` directory and rename it to `firebase-admin.json`.
6. Run the app using `mvnw spring-boot:run`.
7. Open your browser and navigate to `127.0.0.1:8080/actuator/health`. You should now see the status
   is `UP` on that JSON response.

## Roadmap

See the [open issues](https://github.com/halildurmus/hotdeals-backend/issues) for a list of proposed
features (and known issues).

## Code Contributors

This project exists thanks to all the people who contribute.

<a href="https://github.com/halildurmus/hotdeals-backend/graphs/contributors">
  <img class="avatar" alt="halildurmus" src="https://github.com/halildurmus.png?v=4&s=96" width="48" height="48" />
</a>

## 🤝 Contributing

Contributions, issues and feature requests are welcome.  
Feel free to check [issues page](https://github.com/halildurmus/hotdeals-backend/issues) if you want
to contribute.

## Author

👤 **Halil İbrahim Durmuş**

- Github: [@halildurmus](https://github.com/halildurmus)

## 📝 License

This project is [MIT](https://github.com/halildurmus/hotdeals-backend/blob/master/LICENSE) licensed.
