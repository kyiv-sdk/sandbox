//
// Created by Ivan Yurovych on 12/26/18.
//

#ifndef SOCKETSTEST_NETWORKER_H
#define SOCKETSTEST_NETWORKER_H

#include <jni.h>
#include <string>
#include <netdb.h>
#include <linux/in.h>
#include <endian.h>
#include <zconf.h>
#include <sstream>
#include <android/log.h>
#include <android/looper.h>
#include <unistd.h>
#include "Main.h"

int sock;
struct sockaddr_in client;
int PORT = 80;

struct targs{
    std::string hostname;
    std::string out;
    jobject instance;
};

class Networker {

};


#endif //SOCKETSTEST_NETWORKER_H
