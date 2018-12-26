//
// Created by Ivan Yurovych on 12/26/18.
//

#include <thread>
#include "Networker.h"

void checkPendingExceptions(JNIEnv *env, std::string s){
    jboolean flag = env->ExceptionCheck();
    if (flag) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", s.c_str());
    }
}

void makeRequest(std::string hostname, jobject instance)
{
    struct hostent *host = gethostbyname(hostname.c_str());

    if ( (host == NULL) || (host->h_addr == NULL) ) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Error retrieving DNS information.");
        exit(1);
    }

    bzero(&client, sizeof(client));
    client.sin_family = AF_INET;
    client.sin_port = htons( PORT );
    memcpy(&client.sin_addr, host->h_addr, (size_t)host->h_length);

    sock = socket(AF_INET, SOCK_STREAM, 0);

    if (sock < 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Error creating socket.");
        exit(1);
    }

    if (connect(sock, (struct sockaddr *)&client, sizeof(client)) < 0 ) {
        close(sock);
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Could not connect");
        exit(1);
    }

    std::string api_key = "7c381e56b95c83b75aeab1b1628d4363";

    std::stringstream ss;

    ss << "GET / HTTP/1.1\r\nHost: "<< hostname.c_str() << "\r\nConnection: close\r\n\r\n";
    std::string request = ss.str();

    if (send(sock, request.c_str(), request.length(), 0) != (int)request.length()) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Error sending request.");
        exit(1);
    }

    std::string resultStr;
    char cur;
    while ( read(sock, &cur, 1) > 0 ) {
        resultStr += cur;
    }

    sleep(5);

    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", resultStr.c_str());

    main m;
    m.attachEnv();
    JNIEnv *new_env = m.getEnv();
    checkPendingExceptions(new_env, "1");

    jmethodID mjmethodID = nullptr;
    jclassReference jclassR = main::getJclassReferenceByName("com/example/iyuro/mktest/NetworkSingleton/onSuccessDownload");
    mjmethodID = jclassR.getJmID();
    jclass objectMainActivity = (jclass) instance;

    jstring result = (jstring)new_env->NewGlobalRef(new_env->NewStringUTF(resultStr.c_str()));
    new_env->CallVoidMethod(objectMainActivity, mjmethodID, result);

    m.detachMyThread();

    pthread_exit(NULL);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_socketstest_NetworkSingleton_makeRequest(JNIEnv *env, jobject instance,
                                                                jstring s) {
    const char* chostname = env->GetStringUTFChars(s, 0);

    jobject gInstance = env->NewGlobalRef(instance);
    std::thread thrd(makeRequest, std::string(chostname), gInstance);
    thrd.join();
}