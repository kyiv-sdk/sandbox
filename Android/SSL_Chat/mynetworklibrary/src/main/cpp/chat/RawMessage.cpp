//
// Created by Ivan Yurovych on 1/23/19.
//

#include "RawMessage.h"

RawMessage::RawMessage(int headerLen, int fileLen, bool isFromServer, const std::string &data)
        : headerLen(headerLen), fileLen(fileLen), fromServer(isFromServer), data(data) {}

int RawMessage::getHeaderLen() const {
    return headerLen;
}

int RawMessage::getFileLen() const {
    return fileLen;
}

bool RawMessage::isFromServer() const {
    return fromServer;
}

const std::string &RawMessage::getData() const {
    return data;
}
