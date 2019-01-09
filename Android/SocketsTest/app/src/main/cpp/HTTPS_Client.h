//
// Created by Ivan Yurovych on 1/3/19.
//

#ifndef SOCKETSTEST_SSLCLIENT_H
#define SOCKETSTEST_SSLCLIENT_H

#include <openssl/ossl_typ.h>

#include "HTTP_Client.h"

class HTTPS_Client : public HTTP_Client{
    SSL_CTX* InitCTX(void);
    void handle_error (const char *msg);
public:
    void loadData(std::string &result, const char *hostname, int port) override;
};


#endif //SOCKETSTEST_SSLCLIENT_H
