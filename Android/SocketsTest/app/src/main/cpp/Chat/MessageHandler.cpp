//
// Created by Ivan Yurovych on 1/9/19.
//

#include "MessageHandler.h"

#include <Logger.h>

MessageHandler::MessageHandler(const char *t_hostname, int t_port, MessageHandlerAdapter *new_messageHandlerAdapter)
{
    Logger::log("NetworkHandler() called!");
    mMessageHandlerAdapter = new_messageHandlerAdapter;

    mConnection = new Basic_Connection();
    mConnection->open_connection(t_hostname, t_port);

    this->mNeedOneMoreLoop = true;

    mReaderThread = std::thread(&MessageHandler::readerFn, this);
    mSenderThread = std::thread(&MessageHandler::senderFn, this);
}

MessageHandler::~MessageHandler()
{
    mConnection->close_connection();
    mNeedOneMoreLoop = false;
    mCv.notify_all();
    try
    {
        if (mSenderThread.joinable())
        {
            mSenderThread.join();
            Logger::log("senderThread.join();");
        }
        if (mReaderThread.joinable())
        {
            mReaderThread.join();
            Logger::log("readerThread.join();");
        }
        delete mMessageHandlerAdapter;
        Logger::log("~NetworkHandler() called!");
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}

void MessageHandler::send(const char* message)
{
    Logger::log("cpp send");
    Logger::log(message);
    mConnection->write(message);
}

void MessageHandler::senderFn()
{
    while (mNeedOneMoreLoop)
    {
        std::unique_lock<std::mutex> lck(mMtx);
        mCv.wait(lck);
        Logger::log("cpp senderFn notified");

        while (!mMessagesToSend.empty())
        {
            std::string strToProcess = mMessagesToSend.front().c_str();
            mMessagesToSend.pop();
            Logger::log("cpp senderFn managing with");
            Logger::log(strToProcess);
            mMessageHandlerAdapter->runCallback(&strToProcess);
        }
    }
}

void MessageHandler::readerFn()
{
    std::string resultStr;

    while (mNeedOneMoreLoop)
    {
        mConnection->load(resultStr);
        if (!resultStr.empty())
        {
            std::unique_lock<std::mutex> lck(mMtx);
            mMessagesToSend.push(resultStr);
            mCv.notify_all();
            Logger::log("readerFn");
            Logger::log(resultStr.c_str());
        }
    }
}