//
// Created by Ivan Yurovych on 1/9/19.
//

#include <jni.h>

#include "JNI_Helper.h"
#include "MessageHandlerAdapter.h"
#include "MessageHandler.h"

class MessageAdapterImplementation: public MessageHandlerAdapter
{
private:
    void (*JNIcallback)(jobject, int headerLen, int fileLen, std::string *);
    jobject instance;
    JNIEnv *m_env;
public:
    void runCallback(int headerLen, int fileLen, std::string* ) override;
    MessageAdapterImplementation(JNIEnv *env, jobject ninstance,
    void (*ncallback)(jobject, int headerLen, int Filelen, std::string *));
    virtual ~MessageAdapterImplementation();
};

void MessageAdapterImplementation::runCallback(int headerLen, int fileLen, std::string* resultData)
{
    JNIcallback(instance, headerLen, fileLen, resultData);
}

MessageAdapterImplementation::MessageAdapterImplementation(JNIEnv *t_env, jobject ninstance,
void (*ncallback)(jobject, int headerLen, int fileLen,
                  std::string*))
{
    JNIcallback = ncallback;
    instance = ninstance;
    m_env = t_env;
}

MessageAdapterImplementation::~MessageAdapterImplementation() {
    m_env->DeleteGlobalRef(instance);
}

void jni_sendMessageToJava(jobject instance, int headerLen, int fileLen, std::string* resultData)
{
    JNI_Helper jniHelper;
    jniHelper.attachEnv();
    JNIEnv *new_env = jniHelper.getEnv();
    jniHelper.checkPendingExceptions(new_env, "2");

    jmethodID mjmethodID = JNI_Helper::mMessageHandlerOnSuccessMethodId;
    jclass objectMainActivity = (jclass) instance;

    int size = (*resultData).size();
    jbyteArray result = new_env->NewByteArray(size);
    new_env->SetByteArrayRegion(result, 0, (*resultData).size(), (const jbyte*)(*resultData).c_str());

    new_env->CallVoidMethod(objectMainActivity, mjmethodID, headerLen, fileLen, result);

    new_env->DeleteLocalRef(result);

    jniHelper.detachMyThread();
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_iyuro_ssl_1chat_network_NetworkManager_cppCreateMessageHandler(
        JNIEnv *env, jobject instance, jstring t_host, jint t_port, jboolean isSSLEnabled)
{
    const char* m_host = env->GetStringUTFChars(t_host, 0);
    jobject globalInstance = env->NewGlobalRef(instance);
    MessageAdapterImplementation *messageAdapterImplementation = new MessageAdapterImplementation(env, globalInstance, jni_sendMessageToJava);
    MessageHandler *messageHandler = new MessageHandler(m_host, (int)t_port, (bool)isSSLEnabled, messageAdapterImplementation);

    return (jlong)messageHandler;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_ssl_1chat_network_NetworkManager_cppSendMessage(
        JNIEnv *env, jobject instance, jlong t_messageHandler, jbyteArray t_jByteArray)
{
    MessageHandler* messageHandler = (MessageHandler*) t_messageHandler;

    jsize num_bytes = env->GetArrayLength(t_jByteArray);
//    char *buffer = static_cast<char *>(malloc(num_bytes + 1));

//    if (!buffer)
//    {
//        // handle allocation failure ...
//    }

    jbyte* elements = env->GetByteArrayElements(t_jByteArray, NULL);

    if (!elements)
    {
        // handle JNI error ...
    }

//    memcpy(buffer, elements, num_bytes);
//    buffer[num_bytes] = 0;

    char *buffer = reinterpret_cast<char *>(elements);

    messageHandler->send((int) num_bytes, buffer);

    env->ReleaseByteArrayElements(t_jByteArray, elements, JNI_ABORT);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_ssl_1chat_network_NetworkManager_cppDeleteMessageHandler(
        JNIEnv *env, jobject instance, jlong t_messageHandler)
{
    MessageHandler* messageHandler = (MessageHandler*) t_messageHandler;
    delete messageHandler;
}
