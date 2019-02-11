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

    mConnected = true;

    if ( (host = gethostbyname(hostname)) == NULL )
    {
        mConnected = false;
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
        mConnected = false;
    }
}

void Basic_Connection::close_connection()
{
    mConnected = false;
    close(mSock);
}

void Basic_Connection::load(int &headerLen, int &fileLen, std::string &resultStr)
{
    resultStr = "";

    int all_len = headerLen + fileLen;

    int remainedLen = all_len;
    char buf[all_len];

    resultStr.reserve(all_len);

    for (;;)
    {
        int len = read(mSock, buf, remainedLen);

        if (len == 0)
            break;

        if (len < 0)
        {
            handle_error ("Connection: Failed reading data");
            resultStr = "";
            break;
        }

        remainedLen -= len;

        resultStr.append(buf, len);

        if (remainedLen <= 0)
        {
            break;
        }

        memset(buf, 0, len);
    }
}

void Basic_Connection::load(std::string &resultStr)
{
    const int MAX_BUF_SIZE = 1024;
    int bufLen = MAX_BUF_SIZE;
    char buf[MAX_BUF_SIZE];

    resultStr = "";
    for (;;)
    {
        int len = read(mSock, buf, bufLen);

        if (len == 0)
            break;

        if (len < 0)
        {
            handle_error ("Connection: Failed reading data");
            resultStr = "";
            break;
        }

        std::string sbuf(buf, len);

        resultStr.append(sbuf.c_str(), len);

        memset(buf, 0, len);
    }
}

void Basic_Connection::write(std::string data)
{
    int strLen = data.length();
    if (send(mSock, data.c_str(), strLen, 0) != strLen)
    {
        handle_error("Error sending request.");
    }
}

void Basic_Connection::handle_error (const char *msg)
{
    Logger::getInstance()->log("connection error");
    Logger::getInstance()->log(msg);
}

int Basic_Connection::readNum()
{
    std::string strNum = "";
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

        strNum += cur;
    }

    return atoi(strNum.c_str());
}

bool Basic_Connection::isConnected() const {
    return mConnected;
}
