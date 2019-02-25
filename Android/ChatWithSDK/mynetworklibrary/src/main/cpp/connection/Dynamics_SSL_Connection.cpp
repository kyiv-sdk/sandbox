//
// Created by Ivan Yurovych on 2/12/19.
//

#include "Dynamics_SSL_Connection.h"

#include <openssl/err.h>
#include <openssl/ssl.h>
#include <Logger.h>
#include <openssl/evp.h>

#define FAIL    -1

Dynamics_SSL_Connection::Dynamics_SSL_Connection(){}

void Dynamics_SSL_Connection::open_connection(const char *hostname, int port)
{
    Dynamics_Connection::open_connection(hostname, port);

    if (!mConnected){
        return;
    }

    SSL_CTX *ctx;

    SSL_load_error_strings ();
    SSL_library_init ();

    ctx = InitCTX();

    mSSL = SSL_new (ctx);
    if (!mSSL)
    {
        handle_error ("Failed allocating SSL structure");
    }

    SSL_set_connect_state (mSSL);

    SSL_set_fd(mSSL, mSock);
    if ( SSL_connect(mSSL) == FAIL )
    {
        handle_error("Connection failed");
    }

    Logger::getInstance()->log("ssl cipher");
    Logger::getInstance()->log(SSL_get_cipher (mSSL));



    X509* server_cert;
    server_cert = SSL_get_peer_certificate(mSSL);
    Logger::getInstance()->log("ssl certificate");
    Logger::getInstance()->log(server_cert->name);

    if (server_cert)
    {
        X509_free(server_cert);
    }

    long res = SSL_get_verify_result(mSSL);
    if(X509_V_OK != res)
    {
        handle_error("certificate is not safe");
    } else {
        Logger::getInstance()->log("ssl certificate is safe");
    }

    EVP_PKEY * pubkey;
    pubkey = X509_get_pubkey (server_cert);
}

void Dynamics_SSL_Connection::close_connection()
{
    Basic_Connection::close_connection();

    SSL_free(mSSL);
    SSL_CTX_free(mCtx);
}

SSL_CTX* Dynamics_SSL_Connection::InitCTX(void)
{
    const SSL_METHOD *method;

    OpenSSL_add_all_algorithms();
    method = TLSv1_2_method();
    mCtx = SSL_CTX_new(method);
    if ( mCtx == NULL )
    {
        ERR_print_errors_fp(stderr);
        abort();
    }
    return mCtx;
}

void Dynamics_SSL_Connection::write(std::string data)
{
    int strLen = data.length();
    if (SSL_write(mSSL, data.c_str(), strLen) != strLen)
    {
        handle_error("Error sending request.");
    }
}

void Dynamics_SSL_Connection::load(int &headerLen, int &fileLen, std::string &resultStr)
{
    resultStr = "";

    int all_len = headerLen + fileLen;

    int remainedLen = all_len;
    char buf[all_len];

    resultStr.reserve(all_len);

    for (;;)
    {
        int len = SSL_read(mSSL, buf, remainedLen);

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

void Dynamics_SSL_Connection::load(std::string &resultStr)
{
    const int MAX_BUF_SIZE = 1024;
    int bufLen = MAX_BUF_SIZE;
    char buf[MAX_BUF_SIZE];

    resultStr = "";
    for (;;)
    {
        int len = SSL_read(mSSL, buf, bufLen);

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

int Dynamics_SSL_Connection::readNum()
{
    std::string strNum = "";
    char cur;
    while (SSL_read(mSSL, &cur, 1) > 0)
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