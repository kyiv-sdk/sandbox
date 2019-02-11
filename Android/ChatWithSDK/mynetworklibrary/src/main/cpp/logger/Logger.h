//
// Created by Ivan Yurovych on 1/16/19.
//

#ifndef SOCKETSTEST_LOGGER_H
#define SOCKETSTEST_LOGGER_H

#include <string>
#include <mutex>

class Logger
{
private:
    static Logger *instance;
    static std::mutex mMutex;

    Logger() = default;
    ~Logger()= default;

public:
    static Logger* getInstance();

    void log(const char* logThis);
    void log(std::string logThis);
};


#endif //SOCKETSTEST_LOGGER_H
