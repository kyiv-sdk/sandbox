//
// Created by Ivan Yurovych on 2/12/19.
//

#ifndef CHATWITHSDK_DYNAMICS_CONNECTION_H
#define CHATWITHSDK_DYNAMICS_CONNECTION_H


#include "Basic_Connection.h"

class Dynamics_Connection : public Basic_Connection{
public:
    Dynamics_Connection();

    void open_connection(const char *hostname, int port) override;
    void close_connection() override;
    void load(std::string &resultStr) override;
    void load(int &headerLen, int &fileLen, std::string &resultStr) override;
    void write(std::string request) override;
    int readNum() override;
};


#endif //CHATWITHSDK_DYNAMICS_CONNECTION_H
