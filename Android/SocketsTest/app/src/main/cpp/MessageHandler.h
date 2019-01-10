//
// Created by Ivan Yurovych on 1/9/19.
//

#ifndef SOCKETSTEST_MESSAGEHANDLER_H
#define SOCKETSTEST_MESSAGEHANDLER_H


#include <thread>
#include "MessageHandlerAdapter.h"
#include "Basic_Connection.h"

class MessageHandler {
    std::thread myThread;
    MessageHandlerAdapter *messageHandlerAdapter;
    Basic_Connection *connection;

    void run(const char* message);
public:
    MessageHandler(Basic_Connection *connection, MessageHandlerAdapter *new_messageHandlerAdapter);
    void send(const char* message);
    ~MessageHandler();
};


#endif //SOCKETSTEST_MESSAGEHANDLER_H
