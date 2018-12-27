//
// Created by Ivan Yurovych on 12/27/18.
//

#ifndef SOCKETSTEST_NETWORKEXECUTORADAPTER_H
#define SOCKETSTEST_NETWORKEXECUTORADAPTER_H


#include <jni.h>
#include <string>

class NetworkExecutorAdapter {
public:
    void (*JNIcallback)(jobject, std::string);
    jobject instance;
public:
    NetworkExecutorAdapter(jobject instance,
                           void (*callback)(jobject, std::string));
    virtual void runCallback(std::string) = 0;
};


#endif //SOCKETSTEST_NETWORKEXECUTORADAPTER_H
