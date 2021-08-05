# hotdeals-backend

![GitHub top language](https://img.shields.io/github/languages/top/halildurmus/hotdeals-backend?style=for-the-badge)
[![GitHub contributors](https://img.shields.io/github/contributors-anon/halildurmus/hotdeals-backend?style=for-the-badge)](https://github.com/halildurmus/hotdeals-backend/graphs/contributors)
[![GitHub issues](https://img.shields.io/github/issues/halildurmus/hotdeals-backend?style=for-the-badge)](https://github.com/halildurmus/hotdeals-backend/issues)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](https://github.com/halildurmus/hotdeals-backend/blob/master/LICENSE)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?logo=linkedin&labelColor=blue&style=for-the-badge)](https://linkedin.com/in/halildurmus)
![Visits](https://badges.pufler.dev/visits/halildurmus/hotdeals-backend?style=for-the-badge)

> This is the **Backend** for my **[hotdeals app](https://github.com/halildurmus/hotdeals-app)**.

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

**TODO**

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites
- Java 11+
- Docker (for tests)
- You need to have **[MongoDB](https://www.mongodb.com)**, **[Redis](https://redis.io)** and **[Elasticsearch](https://www.elastic.co)** installed on your pc.  
- Also you need to have a **[Firebase](https://firebase.google.com)** account and setup **[Firebase Authentication](https://firebase.google.com/products/auth)** and **[Firebase Cloud Messaging](https://firebase.google.com/products/dynamic-links)** services.

#### 1. MongoDB Installation
1. [Install MongoDB](https://docs.mongodb.com/manual/administration/install-community/)
2. Start the MongoDB instance.

#### 2. Redis Installation
1. [Install Redis](https://redis.io/topics/quickstart)
2. Run `redis-server` to start the Redis server.

#### 3. Elasticsearch Installation
1. [Install Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/install-elasticsearch.html)
2. Start the Elasticsearch instance.

### Installation

1. Clone the repo using: `git clone https://github.com/halildurmus/hotdeals-backend.git`
2. Go to `src/main/resources` folder. In here you will find an `application-example.yaml` file. Copy it and rename it to `application.yaml`.
3. Open the `application.yaml` file and change the env variables to fit your environment.
4. Run the app using `mvnw spring-boot:run`
5. Open your browser and navigate to `localhost:8080/actuator/health`. You should now see the status is `UP` on that JSON response.

## Roadmap

See the [open issues](https://github.com/halildurmus/hotdeals-backend/issues) for a list of proposed features (and known issues).

## Code Contributors

This project exists thanks to all the people who contribute.

<a href="https://github.com/halildurmus/hotdeals-backend/graphs/contributors">
  <img class="avatar" alt="halildurmus" src="https://github.com/halildurmus.png?v=4&s=96" width="48" height="48" />
</a>

## 🤝 Contributing

Contributions, issues and feature requests are welcome.  
Feel free to check [issues page](https://github.com/halildurmus/hotdeals-backend/issues) if you want to contribute.

## Author

👤 **Halil İbrahim Durmuş**

- Github: [@halildurmus](https://github.com/halildurmus)

## 📝 License

This project is [MIT](https://github.com/halildurmus/hotdeals-backend/blob/master/LICENSE) licensed.
