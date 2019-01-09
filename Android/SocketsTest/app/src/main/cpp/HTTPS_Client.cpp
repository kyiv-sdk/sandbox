//
// Created by Ivan Yurovych on 1/3/19.
//

#include "HTTPS_Client.h"

#include <openssl/ssl.h>
#include <openssl/err.h>
#include <android/log.h>
#include <string>
#include <zconf.h>

#define FAIL    -1

void HTTPS_Client::loadData(std::string &resultStr, const char *hostname, int port) {
    SSL_CTX *ctx;
    int socket;

    SSL_load_error_strings ();
    SSL_library_init ();

    ctx = InitCTX();

    SSL *ssl = SSL_new (ctx);
    if (!ssl)
        handle_error ("Failed allocating SSL structure");

    SSL_set_connect_state (ssl);

    socket = OpenConnection(hostname, port);
    SSL_set_fd(ssl, socket);
    if ( SSL_connect(ssl) == FAIL )
        ERR_print_errors_fp(stderr);
    else
    {
        std::string request = "GET / ";
        request += "HTTP";
        request += "/1.1\r\nHost: ";
        request += hostname;
        request +="\r\nConnection: close\r\n\r\n";
        const char *msg = request.c_str();

        printf("Connected with %s encryption\n", SSL_get_cipher(ssl));
        SSL_write(ssl, msg, strlen(msg));

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

        SSL_free(ssl);
    }
    close(socket);
    SSL_CTX_free(ctx);
}


void HTTPS_Client::handle_error (const char *msg)
{
    fprintf (stderr, "%s\n", msg);
    ERR_print_errors_fp (stderr);
    exit (1);
}

SSL_CTX* HTTPS_Client::InitCTX(void){
   const SSL_METHOD *method;
    SSL_CTX *ctx;

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