//
// Created by Ivan Yurovych on 1/16/19.
//

#include "Logger.h"

#include <android/log.h>

Logger* Logger::instance= nullptr;
std::mutex Logger::mMutex;

Logger* Logger::getInstance(){
    if ( !instance ){
        std::lock_guard<std::mutex> myLock(mMutex);
        if ( !instance ){
            instance= new Logger();
        }
    }
    return instance;
}

void Logger::log(const char* logThis)
{
    std::lock_guard<std::mutex> myLock(mMutex);
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", logThis);
}

void Logger::log(std::string logThis)
{
    std::lock_guard<std::mutex> myLock(mMutex);
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", logThis.c_str());
}
