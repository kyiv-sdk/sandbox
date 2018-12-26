//
// Created by Ivan Yurovych on 12/26/18.
//

#ifndef SOCKETSTEST_NETWORKEXECUTOR_H
#define SOCKETSTEST_NETWORKEXECUTOR_H

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

class NetworkExecutor {
    int sock;
    struct sockaddr_in client;
    int PORT = 80;
public:
    std::string loadData(std::string hostname);
};

#endif //SOCKETSTEST_NETWORKEXECUTOR_H
