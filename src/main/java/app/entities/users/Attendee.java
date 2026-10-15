package app.entities.users;

import app.entities.Event;
import app.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Objects;
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
    @Builder.Default
    private Set<Event> favoriteEvents = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "attendee_liked_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    @Builder.Default
    private Set<Event> likedEvents = new HashSet<>();

    public void addLikedEvent(Event event) {
            this.likedEvents.add(Objects.requireNonNull(event));
    }

    public void removeLikedEvent(Event event) {
        this.likedEvents.remove(event);
    }

    public void addFavoriteEvent(Event event) {
        this.favoriteEvents.add(Objects.requireNonNull(event));
    }

    public void removeFavoriteEvent(Event event) {
        this.favoriteEvents.remove(event);
    }

    @PrePersist
    public void prePersist() {
        this.addUserRole(UserRole.ATTENDEE);
    }
}
