//
// Created by Ivan Yurovych on 1/9/19.
//

#include "SSL_Connection.h"

#include <android/log.h>
#include <openssl/err.h>
#include <openssl/ssl.h>

#define FAIL    -1

SSL_Connection::SSL_Connection(){}

void SSL_Connection::open_connection(const char *hostname, int port) {
    Basic_Connection::open_connection(hostname, port);

    SSL_CTX *ctx;

    SSL_load_error_strings ();
    SSL_library_init ();

    ctx = InitCTX();

    ssl = SSL_new (ctx);
    if (!ssl)
        handle_error ("Failed allocating SSL structure");

    SSL_set_connect_state (ssl);

    SSL_set_fd(ssl, sock);
    if ( SSL_connect(ssl) == FAIL )
        handle_error("Connection failed");
}

void SSL_Connection::close_connection() {
    Basic_Connection::close_connection();

    SSL_free(ssl);
    SSL_CTX_free(ctx);
}

void SSL_Connection::handle_error (const char *msg)
{
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG_SSL--------", "resultStr=%s", msg);
    exit (1);
}

SSL_CTX* SSL_Connection::InitCTX(void)
{
    const SSL_METHOD *method;

    OpenSSL_add_all_algorithms();
    method = TLSv1_2_method();
    ctx = SSL_CTX_new(method);
    if ( ctx == NULL )
    {
        ERR_print_errors_fp(stderr);
        abort();
    }
    return ctx;
}

void SSL_Connection::write(std::string request) {
    const char *msg = request.c_str();

    SSL_write(ssl, msg, strlen(msg));
}

void SSL_Connection::load(std::string &resultStr) {
    char buf[1024];
    for (;;) {
        int len = SSL_read(ssl, buf, sizeof (buf));

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
    }

    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG_SSL--------", "resultStr=%s", resultStr.c_str());
}
