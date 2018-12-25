//
// Created by Ivan Yurovych on 12/18/18.
//

#include "MyThread.h"

struct targs{
    jobject type;
    jint id;
    jobject obj;
};

void checkPendingExceptions(JNIEnv *env, std::string s){
    jboolean flag = env->ExceptionCheck();
    if (flag) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", s.c_str());
    }
}

void *createNewTestDataPThread(void* params){
    struct targs *args;
    args = (struct targs*)params;

    main m;
    m.attachEnv();

    JNIEnv *new_env = m.getEnv();
    checkPendingExceptions(new_env, "1");

    jobject obj = TestData::getNewTestData(new_env, args->type, args->id);

    args->obj = obj;

    checkPendingExceptions(new_env, "2");

    m.detachMyThread();
    pthread_exit(NULL);
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_iyuro_mktest_ThreadManager_newThread(JNIEnv *env, jobject instance, jint id) {
    pthread_t tid;
    pthread_attr_t attr;

    pthread_attr_init(&attr);
    struct targs args;
    args.type = instance;
    args.id = id;
    args.obj = nullptr;
    checkPendingExceptions(env, "0");
    pthread_create(&tid, NULL, createNewTestDataPThread, (void*)&args);

    return args.obj;
}