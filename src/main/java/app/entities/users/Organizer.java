package app.entities.users;

import app.entities.Advert;
import app.entities.Event;
import app.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Organizer extends User {
    @Column(name = "organizer_name")
    private String organizerName;

    @Enumerated(EnumType.STRING)
    private Status accountStatus;

    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //TODO: Decide fetchType and cascadeType
    private Set<Advert> adverts;

    private void addAdvert(Advert advert) {
        this.adverts.add(advert);
        if (advert != null) {
            advert.setOrganizer(this);
        }
    }

    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //TODO: Decide fetchType and cascadeType
    private Set<Event> events;

    private void addEvent(Event event) {
        this.events.add(event);
        if (event != null) {
            event.setOrganizer(this);
        }
    }
}
