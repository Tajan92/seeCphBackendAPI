## EventDao
- public List<Event> searchAndFilterEvent(String search, EventCategory category, LocalDate startDate, String postalCode, int page, int pageSize)
- public List<EventCategory> readDistinctCategoriesInDb()
- public List<Event> readEventsByUserId(int userId) `needs service, handler, endpoint`
- public List<Event> readEventsByAdvertId(int advertId)`needs service, handler, endpoint`
- public List<Event> readFavoritEventsByUserId(int userId) `needs service, handler, endpoint`
- public List<Event> readLikedEventsByUserId(int userId) `needs service, handler, endpoint`

## EventService
- readAllEventsBySearchAndFilter(String search, EventCategory category, LocalDate startDate, String postalCode, int page, int pageSize)

## EventHandler
- getAll(Context ctx) updated

## EvenController
- getAll endpoint addition ?(category=ROCK, search=Whatever, postalCode=2100, startDate=2026-11-11, page=2, pageSize=20) & between.
- eg. /api/v1/events?category=ROCK&search=Whatever&postalCode=2100&startDate=2026-11-11&page=2&pageSize=20