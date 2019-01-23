package main;

public interface UserHandlerInterface {
    void onLoginSuccess(byte[] bytesData, String userName);
    void onLoginFailed(byte[] bytesData, String userName);
    void onResponse(String dstId, byte[] bytesData);
    void onConnectionClose();
    String getUserName();
    String getUserUniqueID();
    boolean isUsernameValid(String username);
    void setUniqueID(String uniqueID);
}
