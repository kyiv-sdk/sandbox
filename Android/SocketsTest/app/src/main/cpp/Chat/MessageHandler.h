//
// Created by Ivan Yurovych on 1/9/19.
//

#ifndef SOCKETSTEST_MESSAGEHANDLER_H
#define SOCKETSTEST_MESSAGEHANDLER_H


#include <thread>
#include <queue>
#include "MessageHandlerAdapter.h"
#include "../Connection/Basic_Connection.h"

class MessageHandler {
    std::thread senderThread, readerThread;
    MessageHandlerAdapter *messageHandlerAdapter;
    Basic_Connection *connection;

    void senderFn();

    void readerFn();

    std::queue<std::string *> messagesToSend;

    bool needOneMoreLoop;

    std::mutex mtx;
    std::condition_variable cv;
public:
    MessageHandler(const char *hostname, int port, MessageHandlerAdapter *new_messageHandlerAdapter);
    void send(const char* message);
    ~MessageHandler();
};


#endif //SOCKETSTEST_MESSAGEHANDLER_H
