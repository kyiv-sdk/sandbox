//
// Created by Ivan Yurovych on 12/26/18.
//

#ifndef SOCKETSTEST_NETWORKER_H
#define SOCKETSTEST_NETWORKER_H

#include <jni.h>

class NetworkManager {
private:
    jclass instance;
public:
    static void makeRequest(std::string hostname, jobject instance, void (* callback)(jobject instance, std::string resultData));
    static void jni_sendToJava(jobject instance, std::string result);
    std::thread myThread;
    ~NetworkManager();
};


#endif //SOCKETSTEST_NETWORKER_H
