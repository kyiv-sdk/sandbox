//
// Created by Ivan Yurovych on 1/9/19.
//

#include "SSL_Connection.h"

#include <openssl/err.h>
#include <openssl/ssl.h>
#include <Logger.h>
#include <openssl/evp.h>

#define FAIL    -1

SSL_Connection::SSL_Connection(){}

void SSL_Connection::open_connection(const char *hostname, int port)
{
    Basic_Connection::open_connection(hostname, port);

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

    Logger::log("ssl cipher");
    Logger::log(SSL_get_cipher (mSSL));



    X509* server_cert;
    server_cert = SSL_get_peer_certificate(mSSL);
    Logger::log("ssl certificate");
    Logger::log(server_cert->name);

    if (server_cert)
    {
        X509_free(server_cert);
    }

    long res = SSL_get_verify_result(mSSL);
    if(X509_V_OK != res)
    {
        handle_error("certificate is not safe");
    } else {
        Logger::log("ssl certificate is safe");
    }

    EVP_PKEY * pubkey;
    pubkey = X509_get_pubkey (server_cert);
}

void SSL_Connection::close_connection()
{
    Basic_Connection::close_connection();

    SSL_free(mSSL);
    SSL_CTX_free(mCtx);
}

SSL_CTX* SSL_Connection::InitCTX(void)
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

void SSL_Connection::write(std::string request)
{
    request += "\n";
    const char *msg = request.c_str();

    SSL_write(mSSL, msg, strlen(msg));
}

void SSL_Connection::load(int &headerLen, int &fileLen, std::string& resultStr)
{
    resultStr = "";
    char buf[1024];
    for (;;) {
        int len = SSL_read(mSSL, buf, sizeof (buf));

        if (len == 0)
            break;

        if (len < 0)
            handle_error ("Failed reading response data");

        std::string sbuf = buf;
        int testi = sbuf.length();
        if (testi > 1024){
            std::string test = sbuf.substr(0, 1024);
            resultStr += sbuf.substr(0, 1024);
        } else {
            resultStr += buf;
        }
        memset(buf, 0, len);
        if (resultStr.back() == '\n') break;
    }
}
