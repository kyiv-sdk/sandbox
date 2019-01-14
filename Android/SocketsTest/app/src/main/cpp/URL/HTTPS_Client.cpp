//
// Created by Ivan Yurovych on 1/3/19.
//

#include "HTTPS_Client.h"

#include <sstream>

#include "../Connection/SSL_Connection.h"

void HTTPS_Client::loadData(std::string &resultStr, const char *hostname, int port) {
    SSL_Connection connection;
    connection.open_connection(hostname, port);

    std::stringstream ss;
    ss << "GET / HTTP/1.1\r\nHost: "<< hostname << "\r\nConnection: close\r\n\r\n";
    std::string request = ss.str();
    connection.write(request);

    connection.load(resultStr);

    connection.close_connection();
}