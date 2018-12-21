//
// Created by Ivan Yurovych on 12/19/18.
//

#include "jclassReference.h"

jclassReference::jclassReference(jclass jclz, jmethodID jconstrID) {
    jclazz = jclz;
    jconstructorID = jconstrID;
}

jclass jclassReference::getJclazz() const {
    return jclazz;
}

//void jclassReference::setJclazz(const _jclass *jclazz) {
//    jclassReference::jclazz = jclazz;
//}

jmethodID jclassReference::getJconstructorID() const {
    return jconstructorID;
}

//void jclassReference::setJconstructorID(const _jmethodID *jconstructorID) {
//    jclassReference::jconstructorID = jconstructorID;
//}
