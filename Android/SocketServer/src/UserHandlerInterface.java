public interface UserHandlerInterface {
    void onLoginSuccess(String response, String userName);
    void onLoginFailed(String response, String userName);
    void onResponse(String dstId, String msg);
    void onConnectionClose();
    String getUserName();
    String getUserUniqueID();
    boolean isUsernameValid(String username);
}
