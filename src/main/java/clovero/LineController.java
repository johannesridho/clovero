package clovero;

import clovero.logger.CloverLogger;
import clovero.logger.CloverLoggerFactory;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;

@LineMessageHandler
public class LineController {
    private static final CloverLogger logger = CloverLoggerFactory.getCloverLogger(LineController.class);
    private final LineService lineService;

    @Autowired
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        logger.info("Received line webhook with MessageEvent={}", event);
        lineService.handleTextMessageEvent(event);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        logger.info("Received line webhook with Event={}", event);
    }
}
