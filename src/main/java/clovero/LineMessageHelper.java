package clovero;

import clovero.user.User;
import clovero.user.UserService;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.message.imagemap.ImagemapAction;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LineMessageHelper {

    private static final int MAX_BUTTON_TEMPLATE_TEXT_LENGTH = 160;
    private static final int MAX_ACTION_SIZE = 4;
    private static final int MAX_TEMPLATE_ALT_TEXT_LENGTH = 400;
    private static final int MAX_ACTION_LABEL_LENGTH = 20;
    private static final int MAX_TEXT_MESSAGE_LENGTH = 2000;

    private static final String MAIN_MENU_IMAGE_URL = "/image/menu.jpg";

    public static final List<String> MAIN_MENU_BUTTONS = Collections.unmodifiableList(Arrays.asList(
            Buttons.GAME.getValue(), Buttons.MY_SCORE.getValue(), Buttons.HIGH_SCORES.getValue(),
            Buttons.INSTRUCTIONS.getValue()));

    public static final List<String> ROUND_FINISHED_BUTTONS = Collections.unmodifiableList(Arrays.asList(
            Buttons.CONTINUE.getValue(), Buttons.CHANGE_CATEGORY.getValue(), Buttons.EXIT.getValue()));

    public static final List<String> CONFIRMATION_BUTTONS = Collections.unmodifiableList(Arrays.asList(
            Buttons.YES.getValue(), Buttons.NO.getValue()));

    EnhancedMessageSource source;
    UserService userService;
    BotConfig botConfig;

    public List<Message> getMainMenuMessages() {
        final String imageUrl = botConfig.getUrl() + MAIN_MENU_IMAGE_URL;
        final Message message = createButtonTemplateMessageFromTexts("Menu Utama", MAIN_MENU_BUTTONS, imageUrl);

        final List<Message> messages = new ArrayList<>();
        messages.add(message);

        return messages;
    }

    public List<Message> createMessagesWithMainMenu(List<Message> messages) {
        final List<Message> updatedMessages = new ArrayList<>(messages);
        final String imageUrl = botConfig.getUrl() + MAIN_MENU_IMAGE_URL;
        final Message message = createButtonTemplateMessageFromTexts("Menu Utama", MAIN_MENU_BUTTONS, imageUrl);
        updatedMessages.add(message);

        return updatedMessages;
    }

    public List<Message> createMessagesWithRoundFinishedMenu(List<Message> messages) {
        final List<Message> updatedMessages = new ArrayList<>(messages);
        final Message message = createButtonTemplateMessageFromTexts("Menu", ROUND_FINISHED_BUTTONS);
        updatedMessages.add(message);

        return updatedMessages;
    }

    public List<Message> getCurrentUserScore(User user) {
        final List<Message> messages = createTextMessageList("Skor kamu saat ini adalah " + user.getPoint() + " poin. " +
                "Yuk main lagi biar skornya bertambah");
        messages.add(createSupportSticker());

        return messages;
    }

    public List<Message> getHighscores() {
        final List<User> users = userService.getTop10User();
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < users.size() - 1; i++) {
            stringBuilder.append(i + 1)
                    .append(". ")
                    .append(users.get(i).getName())
                    .append(" ")
                    .append(users.get(i).getPoint())
                    .append("\n");
        }

        stringBuilder.append(users.size())
                .append(". ")
                .append(users.get(users.size() - 1).getName())
                .append(" ")
                .append(users.get(users.size() - 1).getPoint());

        return createTextMessageList(stringBuilder.toString());
    }

    public List<Message> getInstructions() {
        return createTextMessageList(source.getMessage("instructions"));
    }

    public List<Message> createQuitConfirmationMessage() {
        final List<Message> messages = new ArrayList<>();
        messages.add(createTextMessage("Poin kamu akan berkurang " + Constant.IN_GAME_QUIT_PENALTY_POINT + " jika " +
                "kamu keluar ketika permainan sedang berlangsung. Apakah kamu yakin?"));
        final Message message = createButtonTemplateMessageFromTexts("Konfirmasi", CONFIRMATION_BUTTONS);
        messages.add(message);

        return messages;
    }

    public List<Message> createTextMessageList(String text) {
        final Message message = createTextMessage(text);

        final List<Message> messages = new ArrayList<>();
        messages.add(message);

        return messages;
    }

    public List<Message> createTextMessageList(List<String> texts) {
        final List<Message> messages = new ArrayList<>();
        texts.forEach(text -> messages.add(createTextMessage(text)));

        return messages;
    }

    public List<Message> createImageMessageList(String text, String originalImageUrl, String previewImageUrl) {
        final List<Message> messages = new ArrayList<>();
        messages.add(createTextMessage(text));
        messages.add(new ImageMessage(originalImageUrl, previewImageUrl));

        return messages;
    }

    public List<Message> createAudioMessageList(String text, String audioUrl, Integer duration) {
        final List<Message> messages = new ArrayList<>();
        messages.add(createTextMessage(text));
        messages.add(new AudioMessage(audioUrl, duration));

        return messages;
    }

    public List<Message> createButtonResponse(String title, List<String> texts) {
        final List<Message> messages = new ArrayList<>();
        final String text;
        if (texts.size() > MAX_BUTTON_TEMPLATE_TEXT_LENGTH) {
            final Message caption = new TextMessage(title);
            text = "Pilih opsi";
            messages.add(caption);
        } else {
            text = title;
        }

        final List<Action> actions = createActionsFromTexts(texts);

        final Message message;

        if (actions.isEmpty()) {
            message = new TextMessage(title);
        } else {
            final Template template = new ButtonsTemplate(null, null, text, actions);

            final String templateText = title.length() > MAX_TEMPLATE_ALT_TEXT_LENGTH
                    ? title.substring(0, MAX_TEMPLATE_ALT_TEXT_LENGTH)
                    : title;

            message = new TemplateMessage(templateText, template);
        }

        messages.add(message);

        return messages;
    }

    public Message createButtonTemplateMessageFromTexts(String text, List<String> texts) {
        return createButtonTemplateMessageFromTexts(text, texts, null);
    }

    public Message createButtonTemplateMessageFromTexts(String text, List<String> texts, String imageUrl) {
        final List<Action> actions = createActionsFromTexts(texts);
        if (actions.size() == 0) {
            return new TextMessage(text);
        }

        final Template template = new ButtonsTemplate(imageUrl, null, text, actions);

        return new TemplateMessage(text, template);
    }

    public TextMessage createTextMessage(String text) {
        if (text.length() > MAX_TEXT_MESSAGE_LENGTH) {
            return new TextMessage(text.substring(0, MAX_TEXT_MESSAGE_LENGTH));
        }
        return new TextMessage(text);
    }

    public List<Action> createActionsFromTexts(List<String> texts) {
        final List<Action> actions = new ArrayList<>();

        int numberOfActions = 0;
        int i = 0;
        while (numberOfActions < MAX_ACTION_SIZE && i < texts.size()) {
            actions.add(createMessageAction(texts.get(i)));
            numberOfActions++;

            i++;
        }

        return actions;
    }

    public Message createSucceedSticker() {
        final List<StickerMessage> stickerMessages = new ArrayList<>();
        stickerMessages.add(new StickerMessage("1", "13"));
        stickerMessages.add(new StickerMessage("1", "114"));
        stickerMessages.add(new StickerMessage("1", "138"));
        stickerMessages.add(new StickerMessage("1", "407"));
        stickerMessages.add(new StickerMessage("2", "144"));

        return getRandomSticker(stickerMessages);
    }

    public Message createFailedSticker() {
        final List<StickerMessage> stickerMessages = new ArrayList<>();
        stickerMessages.add(new StickerMessage("1", "9"));
        stickerMessages.add(new StickerMessage("1", "16"));
        stickerMessages.add(new StickerMessage("1", "104"));
        stickerMessages.add(new StickerMessage("1", "111"));
        stickerMessages.add(new StickerMessage("1", "135"));
        stickerMessages.add(new StickerMessage("2", "152"));
        stickerMessages.add(new StickerMessage("2", "173"));
        stickerMessages.add(new StickerMessage("2", "174"));
        stickerMessages.add(new StickerMessage("2", "524"));

        return getRandomSticker(stickerMessages);
    }

    public Message createSupportSticker() {
        final List<StickerMessage> stickerMessages = new ArrayList<>();
        stickerMessages.add(new StickerMessage("1", "2"));
        stickerMessages.add(new StickerMessage("1", "134"));
        stickerMessages.add(new StickerMessage("1", "406"));
        stickerMessages.add(new StickerMessage("2", "34"));
        stickerMessages.add(new StickerMessage("2", "45"));
        stickerMessages.add(new StickerMessage("2", "501"));

        return getRandomSticker(stickerMessages);
    }


    private Message getRandomSticker(List<StickerMessage> stickerMessages) {
        return stickerMessages.get(new Random().nextInt(stickerMessages.size()));
    }

    private String createAltText(String text) {
        if (text.length() > MAX_TEMPLATE_ALT_TEXT_LENGTH) {
            return text.substring(0, MAX_TEMPLATE_ALT_TEXT_LENGTH);
        }

        return text;
    }

    private List<ImagemapAction> createImagemapActions(int height, int width, int row, int column,
                                                       List<String> contents) {

        final List<String> normalizedContents = new ArrayList<>(contents);

        final boolean isContentsOdd = contents.size() % 2 != 0;
        if (isContentsOdd) {
            final String lastContent = contents.get(contents.size() - 1);
            for (int i = 0; i < row - 1; i++) {
                normalizedContents.add(lastContent);
            }
        }

        final List<ImagemapAction> imagemapActions = new ArrayList<>();

        int i = 0;
        final int imageWidthPerNominal = width / column;
        final int imageHeightPerNominal = height / row;

        for (int y = 0; y < row; y++) {
            for (int x = 0; x < column; x++) {
                final ImagemapAction action = new MessageImagemapAction(
                        normalizedContents.get(i),
                        new ImagemapArea(
                                x * imageWidthPerNominal,
                                y * imageHeightPerNominal,
                                imageWidthPerNominal,
                                imageHeightPerNominal));

                imagemapActions.add(action);
                i++;
            }
        }

        return imagemapActions;
    }

    private Action createMessageAction(String text) {
        final String label = text.length() > MAX_ACTION_LABEL_LENGTH
                ? text.substring(0, MAX_ACTION_LABEL_LENGTH)
                : text;

        return new MessageAction(label, text);
    }
}
