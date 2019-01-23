//
// Created by Ivan Yurovych on 1/9/19.
//

#include "Basic_Connection.h"

#include <netdb.h>
#include <endian.h>
#include <unistd.h>
#include <linux/in.h>
#include <Logger.h>
#include <stdlib.h>

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
    mSock = socket(PF_INET, SOCK_STREAM, 0);
    bzero(&addr, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    memcpy(&addr.sin_addr, host->h_addr, (size_t)host->h_length);
    if ( connect(mSock, (struct sockaddr *)&addr, sizeof(addr)) != 0 )
    {
        handle_error("connection failed");
        close(mSock);
        perror(hostname);
//        exit(1);
    }
}

void Basic_Connection::close_connection()
{
    close(mSock);
}

void Basic_Connection::load(int &headerLen, int &fileLen, std::string& resultStr)
{
    std::string strHeaderLen = "";
    std::string strFileLen = "";
    char cur;
    while (read(mSock, &cur, 1) > 0)
    {
        if (cur == 1)
        {
            continue;
        }
        if (cur == 2)
        {
            break;
        }

        strHeaderLen += cur;
    }

    headerLen = atoi(strHeaderLen.c_str());

    while (read(mSock, &cur, 1) > 0)
    {
        if (cur == 2)
        {
            break;
        }

        strFileLen += cur;
    }

    fileLen = atoi(strFileLen.c_str());

    int remainedLen = headerLen + fileLen;
    resultStr = "";
    int bufLen = headerLen;
    const int MAX_BUF_SIZE = 1024;
    char buf[MAX_BUF_SIZE];
    for (;;) {
        int len = read(mSock, buf, bufLen);

        if (len == 0)
            break;

        if (len < 0)
        {
            handle_error ("Failed reading response data");
            break;
        }

        remainedLen -= len;

        std::string sbuf = buf;
        int testi = sbuf.length();
        if (testi > bufLen){
            std::string test = sbuf.substr(0, bufLen);
            resultStr += sbuf.substr(0, bufLen);
        } else {
            resultStr += buf;
        }
        memset(buf, 0, len);
        if (remainedLen == 0){
            break;
        } else {
            if (remainedLen > MAX_BUF_SIZE){
                bufLen = MAX_BUF_SIZE;
            } else {
                bufLen = remainedLen;
            }
        }
    }
}

void Basic_Connection::write(std::string request)
{
//    request += "\n";
    int strLen = request.length();
    int MAX_BUF_SIZE = 1024;

    int msgToSendLen = strLen;
    int i = 0;
    while (strLen > 0){
        if (strLen > MAX_BUF_SIZE){
            msgToSendLen = MAX_BUF_SIZE;
        } else {
            msgToSendLen = strLen;
        }
        strLen -= msgToSendLen;

        std::string toSend = request.substr(i, i + msgToSendLen);
        if (send(mSock, toSend.c_str(), msgToSendLen, 0) != msgToSendLen)
        {
            handle_error("Error sending request.");
        }

        i+= msgToSendLen;
    }
}

void Basic_Connection::handle_error (const char *msg)
{
    Logger::log("error");
    Logger::log(msg);
//    exit (1);
}
