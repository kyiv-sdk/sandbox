//
// Created by Ivan Yurovych on 1/10/19.
//

#ifndef SOCKETSTEST_MESSAGECONNECTION_H
#define SOCKETSTEST_MESSAGECONNECTION_H


#include "Basic_Connection.h"
#include "MessageHandlerAdapter.h"

class MessageConnection {
private:
    Basic_Connection connection;
    const char *hostname;
    int port;

    MessageHandlerAdapter *messageHandlerAdapter;
public:
    MessageConnection(MessageHandlerAdapter *new_messageHandlerAdapter, const char *t_hostname, int t_port);
    void send(const char* message);
    ~MessageConnection();
};


#endif //SOCKETSTEST_MESSAGECONNECTION_H
