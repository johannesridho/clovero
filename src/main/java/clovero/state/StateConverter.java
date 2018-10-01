package clovero.state;

import javax.persistence.AttributeConverter;
import java.util.Locale;

public class StateConverter implements AttributeConverter<State, String> {

    @Override
    public String convertToDatabaseColumn(State attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public State convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return State.valueOf(dbData.toUpperCase(Locale.ENGLISH));
    }
}
