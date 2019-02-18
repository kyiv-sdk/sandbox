//
// Created by Ivan Yurovych on 2/12/19.
//

#include "Dynamics_Connection.h"

#include <netdb.h>
#include <endian.h>
#include <linux/in.h>
#include <stdlib.h>

#include "gd/GD_C_sys_socket.h"
#include "gd/GD_C_unistd.h"
#include "gd/GD_C_netdb.h"

#include <Logger.h>

#define FAIL    -1

Dynamics_Connection::Dynamics_Connection(){}

void Dynamics_Connection::open_connection(const char *hostname, int port)
{
    struct hostent *host;
    struct sockaddr_in addr;

    mConnected = true;

    if ( (host = GD_gethostbyname(hostname)) == NULL )
    {
        mConnected = false;
        perror(hostname);
        exit(1);
    }

    mSock = GD_socket(PF_INET, SOCK_STREAM, 0);
    bzero(&addr, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    memcpy(&addr.sin_addr, host->h_addr, (size_t)host->h_length);

    if ( GD_connect(mSock, (struct sockaddr *)&addr, sizeof(addr)) != 0 )
    {
        handle_error("connection failed");
        perror(hostname);
        close_connection();
    }
}

void Dynamics_Connection::close_connection()
{
    mConnected = false;
    GD_UNISTD_close(mSock);
}

void Dynamics_Connection::load(int &headerLen, int &fileLen, std::string &resultStr)
{
    resultStr = "";

    int all_len = headerLen + fileLen;

    int remainedLen = all_len;
    char buf[all_len];

    resultStr.reserve(all_len);

    for (;;)
    {
        int len = GD_UNISTD_read(mSock, buf, remainedLen);

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

void Dynamics_Connection::load(std::string &resultStr)
{
    const int MAX_BUF_SIZE = 1024;
    int bufLen = MAX_BUF_SIZE;
    char buf[MAX_BUF_SIZE];

    resultStr = "";
    for (;;)
    {
        int len = GD_UNISTD_read(mSock, buf, bufLen);

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

void Dynamics_Connection::write(std::string data)
{
    int strLen = data.length();
    Logger::getInstance()->log("THIS");
    if (GD_send(mSock, data.c_str(), strLen, 0) != strLen)
    {
        handle_error("Error sending request.");
    }
    Logger::getInstance()->log(data);
}

int Dynamics_Connection::readNum()
{
    std::string strNum = "";
    char cur;
    while (GD_UNISTD_read(mSock, &cur, 1) > 0)
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