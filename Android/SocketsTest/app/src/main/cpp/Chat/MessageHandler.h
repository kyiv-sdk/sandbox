//
// Created by Ivan Yurovych on 1/9/19.
//

#ifndef SOCKETSTEST_MESSAGEHANDLER_H
#define SOCKETSTEST_MESSAGEHANDLER_H


#include <thread>
#include <queue>

#include "MessageHandlerAdapter.h"
#include "Basic_Connection.h"

class MessageHandler
{
    std::thread mSenderThread, mReaderThread;
    MessageHandlerAdapter *mMessageHandlerAdapter;
    Basic_Connection *mConnection;

    void senderFn();

    void readerFn();

    std::queue<std::string> mMessagesToSend;

    bool mNeedOneMoreLoop;

    std::mutex mMtx;
    std::condition_variable mCv;
public:
    MessageHandler(const char *hostname, int port, MessageHandlerAdapter *new_messageHandlerAdapter);
    void send(const char* message);
    ~MessageHandler();
};


#endif //SOCKETSTEST_MESSAGEHANDLER_H
