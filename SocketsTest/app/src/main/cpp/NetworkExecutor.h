//
// Created by Ivan Yurovych on 12/26/18.
//

#ifndef SOCKETSTEST_NETWORKEXECUTOR_H
#define SOCKETSTEST_NETWORKEXECUTOR_H

#include <string>
#include <linux/in.h>
#include <thread>

#include "NetworkExecutorAdapter.h"

class NetworkExecutor {
    int sock;
    struct sockaddr_in client;
    int PORT = 80;

    std::thread myThread;
    NetworkExecutorAdapter *networkExecutorAdapter;

    void run(const char* hostname);
    void loadData(std::string& result, const char* hostname);
public:
    NetworkExecutor(NetworkExecutorAdapter *networkExecutorAdapter);
    void start(const char* hostname);
    ~NetworkExecutor();
};

#endif //SOCKETSTEST_NETWORKEXECUTOR_H
