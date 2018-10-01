package clovero.hint;

import javax.persistence.AttributeConverter;
import java.util.Locale;

public class HintTypeConverter implements AttributeConverter<HintType, String> {

    @Override
    public String convertToDatabaseColumn(HintType attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public HintType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return HintType.valueOf(dbData.toUpperCase(Locale.ENGLISH));
    }
}
