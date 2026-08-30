package app.entities.users;

import app.entities.events.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendee")
public class Attendee extends User {
    @Column(name = "favorite_events")
    private Set<Event> favoriteEvents;
    @Column(name = "liked_events")
    private Set<Event> likedEvents;

    @ManyToMany (mappedBy = "events") // TODO: Need more info inn link table??
    @Setter
    private Event event;
}
