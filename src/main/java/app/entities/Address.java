package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String city;
    private String postalCode;
    private String address;

    @Setter
    @OneToMany(mappedBy = "location", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Event> events;

    public void addEvent(Event event) {
        if (event != null) {
            if (this.events == null) {
                this.events = new HashSet<>();
            }
            this.events.add(event);
            event.setLocation(this);
        }
    }
}