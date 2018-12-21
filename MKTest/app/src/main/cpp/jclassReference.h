//
// Created by Ivan Yurovych on 12/19/18.
//

#ifndef MKTEST_JCLASSREFERENCE_H
#define MKTEST_JCLASSREFERENCE_H

#import <jni.h>

class jclassReference {
    jclass jclazz;
    jmethodID jconstructorID;

public:
    jclassReference(jclass jclazz, jmethodID jconstructorID);

    jclass getJclazz() const;

//    void setJclazz(const _jclass *jclazz);

    jmethodID getJconstructorID() const;

//    void setJconstructorID(const _jmethodID *jconstructorID);
};


#endif //MKTEST_JCLASSREFERENCE_H
