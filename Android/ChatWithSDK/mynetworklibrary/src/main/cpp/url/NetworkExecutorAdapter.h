//
// Created by Ivan Yurovych on 12/27/18.
//

#ifndef SOCKETSTEST_NETWORKEXECUTORADAPTER_H
#define SOCKETSTEST_NETWORKEXECUTORADAPTER_H

#include <string>

class NetworkExecutorAdapter
{
public:
    virtual void runCallback(std::string* ) = 0;
    virtual ~NetworkExecutorAdapter();
};


#endif //SOCKETSTEST_NETWORKEXECUTORADAPTER_H
