# NotifyHub - Email Notification Service

NotifyHub Email Service is a production-oriented notification microservice
built using Spring Boot and Apache Kafka.

It is responsible for processing email notification events, rendering
dynamic email templates, sending emails through SMTP, maintaining
notification history, handling retries, and moving permanently failed
notifications to a Dead Letter Topic.

## Tech Stack

- Java 17
- Spring Boot
- Spring Kafka
- Apache Kafka
- MySQL
- Spring Data JPA
- Thymeleaf
- JavaMailSender
- Docker
- Maven

## Features

- Email notification processing
- Kafka-based asynchronous communication
- Dynamic email templates
- Thymeleaf HTML templates
- MySQL persistence
- Email delivery logging
- Retry mechanism
- Configurable retry delays
- Dead Letter Topic
- Global exception handling
- Actual email failure reason tracking
- Notification status tracking
- Dockerized Kafka

## Architecture

Application
    |
    v
Kafka
    |
    v
Email Consumer
    |
    v
Email Service
    |
    +----> Template Service
    |
    +----> Mail Service
    |
    +----> MySQL
    |
    +----> Retry Scheduler
              |
              v
        Kafka Retry Topic
              |
              v
        Email Consumer
              |
              v
        Maximum Retries
              |
              v
          DLT Topic

## Kafka Topics

notification.email

notification.email.retry

notification.email.dlt

## Retry Strategy

The service supports configurable retry attempts.

Example retry delays:

- Retry 1: 30 seconds
- Retry 2: 120 seconds
- Retry 3: 300 seconds

After the maximum retry attempts are exhausted, the notification
is moved to the Dead Letter Topic.

## Database

The service maintains:

- Email notifications
- Email templates
- Email processing logs

## Running the Project

### 1. Start Kafka

```bash
docker compose up -d
