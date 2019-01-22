//
// Created by Ivan Yurovych on 1/9/19.
//

#include "MessageHandler.h"

#include <Logger.h>
#include <SSL_Connection.h>

MessageHandler::MessageHandler(const char *t_hostname, int t_port, bool t_isSSLEnabled, MessageHandlerAdapter *new_messageHandlerAdapter)
{
    Logger::log("NetworkHandler() called!");
    mMessageHandlerAdapter = new_messageHandlerAdapter;

    m_hostname = t_hostname;
    m_port = t_port;
    this->mNeedOneMoreLoop = true;
    this->m_isSSLEnabled = t_isSSLEnabled;

    mManagerThread = std::thread(&MessageHandler::managerFn, this);
}

MessageHandler::~MessageHandler()
{
    mConnection->close_connection();
    mNeedOneMoreLoop = false;
    mCv.notify_all();
    try
    {
        if (mManagerThread.joinable())
        {
            mManagerThread.join();
            Logger::log("mManagerThread.join();");
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

void MessageHandler::managerFn()
{
    if (m_isSSLEnabled){
        mConnection = new SSL_Connection();
    } else {
        mConnection = new Basic_Connection();
    }

    mConnection->open_connection(m_hostname, m_port);

    Logger::log("Manager created");

    mReaderThread = std::thread(&MessageHandler::readerFn, this);

    std::unique_lock<std::mutex> lck(mMtx);

    while (mNeedOneMoreLoop)
    {
        mCv.wait(lck);
        Logger::log("cpp Manager notified");

        while (!mMessagesToSend.empty())
        {
            bool fromServer = mMessagesToSend.front().first;
            std::string strToProcess = mMessagesToSend.front().second;
            mMessagesToSend.pop();
            if (fromServer)
            {
                Logger::log("cpp Manager managing with");
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
        Logger::log("reader: waiting for load...");
        mConnection->load(resultStr);
        if (!resultStr.empty())
        {
            Logger::log("reader: try to lock...");
            std::unique_lock<std::mutex> lck(mMtx);
            Logger::log("reader: locked!");
            mMessagesToSend.push(std::make_pair(true, resultStr));
            mCv.notify_all();
            Logger::log("readerFn");
            Logger::log(resultStr.c_str());
        }
    }
}