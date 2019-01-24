//
// Created by Ivan Yurovych on 1/16/19.
//

#ifndef SOCKETSTEST_LOGGER_H
#define SOCKETSTEST_LOGGER_H

#include <string>

class Logger
{
private:
    Logger(){}
public:
    static void log(const char* logThis);
    static void log(std::string logThis);
};


#endif //SOCKETSTEST_LOGGER_H
