//
// Created by Ivan Yurovych on 1/9/19.
//

#include "Basic_Connection.h"

#include <netdb.h>
#include <endian.h>
#include <android/log.h>
#include <unistd.h>
#include <linux/in.h>

Basic_Connection::Basic_Connection(){}

void Basic_Connection::open_connection(const char *hostname, int port)
{

    struct hostent *host;
    struct sockaddr_in addr;

    if ( (host = gethostbyname(hostname)) == NULL )
    {
        perror(hostname);
        exit(1);
    }
    sock = socket(PF_INET, SOCK_STREAM, 0);
    bzero(&addr, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    memcpy(&addr.sin_addr, host->h_addr, (size_t)host->h_length);
    if ( connect(sock, (struct sockaddr *)&addr, sizeof(addr)) != 0 )
    {
        close(sock);
        perror(hostname);
        exit(1);
    }
}

void Basic_Connection::close_connection() {
    close(sock);
}

void Basic_Connection::load(std::string& resultStr)
{
    resultStr = "";
    char cur;
    while (read(sock, &cur, 1) > 0)
    {
        if (cur == '\n'){
            break;
        }

        resultStr += cur;
    }
}

void Basic_Connection::write(std::string request) {
    request += "\n";
    if (send(sock, request.c_str(), request.length(), 0) != (int)request.length()) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Error sending request.");
//        exit(1);
    }
}
