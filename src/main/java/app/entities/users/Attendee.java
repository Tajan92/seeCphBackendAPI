package app.entities.users;

import app.entities.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Attendee extends User {
    @OneToMany(mappedBy = "attendee")
    private Set<Event> favoriteEvents;

    @OneToMany(mappedBy = "attendee")
    private Set<Event> likedEvents;

}
