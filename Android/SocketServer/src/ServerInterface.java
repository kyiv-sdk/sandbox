import java.util.ArrayList;

public interface ServerInterface {
    ArrayList<String> getLoggedUsers();
    int getLoggedUserHandlerPosition(String userUniqueId);
    void notifyAllUsersAboutNewcomer();
}
