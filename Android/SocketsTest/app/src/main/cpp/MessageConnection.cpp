//
// Created by Ivan Yurovych on 1/10/19.
//

#include "MessageConnection.h"

#include <android/log.h>

MessageConnection::MessageConnection(MessageHandlerAdapter *new_messageHandlerAdapter, const char *t_hostname, int t_port)
{
    messageHandlerAdapter = new_messageHandlerAdapter;
    hostname = t_hostname;
    port = t_port;
    connection.open_connection(hostname, port);
}

MessageConnection::~MessageConnection()
{
    delete messageHandlerAdapter;
    connection.close_connection();
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", "%s", "~MessageConnection() called!");
}

void MessageConnection::send(const char* message) {

}