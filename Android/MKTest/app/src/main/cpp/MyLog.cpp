//
// Created by Ivan Yurovych on 12/17/18.
//

#include "MyLog.h"
#include "main.h"
#include <string>

void MyLog::log(std::string logThis) {
    __android_log_print(ANDROID_LOG_DEBUG, "----------MyLog----------", ":%s", logThis.c_str());
    printf("----------MyLog----------%s", logThis.c_str());
}

const char* MyLog::btn1(const char *logThis){
    return logThis;
}

int MyLog::btn2(int* array, int length) {
    int sum = 0;
    for (int i = 0; i < length; i++){
        sum += array[i];
    }
    return sum;
}

int MyLog::btn3(int x, int y) {
    return x + y;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_mktest_MyLog_log(JNIEnv *env, jobject instance, jstring logThis_) {
    const char *logThis = env->GetStringUTFChars(logThis_, 0);

    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", logThis);

    env->ReleaseStringUTFChars(logThis_, logThis);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_iyuro_mktest_MyLog_btn1(JNIEnv *env, jobject instance, jstring logThis_) {
    const char *logThis = env->GetStringUTFChars(logThis_, 0);

    MyLog myLog;

    env->ReleaseStringUTFChars(logThis_, logThis);

    return env->NewStringUTF(myLog.btn1(logThis));
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_iyuro_mktest_MyLog_btn2(JNIEnv *env, jobject instance, jintArray array) {
    int len = (int) env->GetArrayLength(array);
    MyLog myLog;

    jint* body = env->GetIntArrayElements(array, 0);
    int* a = (int *) malloc(sizeof(int) * len);
    for (int i = 0; i < len; i++){
        a[i] = (int) body[i];
    }

    return myLog.btn2(a, len);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_iyuro_mktest_MyLog_btn3(JNIEnv *env, jobject instance, jint x, jint y) {

    MyLog myLog;
    return (jint) myLog.btn3((int)x, (int)y);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_mktest_MyLog_callJavaMethod(JNIEnv *env, jobject instance, jstring msgToShow) {

    jclass cls = env->GetObjectClass(instance);
    jmethodID mid = env->GetMethodID(cls, "showToast", "(Ljava/lang/String;)V");
    if (mid == 0) {
        return;
    }
    env->CallVoidMethod(instance, mid, msgToShow);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_mktest_MainActivity_callJavaMethod(JNIEnv *env, jobject instance,
                                                          jstring msgToShow) {
    jclass cls = env->GetObjectClass(instance);
    jmethodID mid = env->GetMethodID(cls, "showToast", "(Ljava/lang/String;)V");
    if (mid == 0) {
        return;
    }
    env->CallVoidMethod(instance, mid, msgToShow);
}