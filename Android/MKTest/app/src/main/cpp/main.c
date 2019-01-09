//
// Created by Ivan Yurovych on 12/19/18.
//

//#include "main.h"
//
//std::map<std::string, jclass> main::jclassesGlobalReferences;
//
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_iyuro_mktest_ThreadManager_init(JNIEnv *env, jobject instance) {
////    jclass jclass = env->
//std::vector<std::string> names{"com/example/iyuro/mktest/TestData"};
//
//for (std::string name : names){
//jclass jclazz = env->FindClass(name.c_str());
//main::jclassesGlobalReferences.insert(std::pair<std::string, jclass>(name, jclazz));
//}
//
//return;
//}
//
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_example_iyuro_mktest_MainActivity_helloJNI( JNIEnv* env,
//                                                     jobject thiz)
//{
//    return env->NewStringUTF("Hello from JNI !");
//}
//
//jclass main::getJObjectByName(std::string jclassName){
//    for (auto entity : main::jclassesGlobalReferences){
//        if (entity.first == jclassName){
//            return entity.second;
//        }
//    }
//    return NULL;
//}
