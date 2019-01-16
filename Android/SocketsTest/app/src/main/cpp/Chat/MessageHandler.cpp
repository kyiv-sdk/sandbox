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
    connection->close_connection();
    needOneMoreLoop = false;
    cv.notify_all();
    try
    {
        if (senderThread.joinable())
        {
            senderThread.join();
            __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "senderThread.join();");
        }
        if (readerThread.joinable())
        {
            readerThread.join();
            __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "readerThread.join();");
        }
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
//    messagesToSend.push(message);
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s : %s", "cpp send", message);
//    cv.notify_all();
    connection->write(message);
}

void MessageHandler::senderFn()
{
    while (needOneMoreLoop){
        std::unique_lock<std::mutex> lck(mtx);
        cv.wait(lck);
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s : %s", "cpp senderFn", "notified");
        while (!messagesToSend.empty()){
            __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s : %s", "cpp senderFn managing with", messagesToSend.front()->c_str());
            messageHandlerAdapter->runCallback(messagesToSend.front());
            messagesToSend.pop();
        }
    }
}

void MessageHandler::readerFn() {
    while (needOneMoreLoop){
        std::string resultStr;
        connection->load(resultStr);
        if (!resultStr.empty()){
            std::unique_lock<std::mutex> lck(mtx);
            messagesToSend.push(&resultStr);
            __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s : %s", "readerFn", resultStr.c_str());
            __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s : %s", "cpp readerFn managing with", messagesToSend.front()->c_str());
            cv.notify_all();
        }
    }
}