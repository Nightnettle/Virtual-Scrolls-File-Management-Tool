package vsas.utils;

/**
 * Singleton class for maintaining a globally
 * accessible user session
 */
public class UserSession {
    private static UserSession instance;

    private String userId;

    private boolean isGuest = false;

    private UserSession() {

    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getIsGuest() {
        return isGuest;
    }

    public void setIsGuest(boolean isGuest) {
        this.isGuest = isGuest;
    }
}
