//
// Created by Ivan Yurovych on 12/26/18.
//

#include "NetworkExecutor.h"

std::string NetworkExecutor::loadData(std::string hostname)
{
    struct hostent *host = gethostbyname(hostname.c_str());

    if ( (host == NULL) || (host->h_addr == NULL) ) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Error retrieving DNS information.");
        exit(1);
    }

    bzero(&client, sizeof(client));
    client.sin_family = AF_INET;
    client.sin_port = htons( PORT );
    memcpy(&client.sin_addr, host->h_addr, (size_t)host->h_length);

    sock = socket(AF_INET, SOCK_STREAM, 0);

    if (sock < 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Error creating socket.");
        exit(1);
    }

    if (connect(sock, (struct sockaddr *)&client, sizeof(client)) < 0 ) {
        close(sock);
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Could not connect");
        exit(1);
    }

    std::stringstream ss;

    ss << "GET / HTTP/1.1\r\nHost: "<< hostname.c_str() << "\r\nConnection: close\r\n\r\n";
    std::string request = ss.str();

    if (send(sock, request.c_str(), request.length(), 0) != (int)request.length()) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Error sending request.");
        exit(1);
    }

    std::string resultStr;
    char cur;
    while ( read(sock, &cur, 1) > 0 ) {
        resultStr += cur;
    }

    return resultStr;
}

void NetworkExecutor::run(std::string url, NetworkExecutorAdapter *networkExecutorAdapter) {
    std::string resultData = loadData(url);
    networkExecutorAdapter->runCallback(resultData);
}

void NetworkExecutor::start(std::string url, NetworkExecutorAdapter *networkExecutorAdapter) {
    myThread = std::thread(&NetworkExecutor::run, this, url, networkExecutorAdapter);
}

NetworkExecutor::~NetworkExecutor() {
    try
    {
        if (myThread.joinable())
        {
            myThread.join();
        }
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}
