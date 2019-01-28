//
// Created by Ivan Yurovych on 12/26/18.
//

#include "NetworkExecutor.h"

#include <Logger.h>
#include "HTTP_Client.h"
#include "HTTPS_Client.h"

NetworkExecutor::NetworkExecutor(NetworkExecutorAdapter *new_networkExecutorAdapter)
{
    mNetworkExecutorAdapter = new_networkExecutorAdapter;
}

NetworkExecutor::~NetworkExecutor()
{
    try
    {
        if (mThread.joinable())
        {
            mThread.join();
        }
        delete mNetworkExecutorAdapter;
        Logger::getInstance()->log("~NetworkExecutor() called!");
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}

void NetworkExecutor::download(const char *t_protocol, const char *t_hostname, int t_port)
{
    mThread = std::thread(&NetworkExecutor::run, this, t_protocol, t_hostname, t_port);
}

void NetworkExecutor::run(const char* protocol, const char* hostname, int port)
{
    std::string resultStr;

    HTTP_Client *m_networkClient;
    if (strcmp(protocol, "HTTP") == 0)
    {
        m_networkClient = new HTTP_Client();
    } else {
        m_networkClient = new HTTPS_Client();
    }

    m_networkClient->loadData(resultStr, hostname, port);
    delete hostname;
    delete protocol;
    mNetworkExecutorAdapter->runCallback(&resultStr);
}