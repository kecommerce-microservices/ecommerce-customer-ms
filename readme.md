
# Ecommerce Auth Server Microservice

Um microserviço construido para gerenciar as roles e autenticação dos usuários,
usando oauth2.

[![codecov](https://codecov.io/gh/kecommerce-microservices/ecommerce-customer-ms/branch/develop/graph/badge.svg?token=Suo3KSpMBW)](https://codecov.io/gh/kecommerce-microservices/ecommerce-customer-ms)
[![CI Pipeline for Push to Develop](https://github.com/kecommerce-microservices/ecommerce-customer-ms/actions/workflows/ci-push-develop.yml/badge.svg?branch=develop)](https://github.com/kecommerce-microservices/ecommerce-customer-ms/actions/workflows/ci-push-develop.yml)
[![CI/CD Pipeline for Push to Main](https://github.com/kecommerce-microservices/ecommerce-customer-ms/actions/workflows/ci-cd-push-main.yml/badge.svg?branch=main)](https://github.com/kecommerce-microservices/ecommerce-customer-ms/actions/workflows/ci-cd-push-main.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kecommerce-microservices_ecommerce-customer-ms&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kecommerce-microservices_ecommerce-customer-ms)

# Índice

- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Principais endpoints](#principais-endpoints)
- [Documentação](docs/documentation.md)
- [Contribuição](docs/CONTRIBUTING.md)
- [Roadmap](docs/roadmap.md)
- [Changelog](docs/changelog.md)

## Requisitos

- Tecnologias Utilizadas:
  - Java
  - Spring
  - PostgreSQL
  - Flyway
  - docker
  - Swagger
  - Gradle
  - Github Actions

## Principais endpoints

- Todos os endpoints estão documentados no swagger, para acessar a documentação basta acessar a url do serviço e adicionar `api/swagger-ui/index.html` no final.

## Instalação

- Pré-requisitos:
    - Java JDK 17 ou superior.
    - Docker com docker-compose

- Clone o projeto
```bash
  git clone https://github.com/kecommerce-microservices/ecommerce-customer-ms.git
```

- Entre no diretório do projeto
```bash
  cd ecommerce-customer-ms
```

- Agora abra esse projeto em alguma idea ou então Instale as dependências
```bash
./gradlew --refresh-dependencies
```

- Rode o projeto e o docker-compose
```bash
docker-compose -f docker-compose-dev.yml up -d
./gradlew bootRun
```