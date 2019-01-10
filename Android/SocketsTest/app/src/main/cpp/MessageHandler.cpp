//
// Created by Ivan Yurovych on 1/9/19.
//

#include "MessageHandler.h"

#include <android/log.h>

MessageHandler::MessageHandler(Basic_Connection *t_connection, MessageHandlerAdapter *new_messageHandlerAdapter)
{
    connection = t_connection;
    messageHandlerAdapter = new_messageHandlerAdapter;
}

MessageHandler::~MessageHandler()
{
    try
    {
        if (myThread.joinable())
        {
            myThread.join();
        }
        delete messageHandlerAdapter;
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "~MessageHandler() called!");
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}

void MessageHandler::send(const char* message) {
    myThread = std::thread(&MessageHandler::run, this, message);
}

void MessageHandler::run(const char* message)
{
    std::string resultStr;

    connection->write(message);

    connection->load(resultStr);

    messageHandlerAdapter->runCallback(&resultStr);

    delete message;
}