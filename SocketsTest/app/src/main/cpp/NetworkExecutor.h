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
#include <thread>
#include "Main.h"
#include "NetworkExecutorAdapter.h"

class NetworkExecutor {
    int sock;
    struct sockaddr_in client;
    int PORT = 80;

    std::thread myThread;
public:
    void run(std::string url, NetworkExecutorAdapter *networkExecutorAdapter);
    std::string loadData(std::string url);
    void start(std::string url, NetworkExecutorAdapter *networkExecutorAdapter);
    ~NetworkExecutor();
};

#endif //SOCKETSTEST_NETWORKEXECUTOR_H
