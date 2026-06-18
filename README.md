# Technical Service Network Demo

University demo project for **سامانه شبکه ارتباطی بین افراد فنی**: a lightweight service marketplace where customers can find technical specialists, filter profiles, book an available time slot, export a calendar event, and leave reviews.

GitHub repository: <https://github.com/armankrgr/technical-service-network-demo>

## Stack

- Kotlin
- Spring Boot Web
- Spring Boot Thymeleaf
- Local HTMX asset for live partial updates
- Simple CSS
- In-memory sample data, no database

## Run

Use JDK 21. On this machine, Java 25 is installed as the default Java, so set `JAVA_HOME` to the already installed JDK 21 before running Gradle:

```powershell
cd E:\Project\technical-service-network-demo
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='E:\Project.gradle-cache'
.\gradlew.bat bootRun
```

Open:

```text
http://localhost:8080
```

## Build And Test

```powershell
cd E:\Project\technical-service-network-demo
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='E:\Project.gradle-cache'
.\gradlew.bat clean build
```

## Implemented Features

- Persian-first UI with FA / EN language switch
- RTL layout for Persian and LTR layout for English
- Session-based role switch: Customer, Technician, Admin, Owner
- Home page with marketplace hero, stats, service categories, featured specialists, and Admin/Owner demo cards
- Live technician search and filtering by name, skill, category, city, minimum rating, and availability
- Technician cards with city, category, rating stars, completed jobs, distance, fee, verification, skills, and open slot count
- Technician profile with bio, stats, skills, booking panel, available slots, reviews, and rating summary
- Booking flow with friendly success/error states, request ID, updated available slots, `.ics` download, and Google Calendar link
- Review submission with HTMX refresh, success/error message, and updated rating
- Technician dashboard with request table and accept/reject status updates
- Valid calendar route: `/calendar/{requestId}.ics`
- Local JavaScript safety net for the simple demo HX flows, so the presentation is not dependent on a CDN connection
- Lightweight automated tests for page smoke checks, calendar export after booking, and repository search/booking behavior

## Demo Video Scenario

1. Start on the home page and introduce the system as a technical-service marketplace.
2. Show the stats, service categories, and featured specialists.
3. Switch FA / EN to show bilingual and RTL/LTR behavior.
4. Use the role selector to show the different system actors.
5. Open `/technicians` and search/filter by category, city, rating, and availability.
6. Open a technician profile.
7. Book an available time slot and show the request ID.
8. Download the `.ics` file or open the Google Calendar link.
9. Submit a review and show the updated review list/rating.
10. Open the technician dashboard and accept/reject a request.
11. Connect the demo back to Systems Analysis and Design: actors, entities, workflows, and system boundaries.

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

## Screenshots

Screenshot assets live in `screenshots/`.

Suggested presentation screenshots:

- Home page
- Search and filters
- Technician profile
- Booking success
- Technician dashboard

## Known Limitations

This is an in-memory academic demo. It intentionally does not include production authentication, a database, payments, real GPS, SMS/email notifications, document upload, or persistent storage. The goal is to demonstrate core Systems Analysis and Design workflows in a runnable web app.
