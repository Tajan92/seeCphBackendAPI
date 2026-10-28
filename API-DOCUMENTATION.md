# seeCph Backend API – Documentation

REST API for seeCph, an app for events in Copenhagen. Events come from organizers, admins and from api's. Attendees can save events as favourites or likes, and organizers can buy adverts for their events.

- **Base URL (local):** `http://localhost:7070/api/v1`
- **Deployed URL:** `http://seecph.theisrud.dk/api/v1`
- **Data format:** JSON (`Content-Type: application/json`)
- **Dates:** `yyyy-MM-dd` (e.g. `"2027-06-12"`) · **Times:** `HH:mm` (e.g. `"12:30"`)

## Authentication and roles

Log in with `POST /login` to get a JWT. Send it with every protected request:

```
Authorization: Bearer <token>
```

| Role | Who |
|---|---|
| ANYONE | Everyone, no token needed |
| ATTENDEE | Normal user who can save favourites and likes |
| ORGANIZER | Creates events and adverts. Must be approved (status `ACTIVE`) by an admin before they can log in (default status is set to pending when someone creates an orgnizer) |
| ADMIN | Full access |


---

## Security

| Method | URL | Roles | Request Body (JSON) | Response (JSON) | Error (e) |
|---|---|---|---|---|---|
| **POST** | `/login` | ANYONE | login (L1) | login response (L2) | (e400), (e401), (e403) |
| **PUT** | `/api/v1/users/my/password` | ADMIN ATTENDEE ORGANIZER| (P1) | *204, no body* | (e400), (e401) , (e403) |
Wrong or missing token gives `401`, wrong role gives `403`.

---

## Events

| Method | URL | Roles | Request Body (JSON) | Response (JSON) | Error (e) |
|---|---|---|---|---|---|
| **GET** | `/events` | ANYONE | | (E1) | (e400) |
| **GET** | `/events/active-categories` | ANYONE | | category labels | |
| **GET** | `/events/{id}` | ANYONE | | (E1) | (e404) |
| **GET** | `/events/mine` | ORGANIZER, ADMIN | | (E1) | (e401), (e403) |
| **POST** | `/events` | ORGANIZER, ADMIN | (E2) | (E1) | (e400), (e401), (e403) |
| **PUT** | `/events/{id}` | ORGANIZER (own), ADMIN | (E2) | (E1) | (e400), (e401), (e403), (e404) |
| **DELETE** | `/events/{id}` | ORGANIZER (own), ADMIN | | *204* | (e401), (e403), (e404) |
| **POST** | `/events/ticketmaster` | Header `x-api-key` | | (T1) | (e401) |

### Query parameters for `GET /events`

All are optional and can be combined.

| Parameter | Type | Example | Description |
|---|---|---|---|
| `search` | String | `copenhagen` | Searches title, description, address, city and postal code (not case-sensitive) |
| `category` | String | `Extreme` | Category label as returned by `/events/active-categories` |
| `startDate` | Date | `2026-11-01` | Only events on or after this date |
| `postalCode` | String | `2800` | Only events with this postal code |
| `page` | Number | `0` | Page number, starts at 0. Default `0` |
| `pageSize` | Number | `20` | Events per page, 1–100. Default `20` |

Results are sorted by start date (earliest first). An unknown category or a wrong date format gives `400`.

---

## Users

| Method | URL | Roles | Request Body (JSON) | Response (JSON) | Error (e) |
|---|---|---|---|---|---|
| **POST** | `/users/attendee` | ANYONE | (U2) | (U1) | (e400), (e409) |
| **POST** | `/users/organizer` | ANYONE | (O2) | (O1) | (e400), (e409) |
| **POST** | `/users/admin` | ADMIN | (U2) | (U1) | (e400), (e401), (e403), (e409) |
| **GET** | `/users/my` | ATTENDEE, ORGANIZER, ADMIN | | (U1) | (e401) |
| **PUT** | `/users/my` | ATTENDEE, ORGANIZER, ADMIN | (U3) | (U1) | (e400), (e401), (e409) |
 | **DELETE** | `/users/my` | ATTENDEE, ORGANIZER |  | 204, no body | (e403), (e404) |
