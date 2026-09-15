package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "image_url")
@Entity
public class ImageUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String url;
    private String ratio;
    private int width;
    private int height;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Event event;
}