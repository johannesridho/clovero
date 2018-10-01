package clovero;

import clovero.category.Category;
import clovero.category.CategoryRepository;
import clovero.category.CategoryResponseService;
import clovero.hint.Hint;
import clovero.hint.HintRepository;
import clovero.hint.HintType;
import clovero.logger.CloverLogger;
import clovero.logger.CloverLoggerFactory;
import clovero.quiz.Quiz;
import clovero.quiz.QuizRepository;
import clovero.quizsolver.QuizSolverRepository;
import clovero.quizsolver.QuizSolverService;
import clovero.state.State;
import clovero.user.User;
import clovero.user.UserService;
import clovero.util.JsonUtils;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LineService {
    static CloverLogger logger = CloverLoggerFactory.getCloverLogger(LineService.class);

    LineConfiguration lineConfiguration;
    UserService userService;
    CategoryResponseService categoryResponseService;
    QuizSolverService quizSolverService;
    CategoryRepository categoryRepository;
    QuizRepository quizRepository;
    HintRepository hintRepository;
    QuizSolverRepository quizSolverRepository;
    LineMessageHelper lineMessageHelper;

    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        final String lineId = event.getSource().getUserId();
        final String text = event.getMessage().getText();
        final List<Message> messages = getResponse(lineId, text);
        sendReplyMessage(event.getReplyToken(), messages);
    }

    public String handleTextMessageEvent(String request) {
        final List<Message> messages = getResponse("line id", request);
        return JsonUtils.toJson(messages);
    }

    private List<Message> getResponse(String lineId, String request) {
        final User user = userService.getOrCreateByLineId(lineId);

        if (State.QUIT_CONFIRMATION.equals(user.getState())) {
            return handleQuitConfirmationState(user, request);
        }

        if (Buttons.MAIN_MENU.getValue().equalsIgnoreCase(request) ||
                Buttons.CANCEL.getValue().equalsIgnoreCase(request) ||
                Buttons.EXIT.getValue().equalsIgnoreCase(request)) {

            if (user.getState().equals(State.GAME_QUIZ)) {
                userService.updateState(user, State.QUIT_CONFIRMATION);
                return lineMessageHelper.createQuitConfirmationMessage();
            }

            userService.resetUserQuizData(user);
            userService.resetRoundData(user);
        }

        if (Buttons.INSTRUCTIONS.getValue().equalsIgnoreCase(request) ||
                Buttons.HELP.getValue().equalsIgnoreCase(request)) {
            return lineMessageHelper.getInstructions();
        }

        switch (user.getState()) {
            case GAME_CATEGORY:
                return handleGameCategoryState(user, request);

            case GAME_QUIZ:
                return handleGameQuizState(user, request);

            case MAIN_MENU:
                return handleMainMenuState(user, request);

            case ROUND_FINISHED:
                return handleRoundFinishedState(user, request);

            default:
                return lineMessageHelper.getMainMenuMessages();
        }
    }

    private List<Message> handleMainMenuState(User user, String request) {
        if (Buttons.GAME.getValue().equalsIgnoreCase(request)) {
            userService.updateState(user, State.GAME_CATEGORY);
            return categoryResponseService.getCategoryNames();
        }

        if (Buttons.MY_SCORE.getValue().equalsIgnoreCase(request)) {
            return lineMessageHelper.getCurrentUserScore(user);
        }

        if (Buttons.HIGH_SCORES.getValue().equalsIgnoreCase(request)) {
            return lineMessageHelper.getHighscores();
        }

        return lineMessageHelper.createMessagesWithMainMenu(lineMessageHelper.createTextMessageList("Silakan pilih " +
                "tombol dari menu yang telah tersedia"));
    }

    private List<Message> handleGameCategoryState(User user, String request) {
        final Optional<Category> categoryOptional = categoryRepository.findByName(request);

        if (!categoryOptional.isPresent()) {
            return lineMessageHelper.createTextMessageList("Kategori yang kamu pilih tidak tersedia, " +
                    "silakan pilih kategori yang telah disediakan");
        }

        userService.updateCurrentCategory(user, categoryOptional.get());

        final Optional<Quiz> currentQuizOptional = assignQuizToUser(user, categoryOptional.get());

        if (!currentQuizOptional.isPresent()) {
            return createQuizNotAvailableMessage();
        }

        return createFirstQuizResponse(currentQuizOptional.get());
    }

    private List<Message> handleGameQuizState(User user, String request) {
        if (Buttons.GAME.getValue().equalsIgnoreCase(request) ||
                Buttons.MY_SCORE.getValue().equalsIgnoreCase(request) ||
                Buttons.HIGH_SCORES.getValue().equalsIgnoreCase(request)) {
            return lineMessageHelper.createTextMessageList("Kamu masih dalam permainan. Ketik KELUAR untuk " +
                    "mengakhiri permainan dan kembali ke menu utama");
        }

        final String quizAnswer = user.getCurrentQuiz().getAnswer();
        final String normalizedRequest = normalizeAnswer(request);

        if (quizAnswer.equals(normalizedRequest)) {
            return handleQuizSolved(user);
        }

        if (normalizedRequest.length() == 1 && quizAnswer.contains(normalizedRequest)) {

            userService.updateUserCurrentAnswer(user, normalizedRequest.charAt(0));

            if (user.getUserCurrentAnswer().equals(quizAnswer)) {
                return handleQuizSolved(user);
            }

            return lineMessageHelper.createTextMessageList(addSpaceBetweenChar(user.getUserCurrentAnswer()));
        }

        if (Buttons.HINT.getValue().equalsIgnoreCase(request)) {
            return handleGameHint(user);
        }

        if (user.getCurrentQuizWrongAnswerCount() + 1 == Constant.MAX_WRONG_ANSWER) {
            userService.resetUserQuizData(user);
            userService.decrementPoint(user, Constant.MAX_WRONG_ANSWER_PENALTY_POINT);

            final List<Message> quizFailedMessage = lineMessageHelper.createTextMessageList("Sayang sekali kamu salah " +
                    "menjawab lebih dari " + Constant.MAX_WRONG_ANSWER + " kali. Poin kamu berkurang " +
                    Constant.MAX_WRONG_ANSWER_PENALTY_POINT);

            quizFailedMessage.add(lineMessageHelper.createFailedSticker());

            quizFailedMessage.add(lineMessageHelper.createTextMessage("Kalau bingung, kamu bisa ketik PETUNJUK untuk " +
                    "mendapat bantuan. Coba lagi ya!"));

            return lineMessageHelper.createMessagesWithMainMenu(quizFailedMessage);
        }

        userService.decrementCurrentQuizRemainingPoint(user, Constant.WRONG_ANSWER_PENALTY_POINT);
        userService.incrementCurrentQuizWrongAnswerCount(user);

        return lineMessageHelper.createTextMessageList("Jawaban kamu salah, poin maksimal yang bisa kamu peroleh " +
                "berkurang " + Constant.WRONG_ANSWER_PENALTY_POINT + " poin");
    }

    private List<Message> handleRoundFinishedState(User user, String request) {
        if (Buttons.CHANGE_CATEGORY.getValue().equalsIgnoreCase(request)) {
            userService.updateState(user, State.GAME_CATEGORY);
            return categoryResponseService.getCategoryNames();
        }

        if (Buttons.CONTINUE.getValue().equalsIgnoreCase(request)) {
            return handleGameCategoryState(user, user.getCurrentCategory().getName());
        }

        return lineMessageHelper.createMessagesWithRoundFinishedMenu(
                lineMessageHelper.createTextMessageList("Silakan pilih tombol dari menu yang tersedia"));
    }

    private List<Message> handleQuitConfirmationState(User user, String request) {
        if (Buttons.YES.getValue().equalsIgnoreCase(request)) {
            userService.resetUserQuizData(user);
            userService.resetRoundData(user);
            userService.decrementPoint(user, Constant.IN_GAME_QUIT_PENALTY_POINT);
            return lineMessageHelper.getMainMenuMessages();

        } else if (Buttons.NO.getValue().equalsIgnoreCase(request)) {
            userService.updateState(user, State.GAME_QUIZ);
            return createQuizResponse(user.getCurrentQuiz());
        } else {
            return lineMessageHelper.createQuitConfirmationMessage();
        }
    }

    private List<Message> handleQuizSolved(User user) {
        final Category lastCategory = user.getCurrentCategory();
        final Quiz lastQuiz = user.getCurrentQuiz();
        final Long userLastQuizPoint = user.getCurrentQuizRemainingPoint();

        quizSolverService.addQuizSolver(user);

        userService.incrementPoint(user, user.getCurrentQuizRemainingPoint());
        userService.incrementRoundData(user, userLastQuizPoint, lastQuiz.getPoint());
        userService.resetUserQuizData(user);

        final List<Message> roundSucceedMessage = new ArrayList<>();

        if (user.getCurrentRoundTotalQuizSolved().equals(Constant.TOTAL_QUIZ_PER_ROUND)) {
            roundSucceedMessage.add(
                    lineMessageHelper.createTextMessage("Selamat, kamu mendapatkan " + userLastQuizPoint + " poin. "));

            if (user.getCurrentRoundMaxPoint().equals(user.getCurrentRoundPoint())) {
                userService.incrementPoint(user, Constant.BONUS_POINT_PER_ROUND);
                roundSucceedMessage.add(lineMessageHelper.createTextMessage("Kamu juga mendapatkan bonus " +
                        Constant.BONUS_POINT_PER_ROUND + " poin karena telah menyelesaikan " +
                        Constant.TOTAL_QUIZ_PER_ROUND + " pertanyaan dengan sempurna. "));
            }

            roundSucceedMessage.add(lineMessageHelper.createSucceedSticker());

            roundSucceedMessage.add(lineMessageHelper.createTextMessage("Poin kamu sekarang " + user.getPoint() +
                    ". Tekan tombol lanjut untuk melanjutkan permainan dengan kategori yang sama"));

            userService.resetRoundData(user);
            userService.updateState(user, State.ROUND_FINISHED);
            userService.updateCurrentCategory(user, lastCategory);

            return lineMessageHelper.createMessagesWithRoundFinishedMenu(roundSucceedMessage);
        }

        final Optional<Quiz> nextQuizOptional = assignQuizToUser(user, lastCategory);

        final List<Message> messages = new ArrayList<>();

        final String quizSucceedMessage = "Selamat, kamu mendapatkan " + userLastQuizPoint + " poin. Skor kamu saat " +
                "ini " + user.getPoint();

        messages.add(lineMessageHelper.createTextMessage(quizSucceedMessage));
        messages.add(lineMessageHelper.createSucceedSticker());

        if (!nextQuizOptional.isPresent()) {
            messages.add(lineMessageHelper.createTextMessage("Pertanyaan kuis untuk " +
                    "kategori ini sudah kamu jawab semua, pilih kategori yang lain untuk melanjutkan permainan ya!"));

            return messages;
        }

        userService.updateCurrentCategory(user, lastCategory);

        messages.add(lineMessageHelper.createTextMessage("Pertanyaan selanjutnya"));
        messages.add(lineMessageHelper.createTextMessage(nextQuizOptional.get().getDescription()));
        messages.add(lineMessageHelper.createTextMessage(
                addSpaceBetweenChar(replaceCharWithUnderscore(nextQuizOptional.get().getAnswer()))));

        return messages;
    }

    private Optional<Quiz> assignQuizToUser(User user, Category category) {
        final List<Quiz> quizzes = quizRepository.findByCategoryIdOrderByPointAsc(category.getId());
        if (quizzes.size() == 0) {
            return Optional.empty();
        }

        final List<Quiz> unsolvedQuizzes = quizzes.stream()
                .filter(quiz -> !quizSolverRepository.findByUserIdAndQuizId(user.getId(), quiz.getId()).isPresent())
                .collect(Collectors.toList());

        if (unsolvedQuizzes.size() == 0) {
            return Optional.empty();
        }

        final Long easiestQuizPoint = unsolvedQuizzes.get(0).getPoint();

        final List<Quiz> easiestQuizzes = unsolvedQuizzes.stream()
                .filter(easiestQuiz -> easiestQuiz.getPoint().equals(easiestQuizPoint))
                .collect(Collectors.toList());

        final Quiz assignedQuiz = easiestQuizzes.get(new Random().nextInt(easiestQuizzes.size()));

        userService.updateCurrentQuiz(user, assignedQuiz);
        userService.updateCurrentQuizRemainingPoint(user, assignedQuiz.getPoint());
        userService.updateState(user, State.GAME_QUIZ);
        userService.updateUserCurrentAnswer(user, replaceCharWithUnderscore(assignedQuiz.getAnswer()));

        return Optional.of(assignedQuiz);
    }

    private List<Message> handleGameHint(User user) {
        final List<Hint> hintByQuizOptional = hintRepository.findByQuiz(user.getCurrentQuiz());
        if (hintByQuizOptional.isEmpty()) {
            return lineMessageHelper.createTextMessageList("Petunjuk untuk pertanyaan ini tidak tersedia");
        }

        final Optional<Hint> hintOptional = hintRepository.findByQuizAndNumber(user.getCurrentQuiz(),
                user.getCurrentHintNumber() + 1);

        if (!hintOptional.isPresent()) {
            final List<Message> hintNotAvailableMessages = lineMessageHelper
                    .createTextMessageList("Sayang sekali semua petunjuk sudah diberikan");
            hintNotAvailableMessages.add(lineMessageHelper.createFailedSticker());

            return hintNotAvailableMessages;
        }

        userService.incrementCurrentHintNumber(user);
        userService.decrementCurrentQuizRemainingPoint(user, Constant.USING_HINT_PENALTY_POINT);

        return createHintResponse(hintOptional.get());
    }

    private void sendReplyMessage(String replyToken, List<Message> messages) {
        final ReplyMessage replyMessage = new ReplyMessage(replyToken, messages);

        final Response<BotApiResponse> response;
        try {
            response = LineMessagingServiceBuilder
                    .create(lineConfiguration.getChannelToken())
                    .build()
                    .replyMessage(replyMessage)
                    .execute();
            logger.info("Finished Line reply message with messages={} and response={}", messages, response);
        } catch (IOException e) {
            logger.error("Failed to send Line reply message with messages={} and exception={}", messages,
                    e.getMessage());
        }
    }

    @Async
    public void sendPushMessage(String userId, Message message) {
        final PushMessage pushMessage = new PushMessage(userId, message);

        final Response<BotApiResponse> response;
        try {
            response = LineMessagingServiceBuilder
                    .create(lineConfiguration.getChannelToken())
                    .build()
                    .pushMessage(pushMessage)
                    .execute();
            logger.info("Finished Line push message with message={} and response={}", message, response);
        } catch (IOException e) {
            logger.error("Failed to send Line push message with message={} and exception={}", message,
                    e.getMessage());
        }
    }

    private List<Message> createFirstQuizResponse(Quiz quiz) {
        final List<Message> messages = lineMessageHelper.createTextMessageList(
                Arrays.asList("Permainan dimulai, jawab pertanyaan yang muncul dengan 1 huruf yang kamu anggap " +
                                "ada pada jawaban, atau kamu juga bisa menjawab langsung dengan 1 kata atau frase " +
                                "yang sesuai",
                        "Ketik PETUNJUK untuk mendapatkan bantuan (namun kamu akan mendapat penalty 10 poin), " +
                                "ketik KELUAR untuk kembali ke menu utama, dan INSTRUKSI untuk melihat instruksi " +
                                "permainan. Selamat bermain!"));

        messages.add(lineMessageHelper.createSupportSticker());
        messages.add(lineMessageHelper.createTextMessage(quiz.getDescription()));
        messages.add(lineMessageHelper.createTextMessage(
                addSpaceBetweenChar(replaceCharWithUnderscore(quiz.getAnswer()))));

        return messages;
    }

    private List<Message> createQuizResponse(Quiz quiz) {
        return lineMessageHelper.createTextMessageList(
                Arrays.asList(quiz.getDescription(), addSpaceBetweenChar(replaceCharWithUnderscore(quiz.getAnswer()))));
    }

    private List<Message> createHintResponse(Hint hint) {
        if (HintType.AUDIO.equals(hint.getType())) {
            return lineMessageHelper.createAudioMessageList("Petunjuk #" + hint.getNumber() + "\n" +
                    hint.getDescription(), hint.getAudioUrl(), hint.getAudioDuration());
        }

        if (HintType.IMAGE.equals(hint.getType())) {
            return lineMessageHelper.createImageMessageList("Petunjuk #" + hint.getNumber() + "\n" +
                    hint.getDescription(), hint.getOriginalImageUrl(), hint.getPreviewImageUrl());
        }

        return lineMessageHelper.createTextMessageList(
                Arrays.asList("Petunjuk #" + hint.getNumber(), hint.getDescription()));
    }

    private List<Message> createQuizNotAvailableMessage() {
        return lineMessageHelper.createTextMessageList("Pertanyaan kuis untuk kategori ini sudah kamu jawab semua, " +
                "silakan pilih kategori yang lain");
    }

    private int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    private String addSpaceBetweenChar(String text) {
        return text.replace("", " ").trim();
    }

    private String replaceCharWithUnderscore(String text) {
        return text
                .replaceAll("\\s", "-")
                .replaceAll("\\w", "_");
    }

    private String normalizeAnswer(String answer) {
        return answer.replace(" ", "-")
                .toLowerCase(Locale.ENGLISH);
    }

}
