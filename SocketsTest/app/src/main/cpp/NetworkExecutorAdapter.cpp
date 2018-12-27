//
// Created by Ivan Yurovych on 12/27/18.
//

#include "NetworkExecutorAdapter.h"

NetworkExecutorAdapter::NetworkExecutorAdapter(jobject ninstance,
                                               void (*ncallback)(jobject, std::string)) {
    instance = ninstance;
    JNIcallback = ncallback;
}
