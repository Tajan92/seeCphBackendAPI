package app.entities.users;

import app.entities.events.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attendee")
public class Attendee extends User {
    @Column(name = "favorite_events")
    private Set<Event> favoriteEvents;
    @Column(name = "liked_events")
    private Set<Event> likedEvents;
}
