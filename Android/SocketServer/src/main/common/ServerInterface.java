package main.common;

import java.util.ArrayList;

public interface ServerInterface {
    ArrayList<String> getLoggedUsers();
    int getLoggedUserHandlerPosition(String userUniqueId);
    void notifyAllUsersChanges();
    void notifyAllUsersExceptOne(String exceptThis);
    void onUserHandlerClose(String uniqueID);
}
