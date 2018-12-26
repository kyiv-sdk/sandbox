//
// Created by Ivan Yurovych on 12/19/18.
//

#ifndef MKTEST_MAIN_H
#define MKTEST_MAIN_H

#include <jni.h>
#include <vector>
#include <map>
#include <string>
#include <utility>
#include "JclassReference.h"

class main {
public:
    main();
    static jmethodID NetworkSingletonOnSuccessMethodId;

    JNIEnv *getEnv();
    JNIEnv *attachEnv();
    void detachMyThread();
    static JavaVM* getJVM();
private:
    JNIEnv *env;
};


#endif //MKTEST_MAIN_H
