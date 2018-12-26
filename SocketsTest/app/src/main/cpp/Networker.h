//
// Created by Ivan Yurovych on 12/26/18.
//

#ifndef SOCKETSTEST_NETWORKER_H
#define SOCKETSTEST_NETWORKER_H

#include <jni.h>

class Networker {
public:
    static void makeRequest(std::string hostname, jobject instance);
    std::thread myThread;
};


#endif //SOCKETSTEST_NETWORKER_H
