//
// Created by Ivan Yurovych on 1/9/19.
//

#ifndef SOCKETSTEST_MESSAGEHANDLERADAPTER_H
#define SOCKETSTEST_MESSAGEHANDLERADAPTER_H

#include <string>

class MessageHandlerAdapter
{
public:
    virtual void runCallback(std::string* ) = 0;
    virtual ~MessageHandlerAdapter();
};


#endif //SOCKETSTEST_MESSAGEHANDLERADAPTER_H
