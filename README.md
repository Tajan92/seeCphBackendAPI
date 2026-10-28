# seeCph - REST API

seeCph is a REST API that gathers events in Copenhagen in one place. Events are fetched automatically from API every night and can also be created by approved organizers and admins. Users can search and filter events, save favourites and like events, and organizers can advertise their events on the front page. Searching events is also possible without registration, to make the interaction for users easier.

**Live API:** https://seecph.theisrud.dk/api/v1/events?page1

**Project Blog:** https://theisrud.dk/blog/seecph-blog/

---

## Features

- **Search and filter events** by text, date, category and postal code, with pagination.
- **User accounts** 3 roles: attendee, organizer and admin.
- **JWT login** with role-based access to endpoints.
- **Favourites and likes** for attendees.
- **Organizer accounts with approval:** new organizers must be approved by an admin before they can log in.
- **Creating, editing and deleting events**, where organizers can only change their own.
- **Adverts** is possible.
- **Daily synchronization with Ticketmaster** updating existing events without duplicates, deleting events that's not valid anymore. Ready for synchronization of new API's.
- **AI-generated descriptions** (Gemini) for events without a description.
- **Automatic geocoding** of addresses (Nominatim).
- **Profile management:** update details, change password and delete your own account.

## Technologies

| Area | Technology |
|---|---|
| Language | Java 25 |
| Web | Javalin 6 |
| Database | PostgreSQL 16, Hibernate 7 (JPA) |
| Security | JWT, BCrypt |
| Testing | JUnit 6, REST Assured, Testcontainers |
| Build | Maven |
| Deployment | Docker, Docker Hub, GitHub Actions, Watchtower, Caddy |
| External APIs | Ticketmaster Discovery API, Google Gemini, OpenStreetMap Nominatim |

---

## Project structure

```
src/main/java/app
├── config       Setup of Javalin, Hibernate and default admin
├── controller   Routes (Controller) and request handling (Handler)
├── service      Business logic
├── dao          Database access
├── entities     JPA entities (Event, Address, Advert, User and subclasses)
├── dto          Request and response objects
├── mapper       Conversion between entities and DTOs
├── security     Issuing and verifying JWTs
├── enums        Categories, roles, statuses and placements
├── exceptions   Custom exceptions mapped to HTTP status codes
└── utils        Helper classes (configuration, validation, geocoding etc.)
```

The domain model, user stories and class diagram can be found in the [`docs`](docs) folder.

---

#### By

Theis Rud Halkjær – Datamatiker at EK Lyngby (AP Graduate in Computer Science), 3rd semester, autumn 2026.
