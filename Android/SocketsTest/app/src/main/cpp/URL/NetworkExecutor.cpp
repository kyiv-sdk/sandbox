//
// Created by Ivan Yurovych on 12/26/18.
//

#include "NetworkExecutor.h"

#include <android/log.h>

#include "HTTP_Client.h"
#include "HTTPS_Client.h"

NetworkExecutor::NetworkExecutor(NetworkExecutorAdapter *new_networkExecutorAdapter)
{
    networkExecutorAdapter = new_networkExecutorAdapter;
}

NetworkExecutor::~NetworkExecutor()
{
    try
    {
        if (myThread.joinable())
        {
            myThread.join();
        }
        delete networkExecutorAdapter;
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "~NetworkExecutor() called!");
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}

void NetworkExecutor::download(const char *t_protocol, const char *t_hostname, int t_port) {
    myThread = std::thread(&NetworkExecutor::run, this, t_protocol, t_hostname, t_port);
}

void NetworkExecutor::run(const char* protocol, const char* hostname, int port)
{
    std::string resultStr;

    HTTP_Client *m_networkClient;
    if (strcmp(protocol, "HTTP") == 0){
        m_networkClient = new HTTP_Client();
    } else {
        m_networkClient = new HTTPS_Client();
    }

    m_networkClient->loadData(resultStr, hostname, port);
    networkExecutorAdapter->runCallback(&resultStr);
    delete hostname;
    delete protocol;
}