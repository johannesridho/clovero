package clovero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EnhancedMessageSource {

    private final MessageSource messageSource;

    @Autowired
    public EnhancedMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, new Locale("in","ID"));
    }
}
