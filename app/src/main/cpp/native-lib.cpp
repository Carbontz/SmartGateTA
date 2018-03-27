#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_edu_smartgate_reza_smartgateta_LoginActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
