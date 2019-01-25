//
// Created by Ivan Yurovych on 1/9/19.
//

#include "MessageHandler.h"

#include <Logger.h>

MessageHandler::MessageHandler(const char *t_hostname, int t_port, MessageHandlerAdapter *new_messageHandlerAdapter)
{
    Logger::log("NetworkHandler() called!");
    mMessageHandlerAdapter = new_messageHandlerAdapter;

    m_hostname = t_hostname;
    m_port = t_port;
    this->mNeedOneMoreLoop = true;

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
    std::unique_lock<std::mutex> lck(mMtx);
    mMessagesToSend.push(std::make_pair(false, message));
    mCv.notify_all();
}

void MessageHandler::senderFn()
{
    mConnection = new Basic_Connection();
    mConnection->open_connection(m_hostname, m_port);

    Logger::log("Sender created");

    mReaderThread = std::thread(&MessageHandler::readerFn, this);

    while (mNeedOneMoreLoop)
    {
        std::unique_lock<std::mutex> lck(mMtx);
        mCv.wait(lck);
        Logger::log("cpp senderFn notified");

        while (!mMessagesToSend.empty())
        {
            bool fromServer = mMessagesToSend.front().first;
            std::string strToProcess = mMessagesToSend.front().second;
            mMessagesToSend.pop();
            if (fromServer)
            {
                Logger::log("cpp senderFn managing with");
                Logger::log(strToProcess);
                mMessageHandlerAdapter->runCallback(&strToProcess);
            } else {
                mConnection->write(strToProcess);
            }
        }
    }
}

void MessageHandler::readerFn()
{
    Logger::log("Reader created");
    std::string resultStr;

    while (mNeedOneMoreLoop)
    {
        mConnection->load(resultStr);
        if (!resultStr.empty())
        {
            std::unique_lock<std::mutex> lck(mMtx);
            mMessagesToSend.push(std::make_pair(true, resultStr));
            mCv.notify_all();
            Logger::log("readerFn");
            Logger::log(resultStr.c_str());
        }
    }
}