package clovero.user;

import clovero.FailedToGetLineUserProfileException;
import clovero.LineConfiguration;
import clovero.cache.Cache;
import clovero.cache.CacheService;
import clovero.category.Category;
import clovero.logger.CloverLogger;
import clovero.logger.CloverLoggerFactory;
import clovero.quiz.Quiz;
import clovero.state.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.profile.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {

    static String USER_PROFILE_CACHE_NAME = "LineUserProfile";
    static CloverLogger logger = CloverLoggerFactory.getCloverLogger(UserService.class);

    UserRepository userRepository;
    LineConfiguration lineConfiguration;
    CacheService cacheService;
    ObjectMapper objectMapper;
    Environment environment;

    public Optional<User> findByLineId(String lineId) {
        return userRepository.findByLineId(lineId);
    }

    public User getOrCreateByLineId(String lineId) {
        final Optional<User> userOptional = findByLineId(lineId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        return createUserByLineId(lineId);
    }

    public List<User> getTop10User() {
        return userRepository.findTop10ByOrderByPointDesc();
    }

    public User createUserByLineId(String lineId) {
        final String displayName;

        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            try {
                displayName = getUserProfile(lineConfiguration.getChannelToken(), lineId).getDisplayName();
            } catch (IOException e) {
                throw new FailedToGetLineUserProfileException(lineId);
            }
        } else {
            displayName = "a name";
        }

        final User user = User.builder()
                .lineId(lineId)
                .name(displayName)
                .state(State.MAIN_MENU)
                .point(0L)
                .currentQuizWrongAnswerCount(0)
                .currentHintNumber(0)
                .currentRoundTotalQuizSolved(0)
                .currentRoundPoint(0L)
                .currentRoundMaxPoint(0L)
                .build();

        return userRepository.save(user);
    }

    public void updateState(User user, State state) {
        user.setState(state);
        userRepository.save(user);
    }

    public void updateCurrentCategory(User user, Category category) {
        user.setCurrentCategory(category);
        userRepository.save(user);
    }

    public void updateCurrentQuiz(User user, Quiz quiz) {
        user.setCurrentQuiz(quiz);
        userRepository.save(user);
    }

    public void updateCurrentQuizRemainingPoint(User user, Long point) {
        user.setCurrentQuizRemainingPoint(point);
        userRepository.save(user);
    }

    public void updateUserCurrentAnswer(User user, String answer) {
        user.setUserCurrentAnswer(answer);
        userRepository.save(user);
    }

    public void updateUserCurrentAnswer(User user, Character foundedChar) {
        final List<Integer> foundedCharIndexList = new ArrayList<>();
        final String quizAnswer = user.getCurrentQuiz().getAnswer();

        int index = quizAnswer.indexOf(foundedChar);
        while (index >= 0) {
            foundedCharIndexList.add(index);
            index = quizAnswer.indexOf(foundedChar, index + 1);
        }

        final char[] chars = user.getUserCurrentAnswer().toCharArray();
        foundedCharIndexList.forEach(foundedCharIndex -> chars[foundedCharIndex] = foundedChar);

        user.setUserCurrentAnswer(new String(chars));
        userRepository.save(user);
    }

    public void resetUserQuizData(User user) {
        user.setState(State.MAIN_MENU);
        user.setCurrentCategory(null);
        user.setCurrentQuiz(null);
        user.setCurrentHintNumber(0);
        user.setUserCurrentAnswer("");
        user.setCurrentQuizWrongAnswerCount(0);
        userRepository.save(user);
    }

    public void resetRoundData(User user) {
        user.setCurrentRoundMaxPoint(0L);
        user.setCurrentRoundPoint(0L);
        user.setCurrentRoundTotalQuizSolved(0);
        userRepository.save(user);
    }

    public void incrementRoundData(User user, Long userLastQuizPoint, Long lastQuizMaxPoint) {
        user.setCurrentRoundMaxPoint(user.getCurrentRoundMaxPoint() + lastQuizMaxPoint);
        user.setCurrentRoundPoint(user.getCurrentRoundPoint() + userLastQuizPoint);
        user.setCurrentRoundTotalQuizSolved(user.getCurrentRoundTotalQuizSolved() + 1);
        userRepository.save(user);
    }

    public void incrementPoint(User user, Long point) {
        if (point <= 0) {
            return;
        }
        user.setPoint(user.getPoint() + point);
        userRepository.save(user);
    }

    public void decrementPoint(User user, Long point) {
        if (user.getPoint() <= 0) {
            user.setPoint(0L);
        } else {
            user.setPoint(user.getPoint() - point);
        }
        userRepository.save(user);
    }

    public void incrementCurrentHintNumber(User user) {
        user.setCurrentHintNumber(user.getCurrentHintNumber() + 1);
        userRepository.save(user);
    }

    public void incrementCurrentQuizWrongAnswerCount(User user) {
        user.setCurrentQuizWrongAnswerCount(user.getCurrentQuizWrongAnswerCount() + 1);
        userRepository.save(user);
    }

    public void decrementCurrentQuizRemainingPoint(User user, Long point) {
        user.setCurrentQuizRemainingPoint(user.getCurrentQuizRemainingPoint() - point);
        userRepository.save(user);
    }

    private UserProfileResponse getUserProfile(String channelAccessToken, String lineId) throws IOException {
        final Optional<Cache> cache = cacheService.get(USER_PROFILE_CACHE_NAME + "-" + lineId);
        if (cache.isPresent()) {
            try {
                return objectMapper.readValue(cache.get().getValue(), UserProfileResponse.class);
            } catch (IOException exc) {
                logger.info("Failed to retrieve line user profile from cache");
            }
        }

        final Response<UserProfileResponse> response = LineMessagingServiceBuilder
                .create(channelAccessToken)
                .build()
                .getProfile(lineId)
                .execute();

        if (!response.isSuccessful()) {
            throw new FailedToGetLineUserProfileException(lineId);
        }

        final UserProfileResponse userProfile = response.body();
        cacheService.put(USER_PROFILE_CACHE_NAME + "-" + lineId, userProfile);
        return userProfile;
    }
}
