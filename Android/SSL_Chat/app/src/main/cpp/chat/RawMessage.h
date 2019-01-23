//
// Created by Ivan Yurovych on 1/23/19.
//

#ifndef SSL_CHAT_RAWMESSAGE_H
#define SSL_CHAT_RAWMESSAGE_H


#include <string>

class RawMessage {
    int headerLen, fileLen;
    bool fromServer;
    std::string data;

public:
    RawMessage(int headerLen, int fileLen, bool isFromServer, const std::string &data);

    int getHeaderLen() const;

    int getFileLen() const;

    bool isFromServer() const;

    const std::string &getData() const;
};


#endif //SSL_CHAT_RAWMESSAGE_H
