package clovero;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class LineConfiguration {
    @Value("${line.bot.channelToken}")
    private String channelToken;
}
