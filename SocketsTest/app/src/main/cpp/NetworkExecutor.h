//
// Created by Ivan Yurovych on 12/26/18.
//

#ifndef SOCKETSTEST_NETWORKEXECUTOR_H
#define SOCKETSTEST_NETWORKEXECUTOR_H

#include <thread>

#include "NetworkExecutorAdapter.h"

class NetworkExecutor {
    std::thread myThread;
    NetworkExecutorAdapter *networkExecutorAdapter;

    void run(const char* protocol, const char* hostname, int port);
public:
    NetworkExecutor(NetworkExecutorAdapter *networkExecutorAdapter);
    void download(const char* protocol, const char* hostname, int port);
    ~NetworkExecutor();
};

#endif //SOCKETSTEST_NETWORKEXECUTOR_H
