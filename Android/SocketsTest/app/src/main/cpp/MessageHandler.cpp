//
// Created by Ivan Yurovych on 1/9/19.
//

#include "MessageHandler.h"

#include <android/log.h>

MessageHandler::MessageHandler(const char *t_hostname, int t_port, MessageHandlerAdapter *new_messageHandlerAdapter)
{
    messageHandlerAdapter = new_messageHandlerAdapter;

    connection = new Basic_Connection();
    connection->open_connection(t_hostname, t_port);

    readerThread = std::thread(&MessageHandler::readerFn, this);
    senderThread = std::thread(&MessageHandler::senderFn, this);
}

MessageHandler::~MessageHandler()
{
    try
    {
        if (senderThread.joinable())
        {
            senderThread.join();
        }
        if (readerThread.joinable())
        {
            readerThread.join();
        }
        connection->close_connection();
        delete messageHandlerAdapter;
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "~MessageHandler() called!");
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}

void MessageHandler::send(const char* message) {
    messagesToSend.push(message);
}

void MessageHandler::senderFn()
{
    while (true){
        if (!messagesToSend.empty()){
            connection->write(messagesToSend.front());
            messagesToSend.pop();
        }
    }
}

void MessageHandler::readerFn() {
    std::string resultStr;

    while (true){
        connection->load(resultStr);
        if (!resultStr.empty()){
            messageHandlerAdapter->runCallback(&resultStr);
        }
    }
}