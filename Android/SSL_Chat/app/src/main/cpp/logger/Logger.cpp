//
// Created by Ivan Yurovych on 1/16/19.
//

#include "Logger.h"

#include <android/log.h>

void Logger::log(const char* logThis)
{
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", logThis);
}

void Logger::log(std::string logThis)
{
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", logThis.c_str());
}
