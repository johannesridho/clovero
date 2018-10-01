package clovero.cache;

import clovero.jpa.ZonedDateTimeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "cache")
@Getter
@Setter
public class Cache {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "`key`")
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "expired_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime expiredAt;
}
