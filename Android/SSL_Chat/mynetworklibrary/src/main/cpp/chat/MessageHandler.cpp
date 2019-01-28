//
// Created by Ivan Yurovych on 1/9/19.
//

#include "MessageHandler.h"

#include "../logger/Logger.h"
#include "../connection/SSL_Connection.h"

MessageHandler::MessageHandler(const char* protocol, const char *t_hostname, int t_port, bool t_isSSLEnabled, MessageHandlerAdapter *new_messageHandlerAdapter)
{
    Logger::getInstance()->log("MessageHandler() called!");
    mMessageHandlerAdapter = new_messageHandlerAdapter;

    m_hostname = t_hostname;
    m_port = t_port;
    this->mNeedOneMoreLoop = true;
    this->m_isSSLEnabled = t_isSSLEnabled;

    if (strcmp(protocol, "HTTP") == 0)
    {
        mProtocolType = HTTP;
    }
    else if (strcmp(protocol, "HTTPS") == 0)
    {
        mProtocolType = HTTPS;
    }
    else if (strcmp(protocol, "MyProtocol") == 0)
    {
        mProtocolType = MyProtocol;
    }

    Logger::getInstance()->log("MessageHandler() almost end!");

    mManagerThread = std::thread(&MessageHandler::managerFn, this);

    Logger::getInstance()->log("MessageHandler() end!");
}

MessageHandler::~MessageHandler()
{
    Logger::getInstance()->log("~MessageHandler() called!");
    mConnection->close_connection();
    mNeedOneMoreLoop = false;
    mCv.notify_all();
    try
    {
        if (mManagerThread.joinable())
        {
            mManagerThread.join();
            Logger::getInstance()->log("mManagerThread.join();");
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
    Logger::getInstance()->log("cpp send");
    Logger::getInstance()->log(s_message);

    RawMessage rawMessage(msgLen, 0, false, s_message);
    mMessagesToSend.push(rawMessage);
    mCv.notify_all();
}

void MessageHandler::managerFn()
{
    Logger::getInstance()->log("managerFn() started!");
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

    Logger::getInstance()->log("Manager created");

    mReaderThread = std::thread(&MessageHandler::readerFn, this);

    std::unique_lock<std::mutex> lck(mMtx);

    while (mNeedOneMoreLoop)
    {
        mCv.wait(lck);
        Logger::getInstance()->log("cpp Manager notified");

        while (!mMessagesToSend.empty())
        {
            bool fromServer = mMessagesToSend.front().isFromServer();
            std::string strToProcess = mMessagesToSend.front().getData();
            int headerLen = mMessagesToSend.front().getHeaderLen();
            int fileLen = mMessagesToSend.front().getFileLen();

            mMessagesToSend.pop();
            if (fromServer)
            {
                Logger::getInstance()->log("cpp Manager managing with");
                Logger::getInstance()->log(strToProcess);
                mMessageHandlerAdapter->runCallback(headerLen, fileLen, &strToProcess);
            } else {
                mConnection->write(strToProcess);
            }
        }
    }

    if (mReaderThread.joinable())
    {
        mReaderThread.join();
        Logger::getInstance()->log("readerThread.join();");
    }
}

void MessageHandler::readerFn()
{
    Logger::getInstance()->log("Reader created");
    std::string resultStr;

    while (mNeedOneMoreLoop) {
        Logger::getInstance()->log("reader: waiting for load...");
        int headerLen = 0, fileLen = 0;
        if (mProtocolType == MyProtocol)
        {
            headerLen = mConnection->readNum();
            fileLen = mConnection->readNum();
            mConnection->load(headerLen, fileLen, resultStr);
        }
        else
        {
            mConnection->load(resultStr);
            headerLen = resultStr.length();
        }
        RawMessage rawMessage(headerLen, fileLen, true, resultStr);

        Logger::getInstance()->log("reader: try to lock...");
        std::unique_lock<std::mutex> lck(mMtx);
        Logger::getInstance()->log("reader: locked!");
        mMessagesToSend.push(rawMessage);
        mCv.notify_all();
        Logger::getInstance()->log("readerFn");
        Logger::getInstance()->log(resultStr.c_str());

        if (resultStr.empty()){
            Logger::getInstance()->log("Reader broke");
            mNeedOneMoreLoop = false;
            break;
        }
    }
}

bool MessageHandler::isMNeedOneMoreLoop() const {
    return mNeedOneMoreLoop;
}
