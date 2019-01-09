//
// Created by Ivan Yurovych on 12/24/18.
//

#include <sstream>
#include <jni.h>
#include <android/log.h>
#include "WebLoader.h"

int sock;
struct sockaddr_in client;
int PORT = 80;

void* test(void *params)
{
    addrinfo *res;
    if(getaddrinfo("api.themoviedb.org", "80", 0, &res)) {
        fprintf(stderr, "Can't lookup api.themoviedb.org\n");
        return NULL;
    }

    struct hostent *host = gethostbyname("api.themoviedb.org");

    if ( (host == NULL) || (host->h_addr == NULL) ) {
//        cout << "Error retrieving DNS information." << endl;
        exit(1);
    }

    bzero(&client, sizeof(client));
    client.sin_family = AF_INET;
    client.sin_port = htons( PORT );
    memcpy(&client.sin_addr, host->h_addr, (size_t)host->h_length);

    sock = socket(AF_INET, SOCK_STREAM, 0);

    if (sock < 0) {
//        cout << "Error creating socket." << endl;
        exit(1);
    }

    if (connect(sock, (struct sockaddr *)&client, sizeof(client)) < 0 ) {
        close(sock);
//        cout << "Could not connect" << endl;
        exit(1);
    }

    std::string api_key = "7c381e56b95c83b75aeab1b1628d4363";

    std::stringstream ss;

    ss << "GET /3/movie/" << 550 << "?api_key=7c381e56b95c83b75aeab1b1628d4363 HTTP/1.1\r\n"
       << "Host: api.themoviedb.org\r\n"
       << "Accept: application/json\r\n"
       << "Connection: close\r\n"
       << "\r\n\r\n";
    std::string request = ss.str();

    if (send(sock, request.c_str(), request.length(), 0) != (int)request.length()) {
//        cout << "Error sending request." << endl;
        exit(1);
    }

    std::string resultStr;
    char cur;
    while ( read(sock, &cur, 1) > 0 ) {
//        printf("%c", cur);
        resultStr += cur;
    }

    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", resultStr.c_str());

    pthread_exit(NULL);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_mktest_MainActivity_testNetwork(JNIEnv *env, jobject instance) {
    pthread_t tid;
    pthread_attr_t attr;

    pthread_attr_init(&attr);
    pthread_create(&tid, NULL, test, NULL);
}

