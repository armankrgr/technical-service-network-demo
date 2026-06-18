# Technical Service Network Demo

یک نمونه runnable دانشگاهی برای «سامانه شبکه ارتباطی بین افراد فنی» با Kotlin، Spring Boot، Thymeleaf و HTMX.

## Technologies

- Kotlin
- Spring Boot Web
- Spring Boot Thymeleaf
- HTMX for live partial updates
- Simple CSS
- In-memory demo data, no database

## Features

- Persian-first UI with FA / EN language switch
- RTL for Persian and LTR for English
- Session-based role switch: Customer, Technician, Admin, Owner
- Home page with service categories and featured technicians
- Live technician search and filtering by name, service, skill, category, city, rating, and availability
- Technician profile pages with bio, skills, rating, comments, slots, booking form, and review form
- Booking flow with request ID, selected time, `.ics` download, and Google Calendar link
- HTMX review submission with refreshed rating and review list
- Technician dashboard with service requests and HTMX accept/reject actions
- Admin and Owner demo cards for system analysis scenarios

## How to Run

```powershell
cd E:\Project\technical-service-network-demo
$env:GRADLE_USER_HOME='E:\Project\.gradle-cache'
.\gradlew.bat bootRun
```

Open:

```text
http://localhost:8080
```

## Build

```powershell
cd E:\Project\technical-service-network-demo
$env:GRADLE_USER_HOME='E:\Project\.gradle-cache'
.\gradlew.bat clean build
```

## Demo Scenario

1. Open the home page and introduce the platform.
2. Switch between FA and EN to show bilingual support.
3. Open technicians and search for a name, skill, or service category.
4. Filter by city, category, minimum rating, and availability.
5. Open a technician profile.
6. Select an available time slot and submit a service request.
7. Download the `.ics` calendar file or open the Google Calendar link.
8. Submit a review and show that the review list and rating update.
9. Open the technician dashboard and accept or reject a request.
10. Explain Admin and Owner cards as part of the Systems Analysis and Design scope.

## Routes

- `/`
- `/technicians`
- `/technicians/results`
- `/technicians/{id}`
- `/technicians/{id}/book`
- `/technicians/{id}/reviews`
- `/technician/dashboard`
- `/requests/{id}/status`
- `/calendar/{requestId}.ics`

## GitHub Repository Name

`technical-service-network-demo`

## Screenshots

Add screenshots here after recording or presenting the demo:

- Home page
- Search and filters
- Technician profile
- Booking confirmation
- Technician dashboard

## Academic Demo Note

This project is an in-memory academic demo. It does not include real authentication, payment, SMS, email, GPS, or a production database. Its purpose is to demonstrate core workflows and support Systems Analysis and Design presentation material.
