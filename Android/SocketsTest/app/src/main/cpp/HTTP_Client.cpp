//
// Created by Ivan Yurovych on 1/4/19.
//

#include "HTTP_Client.h"

#include <sstream>

#include "Basic_Connection.h"

void HTTP_Client::loadData(std::string& resultStr, const char* hostname, int port)
{
    Basic_Connection connection;
    connection.open_connection(hostname, port);

    std::stringstream ss;
    ss << "GET / HTTP/1.1\r\nHost: "<< hostname << "\r\nConnection: close\r\n\r\n";
    std::string request = ss.str();
    connection.write(request);

    connection.load(resultStr);

    connection.close_connection();
}