package clovero;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class Constant {

    public static final int HOUR_DIFFERENCE = 7;

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(HOUR_DIFFERENCE);

    public static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    public static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter MYSQL_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern(Constant.MYSQL_DATE_FORMAT)
            .withZone(Constant.ZONE_OFFSET);

    public static final Long MAX_WRONG_ANSWER_PENALTY_POINT = 30L;
    public static final Long WRONG_ANSWER_PENALTY_POINT = 20L;
    public static final Long USING_HINT_PENALTY_POINT = 10L;
    public static final Long IN_GAME_QUIT_PENALTY_POINT = 50L;

    public static final Integer TOTAL_QUIZ_PER_ROUND = 3;
    public static final Long BONUS_POINT_PER_ROUND = 100L;

    public static final Integer MAX_WRONG_ANSWER = 3;

    private Constant() {
    }
}
