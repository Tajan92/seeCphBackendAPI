package app.entities.users;

import app.entities.Advert;
import app.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organizer")
public class Organizer extends User {
    @Column(name = "organizer_name")
    private String organizerName;
    private Set<Advert> adverts;
    @Enumerated(EnumType.STRING)
    private Status accountStatus;
}
