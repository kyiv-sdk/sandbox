//
// Created by Ivan Yurovych on 1/9/19.
//

#include "MessageHandler.h"

#include "../logger/Logger.h"
#include "../connection/SSL_Connection.h"

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
    Logger::log("~NetworkHandler() called!");
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
        delete mMessageHandlerAdapter;
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}

void MessageHandler::send(int len, const char* message)
{
    std::unique_lock<std::mutex> lck(mMtx);
    std::string s_message = std::string(message, len);
    int msgLen = s_message.length();
    Logger::log("cpp send");
    Logger::log(s_message);

    RawMessage rawMessage(msgLen, 0, false, s_message);
    mMessagesToSend.push(rawMessage);
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

    if (!mConnection->isConnected())
    {
        mNeedOneMoreLoop = false;
    }

    Logger::log("Manager created");

    mReaderThread = std::thread(&MessageHandler::readerFn, this);

    std::unique_lock<std::mutex> lck(mMtx);

    while (mNeedOneMoreLoop)
    {
        mCv.wait(lck);
        Logger::log("cpp Manager notified");

        while (!mMessagesToSend.empty())
        {
            bool fromServer = mMessagesToSend.front().isFromServer();
            std::string strToProcess = mMessagesToSend.front().getData();
            int headerLen = mMessagesToSend.front().getHeaderLen();
            int fileLen = mMessagesToSend.front().getFileLen();

            mMessagesToSend.pop();
            if (fromServer)
            {
                Logger::log("cpp Manager managing with");
                Logger::log(strToProcess);
                mMessageHandlerAdapter->runCallback(headerLen, fileLen, &strToProcess);
            } else {
                mConnection->write(strToProcess);
            }
        }
    }

    if (mReaderThread.joinable())
    {
        mReaderThread.join();
        Logger::log("readerThread.join();");
    }
}

void MessageHandler::readerFn()
{
    Logger::log("Reader created");
    std::string resultStr;

    while (mNeedOneMoreLoop)
    {
        Logger::log("reader: waiting for load...");
        int headerLen = 0, fileLen = 0;
        mConnection->load(headerLen, fileLen, resultStr);
        RawMessage rawMessage(headerLen, fileLen, true, resultStr);

        Logger::log("reader: try to lock...");
        std::unique_lock<std::mutex> lck(mMtx);
        Logger::log("reader: locked!");
        mMessagesToSend.push(rawMessage);
        mCv.notify_all();
        Logger::log("readerFn");
        Logger::log(resultStr.c_str());

        if (resultStr.empty()){
            Logger::log("Reader broke");
            mNeedOneMoreLoop = false;
            break;
        }
    }
}

bool MessageHandler::isMNeedOneMoreLoop() const {
    return mNeedOneMoreLoop;
}
