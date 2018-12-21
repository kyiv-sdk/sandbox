//
// Created by Ivan Yurovych on 12/18/18.
//

#ifndef MKTEST_MYTHREAD_H
#define MKTEST_MYTHREAD_H

#include <jni.h>
#include <thread>
#include "TestData.h"
#include "MyLog.h"
#include <string>

class MyThread {
public:
    static void createNewTestData(JNIEnv *env, jobject type, jint id, jstring str);
};

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_mktest_ThreadManager_newThread(JNIEnv*, jobject, jint);

#endif //MKTEST_MYTHREAD_H