| **GET** | `/users` | ADMIN | | (U1) | (e401), (e403) |
| **GET** | `/users/{id}` | ADMIN | | (U1) | (e401), (e403), (e404) |
| **PUT** | `/users/{id}` | ADMIN | (U3) | (U1) | (e400), (e401), (e403), (e404), (e409) |
| **DELETE** | `/users/{id}` | ADMIN | | *204, no body* | (e401), (e403), (e404) |

### Organizer administration

| Method | URL | Roles | Request Body (JSON) | Response (JSON) | Error (e) |
|---|---|---|---|---|---|
| **GET** | `/users/organizers?status=PENDING` | ADMIN | | (O1) | (e400), (e401), (e403) |
| **PUT** | `/users/{id}/status` | ADMIN | (O3) | (O1) | (e400), (e401), (e403), (e404) |

`status` can be `PENDING` (default), `ACTIVE`, `REJECTED`, `PAUSED` or `INACTIVE`.

### Favourites and likes

| Method | URL | Roles | Request Body (JSON) | Response (JSON) | Error (e) |
|---|---|---|---|---|---|
| **GET** | `/users/my/favorites` | ATTENDEE | | (E1) | (e401), (e403) |
| **POST** | `/users/my/favorites/{eventId}` | ATTENDEE | | *204, no body* | (e401), (e403), (e404) |
| **DELETE** | `/users/my/favorites/{eventId}` | ATTENDEE | | *204, no body* | (e401), (e403), (e404) |
| **GET** | `/users/my/liked` | ATTENDEE | | (E1) | (e401), (e403) |
| **POST** | `/users/my/liked/{eventId}` | ATTENDEE | | *204, no body* | (e401), (e403), (e404) |
| **DELETE** | `/users/my/liked/{eventId}` | ATTENDEE | | *204, no body* | (e401), (e403), (e404) |

---

## Adverts

| Method | URL | Roles | Request Body (JSON) | Response (JSON) | Error (e) |
|---|---|---|---|---|---|
| **GET** | `/adverts/active` | ANYONE | | (A1) | (e400) |
| **GET** | `/adverts/active?placement=FRONTPAGE_HEADER` | ANYONE | | (A1) | (e400) |
| **GET** | `/adverts/my-adverts` | ORGANIZER, ADMIN | | (A1) | (e401), (e403) |
| **POST** | `/adverts` | ORGANIZER (own events), ADMIN | (A2) | (A1) | (e400), (e401), (e403), (e404) |
| **GET** | `/adverts` | ADMIN | | (A1) | (e401), (e403) |
| **GET** | `/adverts/{id}` | ADMIN | | (A1) | (e401), (e403), (e404) |
| **PUT** | `/adverts/{id}` | ADMIN | (A2) | (A1) | (e400), (e401), (e403), (e404) |
| **DELETE** | `/adverts/{id}` | ADMIN | | *204, no body* | (e401), (e403), (e404) |

`/adverts/active` only returns adverts where today is between `startDate` and `endDate`.

---

## Request Body and Response Formats

### (L1) Login request

```json
{
  "email": String (email),
  "password": String
}
```

### (L2) Login response

```json
{
  "token": String (JWT),
  "user": user (U1)
}
```

### (E1) Event response

```json
{
  "id": Number,
  "title": String,
  "description": String,
  "price": Number | null,
  "free": Boolean,
  "city": String,
  "postalCode": String,
  "address": String,
  "startTime": String ("HH:mm") | null,
  "startDate": String ("yyyy-MM-dd") | null,
  "url": String,
  "primaryImageUrl": String,
  "category": String (category label, e.g. "Hip-Hop/Rap")
}
```

Ticketmaster events may have `null` for price, time, date and address fields.

