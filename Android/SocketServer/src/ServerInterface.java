import java.util.ArrayList;

public interface ServerInterface {
    ArrayList<String> getLoggedUsersRequest();
    int getLoggedUserHandlerPosition(String userUniqueId);
}
