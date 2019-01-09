//
// Created by Ivan Yurovych on 12/17/18.
//

#ifndef MKTEST_LOG_H
#define MKTEST_LOG_H

#include <cstdlib>
#include <jni.h>
#include <string>
#include <android/log.h>

class MyLog {
public:
    static void log(std::string logThis);
    const char* btn1(const char* logThis);
    int btn2(int array[], int length);
    int btn3(int x, int y);
};


#endif //MKTEST_LOG_H
