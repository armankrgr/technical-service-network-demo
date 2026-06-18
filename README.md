# Technical Service Network Demo

University demo project for **سامانه شبکه ارتباطی بین افراد فنی**. The app demonstrates a lightweight service marketplace where customers discover technical specialists, filter profiles, reserve an available slot, export a calendar event, leave reviews, and track requests from a technician dashboard.

GitHub repository: <https://github.com/armankrgr/technical-service-network-demo>

## Stack

- Kotlin 1.9
- Spring Boot Web + Thymeleaf
- Local HTMX asset for live partial updates
- Vanilla CSS and JavaScript
- In-memory sample data, no database

## Run

Use JDK 21. On this machine, set the Gradle cache to the E: drive before running:

```powershell
cd E:\Project\technical-service-network-demo
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='E:\Project.gradle-cache'
.\gradlew.bat bootRun
```

Open `http://localhost:8080`.

## Build And Test

```powershell
cd E:\Project\technical-service-network-demo
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='E:\Project.gradle-cache'
.\gradlew.bat clean build
```

## Polished Demo Features

- Persian-first polished UI with RTL layout and FA / EN language switch
- Session-based role switch: Customer, Technician, Admin, Owner
- Full-width home hero with Persian headline, two CTAs, trust badges, stats, and local SVG/CSS service-network illustration
- 15 service categories with icon, title, description, active specialist count, and direct search links
- 24 sample technicians with city, category, title, skills, bio, rating, jobs, verification, response time, price, distance, slots, and reviews
- Prominent search/filter screen with sticky filters, category chips, city/min-rating/availability/sort controls, live result count, loading indicator, empty state, and reset action
- Rich technician cards with avatar initials, verified/pending badge, city/distance, rating, completed jobs, response time, starting price, available slots, skill chips, and “مشاهده و رزرو”
- Technician profile with cover header, avatar, verified badge, rating summary, metrics, skill tags, service details, visual booking panel, review distribution, and similar specialists
- Booking flow with visual slot cards, friendly unavailable slots, success card/toast, request code, selected time, status, `.ics` download, Google Calendar link, and dashboard link
- Reviews with visual star selector, average/count, rating distribution bars, author/date/text list, HTMX refresh, and friendly invalid-rating handling
- Technician dashboard with summary cards, responsive request cards, status chips, timeline, and accept/reject HTMX actions
- Admin/Owner demo section for explaining quality control, growth decisions, and Systems Analysis and Design role coverage
- Local JavaScript polish: mobile nav toggle, toast, smooth scroll, selected slot highlight, selected rating state, filter loading state, count-up stats, and double-submit protection

## Demo Video Scenario

1. Start on the home page and introduce the system as a technical-service marketplace.
2. Show the hero, trust badges, stats, 15 categories, and featured specialists.
3. Switch FA / EN to demonstrate RTL/LTR behavior.
4. Use the role selector to explain customer, technician, admin, and owner actors.
5. Open `/technicians` and filter by category, city, rating, availability, and sort order.
6. Open a technician profile and compare skills, reviews, price, response time, and similar specialists.
7. Select a visual time slot, submit a booking, and show the success card with request code.
8. Download the `.ics` file or open the Google Calendar link.
9. Submit a review and show the updated rating/review fragment.
10. Open the technician dashboard and accept/reject the request.
11. Connect the demo back to Systems Analysis and Design: actors, entities, workflows, boundaries, and limitations.

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

Screenshot assets live in `screenshots/`:

- `screenshots/home.png`
- `screenshots/search.png`
- `screenshots/profile.png`
- `screenshots/booking-success.png`
- `screenshots/dashboard.png`
- `screenshots/admin-owner.png`

## Known Limitations

This is an in-memory academic demo. It intentionally does not include production authentication, a database, payments, real GPS, SMS/email notifications, document upload, or persistent storage. The goal is to demonstrate core Systems Analysis and Design workflows in a runnable, understandable, presentation-ready web app.
