//
// Created by Ivan Yurovych on 1/9/19.
//

#ifndef SOCKETSTEST_MESSAGEHANDLER_H
#define SOCKETSTEST_MESSAGEHANDLER_H


#include <thread>
#include <queue>

#include "MessageHandlerAdapter.h"
#include "Basic_Connection.h"
#include "RawMessage.h"

class MessageHandler
{
    std::thread mManagerThread, mReaderThread;
    MessageHandlerAdapter *mMessageHandlerAdapter;
    Basic_Connection *mConnection;
    const char *m_hostname;
    int m_port;
    bool m_isSSLEnabled;

    void managerFn();

    void readerFn();

    std::queue<RawMessage> mMessagesToSend; // bool = true if msg from server, else = false

    bool mNeedOneMoreLoop;

    std::mutex mMtx;
    std::condition_variable mCv;
public:
    MessageHandler(const char *t_hostname, int t_port, bool isSSLEnabled, MessageHandlerAdapter *new_messageHandlerAdapter);
    void send(int len, const char* message);
    ~MessageHandler();
};


#endif //SOCKETSTEST_MESSAGEHANDLER_H