### (E2) Event request (for POST and PUT, don't provide id)

```json
{
  "title": String (required),
  "description": String (required),
  "price": Number (0 or higher),
  "free": Boolean,
  "city": String (required),
  "postalCode": String (required),
  "address": String (required),
  "startTime": String ("HH:mm"),
  "startDate": String ("yyyy-MM-dd", required, today or later),
  "url": String (required),
  "primaryImageUrl": String (required unless useDefaultImage is true),
  "useDefaultImage": Boolean,
  "category": String (category label, required)
}
```

The owner of the event is taken from the token, not from the body.

### (P1) Change password request

```json
{
  "password": String (current password, required),
  "newPassword": String (min. 8 characters, at least one number and one special character),
  "newPasswordCheck": String (must match newPassword)
}
```
Token still exist after password change if still valid. 

### (U1) User response

```json
{
  "id": Number,
  "name": String,
  "email": String (email),
  "phone": String,
  "userRole": String ["ATTENDEE" | "ORGANIZER" | "ADMIN"]
}
```

### (U2) User request (for POST, don't provide id)

```json
{
  "name": String (required),
  "email": String (email, required),
  "phone": String (8 digits, or international format with + or 00),
  "password": String (min. 8 characters, at least one number and one special character),
  "passwordCheck": String (must match password)
}
```

### (U3) User update request

```json
{
  "name": String (required),
  "email": String (email, required),
  "phone": String (required)
}
```

### (O1) Organizer response

```json
{
  "id": Number,
  "name": String,
  "email": String (email),
  "phone": String,
  "userRole": "ORGANIZER",
  "status": String ["PENDING" | "ACTIVE" | "REJECTED" | "PAUSED" | "INACTIVE"]
}
```

### (O2) Organizer request

```json
{
  "organizerName": String (company or organisation name, required),
  "input": user (U2)
}
```

### (O3) Organizer status request

```json
{
  "status": String ["PENDING" | "ACTIVE" | "REJECTED" | "PAUSED" | "INACTIVE"]
}
```

### (A1) Advert response

```json
{
  "id": Number,
  "eventDTOResponse": event (E1),
  "userDTOResponse": user (U1),
  "addPlacement": String ["FRONTPAGE_HEADER" | "FRONTPAGE_HIGHLIGHT"],
  "startDate": String ("yyyy-MM-dd"),
  "endDate": String ("yyyy-MM-dd")
}
```

### (A2) Advert request (for POST and PUT, don't provide id)

```json
{
  "eventId": Number (required),
  "addPlacement": String ["FRONTPAGE_HEADER" | "FRONTPAGE_HIGHLIGHT"] (required),
  "startDate": String ("yyyy-MM-dd", today or later),
  "endDate": String ("yyyy-MM-dd", on or after startDate)
}
```
### (T1) Sync Response

```json
{
  "text": "Ticketmaster sync started successfully"
}
```
With response code `202`

---

## Errors

(e) All errors are returned in this format, with the HTTP status code matching the type of error:

```json
{ "error": "Explains the problem" }
```

| Code | When | Example |
|---|---|---|
| (e400) | Invalid input or query parameter | `{ "error": "Title must be filled out, Start date must be today or in the future" }` |
| (e401) | Missing or invalid token, or wrong email/password | `{ "error": "Missing Authorization" }` · `{ "error": "Invalid user or password" }` |
| (e403) | Logged in, but not allowed | `{ "error": "You do not have access to this resource" }` · `{ "error": "You can only change your own events" }` · `{ "error": "Organizer account is not active" }` |
| (e404) | The resource does not exist | `{ "error": "Event not found with id: 5" }` |
| (e409) | Conflict with existing data | `{ "error": "Email is already in use" }` |
| (e500) | Unexpected server error | `{ "error": "An unexpected internal server error occurred" }` |

If several validation rules fail at once, all messages are returned in one string, separated by commas.

Ticketmaster sync endpoint gives exception:`Unauthorized` with status `401`.
