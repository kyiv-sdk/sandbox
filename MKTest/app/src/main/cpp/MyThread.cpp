//
// Created by Ivan Yurovych on 12/18/18.
//

#include "MyThread.h"

void MyThread::createNewTestData(JNIEnv *env, jclass type, int id, jstring str, JavaVM *javaVM){

    JNIEnv *new_env = nullptr;
    bool attached = true;
    if (javaVM->GetEnv(reinterpret_cast<void**>(&new_env), JNI_VERSION_1_6) == JNI_EDETACHED){
        attached = false;
        if (javaVM->AttachCurrentThread(&new_env, nullptr) ==JNI_OK){
            attached = true;
        }
    }

    if (attached) {
        jobject obj = TestData::getNewTestData(new_env, type, rand() % 10);
        MyLog myLog;
        if (obj != nullptr){
            std::string s = "Thread #" + std::to_string(id);
//        MyLog::showToast(s);
            myLog.log(s);
            TestData::deleteGlobalRef(new_env, obj);
        } else {
            std::string s = "Not created :(";
            myLog.log(s);
            obj = TestData::getNewTestData(env, type, rand() % 10);
        }

        javaVM->DetachCurrentThread();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_mktest_ThreadManager_newThread(JNIEnv *env, jclass type, jint id) {

    JavaVM *javaVM;
    env->GetJavaVM(&javaVM);

    jstring str = env->NewStringUTF("THREAD");
    std::thread thrd(MyThread::createNewTestData, env, type, (int) id, str, javaVM);
    thrd.join();
}