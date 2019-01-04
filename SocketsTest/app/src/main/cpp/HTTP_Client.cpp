//
// Created by Ivan Yurovych on 1/4/19.
//

#include "HTTP_Client.h"

#include <netdb.h>
#include <endian.h>
#include <sstream>
#include <android/log.h>
#include <unistd.h>
#include <linux/in.h>

void HTTP_Client::loadData(std::string& resultStr, const char* hostname, int port)
{
    sock = OpenConnection(hostname, port);

    std::stringstream ss;

    ss << "GET / HTTP/1.1\r\nHost: "<< hostname << "\r\nConnection: close\r\n\r\n";
    std::string request = ss.str();

    if (send(sock, request.c_str(), request.length(), 0) != (int)request.length()) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Error sending request.");
        exit(1);
    }

    char cur;
    while ( read(sock, &cur, 1) > 0 ) {
        resultStr += cur;
    }
}

int HTTP_Client::OpenConnection(const char *hostname, int port)
{   int sd;
    struct hostent *host;
    struct sockaddr_in addr;

    if ( (host = gethostbyname(hostname)) == NULL )
    {
        perror(hostname);
        exit(1);
    }
    sd = socket(PF_INET, SOCK_STREAM, 0);
    bzero(&addr, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    memcpy(&addr.sin_addr, host->h_addr, (size_t)host->h_length);
    if ( connect(sd, (struct sockaddr *)&addr, sizeof(addr)) != 0 )
    {
        close(sd);
        perror(hostname);
        exit(1);
    }
    return sd;
}