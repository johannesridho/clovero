package clovero.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Converter
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Long> {

    @Override
    public Long convertToDatabaseColumn(ZonedDateTime attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.toInstant().toEpochMilli();
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Long date) {
        if (date == null) {
            return null;
        }

        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("UTC"));
    }
}
