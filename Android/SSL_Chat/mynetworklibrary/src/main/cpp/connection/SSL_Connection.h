//
// Created by Ivan Yurovych on 1/9/19.
//

#ifndef SOCKETSTEST_SSL_CONNECTION_H
#define SOCKETSTEST_SSL_CONNECTION_H

#include <openssl/ossl_typ.h>

#include "Basic_Connection.h"

class SSL_Connection : public Basic_Connection
{
private:
    SSL *mSSL;
    SSL_CTX *mCtx;

    SSL_CTX *InitCTX(void);

public:
    SSL_Connection();

    void open_connection(const char *hostname, int port) override;
    void close_connection() override;
    void load(std::string &resultStr) override;
    void load(int &headerLen, int &fileLen, std::string &resultStr) override;
    void write(std::string request) override;
    int readNum() override;
};


#endif //SOCKETSTEST_SSL_CONNECTION_H
