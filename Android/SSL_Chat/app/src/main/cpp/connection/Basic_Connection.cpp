//
// Created by Ivan Yurovych on 1/9/19.
//

#include "Basic_Connection.h"

#include <netdb.h>
#include <endian.h>
#include <unistd.h>
#include <linux/in.h>
#include <Logger.h>

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

void Basic_Connection::load(std::string& resultStr)
{
    resultStr = "";
    char cur;
    while (read(mSock, &cur, 1) > 0)
    {
        if (cur == '\n')
        {
            break;
        }

        resultStr += cur;
    }
}

void Basic_Connection::write(std::string request)
{
    request += "\n";
    if (send(mSock, request.c_str(), request.length(), 0) != (int)request.length())
    {
        handle_error("Error sending request.");
    }
}

void Basic_Connection::handle_error (const char *msg)
{
    Logger::log("error");
    Logger::log(msg);
//    exit (1);
}
