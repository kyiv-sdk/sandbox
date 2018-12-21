//
// Created by Ivan Yurovych on 12/19/18.
//

#include "main.h"

std::map<std::string, jclassReference> main::jclassesGlobalReferences;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_mktest_ThreadManager_init(JNIEnv *env, jobject instance) {
    std::string name = "com/example/iyuro/mktest/TestData";

    jclass jclazz = env->FindClass(name.c_str());
    jmethodID methodID = env->GetMethodID(jclazz, "<init>", "(I)V");
    jclassReference classReference(jclazz, methodID);
    main::jclassesGlobalReferences.insert(std::pair<std::string, jclassReference>(name, classReference));

    return;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_iyuro_mktest_MainActivity_helloJNI( JNIEnv* env,
                                                             jobject thiz)
{
return env->NewStringUTF("Hello from JNI !");
}

jclassReference main::getJclassReferenceByName(std::string jclassName){
    for (auto entity : main::jclassesGlobalReferences){
        if (entity.first == jclassName){
            return entity.second;
        }
    }
    throw "My: No such class";
}

//const std::map<std::string, jclassReference> &main::getJclassesGlobalReferences() {
//    return jclassesGlobalReferences;
//}
//
//void main::setJclassesGlobalReferences(
//        const std::map<std::string, jclassReference> &jclassesGlobalReferences) {
//    main::jclassesGlobalReferences = jclassesGlobalReferences;
//}

static JavaVM* gJvm = nullptr;
static jobject gClassLoader;
static jmethodID gFindClassMethod;
static JNIEnv *env;

static jclass MainActivityClassObject;
static jmethodID showToastmid;

JNIEnv* main::getEnv() {
//    JNIEnv *env;
    int status = gJvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if(status < 0) {
        status = gJvm->AttachCurrentThread(&env, NULL);
        if(status < 0) {
            return nullptr;
        }
    }
    return env;
}

jclass main::findClass(const char* name) {
    return static_cast<jclass>(main::getEnv()->CallObjectMethod(gClassLoader, gFindClassMethod, getEnv()->NewStringUTF(name)));
}

jclass main::getMainActivityClassObject() {
    return MainActivityClassObject;
}

jmethodID main::getShowToastmid() {
    return showToastmid;
}

JavaVM *main::getJVM() {
    return gJvm;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *pjvm, void *reserved) {
    gJvm = pjvm;  // cache the JavaVM pointer
    env = main::getEnv();
//    if (gJvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
//        return -1;
//    }
    //replace with one of your classes in the line below
    auto testDataClass = env->FindClass("com/example/iyuro/mktest/TestData");
    jclass jclazz = env->GetObjectClass(testDataClass);
    jmethodID jmethodID = env->GetMethodID(testDataClass, "<init>", "(I)V");

    main::jclassesGlobalReferences.insert(std::pair<std::string, jclassReference>("com/example/iyuro/mktest/TestData", jclassReference(testDataClass, jmethodID)));

//    auto MainActivityClass = env->FindClass("com/example/iyuro/mktest/MainActivity");
//    MainActivityClassObject = env->GetObjectClass(MainActivityClass);
//    showToastmid = env->GetMethodID(MainActivityClassObject, "showToast", "(Ljava/lang/String;)V");

    return JNI_VERSION_1_6;
}

