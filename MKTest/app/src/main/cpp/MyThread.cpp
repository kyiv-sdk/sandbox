//
// Created by Ivan Yurovych on 12/18/18.
//

#include "MyThread.h"

void MyThread::createNewTestData(JNIEnv *env, jobject type, jint id, jstring str){
    main m;
    m.attachEnv();
    JNIEnv *new_env = m.getEnv();
    jclass jclazz = new_env->FindClass("com/example/iyuro/mktest/TestData");
//    bool attached = true;
//    if (javaVM->GetEnv(reinterpret_cast<void**>(&new_env), JNI_VERSION_1_6) == JNI_EDETACHED){
//        attached = false;
//        if (javaVM->AttachCurrentThread(&new_env, nullptr) == JNI_OK){
//            attached = true;
//        }
//    }
//    if (attached) {
        jobject obj = TestData::getNewTestData(new_env, type, id);
//        MyLog myLog;
//        if (obj != nullptr){
//            std::string s = "Thread #" + std::to_string(id);
////        MyLog::showToast(s);
//            myLog.log(s);
//            TestData::deleteGlobalRef(new_env, obj);
//        } else {
//            std::string s = "Not created :(";
//            myLog.log(s);
//            obj = TestData::getNewTestData(env, type, rand() % 10);
//        }

//        javaVM->DetachCurrentThread();
//    }
        m.detachMyThread();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_mktest_ThreadManager_newThread(JNIEnv *env, jobject type, jint id) {

//    JavaVM *javaVM;
//    env->GetJavaVM(&javaVM);
//    JavaVM *javaVM = main::getJVM();

    jstring str = env->NewStringUTF("THREAD");
    std::thread thrd(MyThread::createNewTestData, env, type, id, str);
    thrd.join();
}