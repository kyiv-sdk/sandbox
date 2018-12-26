//
// Created by Ivan Yurovych on 12/19/18.
//

#include "JclassReference.h"

jclassReference::jclassReference(jclass jclz, jmethodID njmID) {
    jclazz = jclz;
    jmID = njmID;
}

jclass jclassReference::getJclazz() const {
    return jclazz;
}

//void jclassReference::setJclazz(const _jclass *jclazz) {
//    jclassReference::jclazz = jclazz;
//}

jmethodID jclassReference::getJmID() const {
    return jmID;
}

//void jclassReference::setJconstructorID(const _jmethodID *jmID) {
//    jclassReference::jmID = jmID;
//}
