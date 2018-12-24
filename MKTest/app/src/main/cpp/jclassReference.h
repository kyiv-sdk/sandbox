//
// Created by Ivan Yurovych on 12/19/18.
//

#ifndef MKTEST_JCLASSREFERENCE_H
#define MKTEST_JCLASSREFERENCE_H

#import <jni.h>

class jclassReference {
    jclass jclazz;
    jmethodID jmID;

public:
    jclassReference(jclass jclazz, jmethodID jmID);

    jclass getJclazz() const;

//    void setJclazz(const _jclass *jclazz);

    jmethodID getJmID() const;

//    void setJconstructorID(const _jmethodID *jmID);
};


#endif //MKTEST_JCLASSREFERENCE_H
