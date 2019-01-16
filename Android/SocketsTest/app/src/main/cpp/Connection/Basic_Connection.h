//
// Created by Ivan Yurovych on 1/9/19.
//

#ifndef SOCKETSTEST_CONNECTION_H
#define SOCKETSTEST_CONNECTION_H

#include <string>

class Basic_Connection
{
protected:
    int mSock;
public:
    Basic_Connection();
    virtual void open_connection(const char *hostname, int port);
    virtual void close_connection();
    virtual void load(std::string& resultStr);
    virtual void write(std::string request);
};


#endif //SOCKETSTEST_CONNECTION_H
