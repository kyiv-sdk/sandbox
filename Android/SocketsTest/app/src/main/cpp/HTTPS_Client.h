//
// Created by Ivan Yurovych on 1/3/19.
//

#ifndef SOCKETSTEST_SSLCLIENT_H
#define SOCKETSTEST_SSLCLIENT_H

#include "HTTP_Client.h"

class HTTPS_Client : public HTTP_Client{
public:
    void loadData(std::string &result, const char *hostname, int port) override;
};


#endif //SOCKETSTEST_SSLCLIENT_H
