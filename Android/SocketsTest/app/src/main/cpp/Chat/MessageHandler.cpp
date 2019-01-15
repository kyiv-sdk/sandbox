//
// Created by Ivan Yurovych on 1/9/19.
//

#include "MessageHandler.h"

#include <android/log.h>

MessageHandler::MessageHandler(const char *t_hostname, int t_port, MessageHandlerAdapter *new_messageHandlerAdapter)
{
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "NetworkHandler() called!");
    messageHandlerAdapter = new_messageHandlerAdapter;

    connection = new Basic_Connection();
    connection->open_connection(t_hostname, t_port);

    this->needOneMoreLoop = true;

    readerThread = std::thread(&MessageHandler::readerFn, this);
    senderThread = std::thread(&MessageHandler::senderFn, this);
}

MessageHandler::~MessageHandler()
{
    needOneMoreLoop = false;
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
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "~NetworkHandler() called!");
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}

void MessageHandler::send(const char* message) {
//    std::unique_lock<std::mutex> lck(mtx);
    messagesToSend.push(message);
    cv.notify_all();
}

void MessageHandler::senderFn()
{
    std::unique_lock<std::mutex> lck(mtx);
    while (needOneMoreLoop){
        cv.wait(lck);
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "just trash");
        if (!messagesToSend.empty()){
            connection->write(messagesToSend.front());
            messagesToSend.pop();
        }
    }
}

void MessageHandler::readerFn() {
    std::string resultStr;

    while (needOneMoreLoop){
        connection->load(resultStr);
        if (!resultStr.empty()){
            messageHandlerAdapter->runCallback(&resultStr);
        }
    }
}