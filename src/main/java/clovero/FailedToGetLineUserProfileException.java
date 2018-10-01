package clovero;

public class FailedToGetLineUserProfileException extends CloverException {

    public FailedToGetLineUserProfileException(String lineId) {
        super("Failed to get Line user profile, line id = " + lineId);
    }

    @Override
    public String getErrorCode() {
        return "failed_to_get_line_user_profile";
    }
}
