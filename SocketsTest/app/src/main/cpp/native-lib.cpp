#include <jni.h>
#include <string>
#include <netdb.h>
#include <linux/in.h>
#include <endian.h>
#include <zconf.h>
#include <sstream>
#include <android/log.h>
#include <android/looper.h>
#include <unistd.h>

int sock;
struct sockaddr_in client;
int PORT = 80;

struct targs{
    std::string hostname;
    std::string out;
};

void* makeRequest(void *params)
{
    struct targs *args;
    args = (struct targs*)params;

    std::string hostname = args->hostname;

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

    args->out = std::string(resultStr);

//    sleep(5);

    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", resultStr.c_str());

    pthread_exit(NULL);
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_iyuro_socketstest_MainActivity_makeRequest(JNIEnv *env, jobject instance, jstring s) {
    pthread_t tid;

    const char* chostname = env->GetStringUTFChars(s, 0);

    std::string hostname(chostname);
    env->ReleaseStringUTFChars(s, chostname);
    std::string out;
    struct targs* args =(targs *)malloc(sizeof(struct targs));
    args->hostname = hostname;
    args->out = out;

    pthread_create(&tid, NULL, makeRequest, (void *)args);
    pthread_join(tid, NULL);

    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", args->out.c_str());

    jstring result = (jstring)env->NewGlobalRef(env->NewStringUTF(args->out.c_str()));

    return result;
}