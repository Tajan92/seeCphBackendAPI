package app.entities.users;

import app.entities.Event;
import app.enums.UserRole;
import app.exceptions.DatabaseException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Attendee extends User {
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "attendee_favorite_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> favoriteEvents;

    public void addFavoriteEvent(Event event) {
        if (event == null) {
            throw new DatabaseException("Event not found");
        }
        if (event != null) {
            if (this.favoriteEvents == null) {
                this.favoriteEvents = new HashSet<>();
            }
            this.favoriteEvents.add(event);
        }
    }

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "attendee_liked_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> likedEvents;

    public void addLikedEvent(Event event) {
        if (event == null) {
            throw new DatabaseException("Event not found");
        }
        if (event != null) {
            if (this.likedEvents == null) {
                this.likedEvents = new HashSet<>();
            }
            this.likedEvents.add(event);
        }
    }

    @PrePersist
    public void prePersist() {
        this.addUserRole(UserRole.ATTENDEE);
    }
}
