//
// Created by Ivan Yurovych on 1/4/19.
//

#ifndef SOCKETSTEST_HTTPCLIENT_H
#define SOCKETSTEST_HTTPCLIENT_H

#include <string>

#include "NetworkExecutorAdapter.h"

class HTTP_Client {
public:
    virtual void loadData(std::string &result, const char *hostname, int port);
};


#endif //SOCKETSTEST_HTTPCLIENT_H
