//
// Created by Ivan Yurovych on 2/12/19.
//

#ifndef CHATWITHSDK_DYNAMICS_SSL_CONNECTION_H
#define CHATWITHSDK_DYNAMICS_SSL_CONNECTION_H


#include <openssl/ossl_typ.h>

#include "Dynamics_Connection.h"

class Dynamics_SSL_Connection : public Dynamics_Connection
{
private:
    SSL *mSSL;
    SSL_CTX *mCtx;

    SSL_CTX *InitCTX(void);
public:
    Dynamics_SSL_Connection();

    void open_connection(const char *hostname, int port) override;
    void close_connection() override;
    void load(std::string &resultStr) override;
    void load(int &headerLen, int &fileLen, std::string &resultStr) override;
    void write(std::string request) override;
    int readNum() override;
};


#endif //CHATWITHSDK_DYNAMICS_SSL_CONNECTION_H
