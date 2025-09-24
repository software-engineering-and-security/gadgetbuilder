package org.ses.gadgetbuilder.impl.agent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class JavaAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        transformClass(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        transformClass(inst);
    }

    public static void transformClass(Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                byte[] byteCode = classfileBuffer;

                if ("java/util/concurrent/PriorityBlockingQueue".equals(className)) {
                    ClassPool pool = ClassPool.getDefault();
                    CtClass ctClass = null;
                    try {
                        ctClass = pool.get("java.util.concurrent.PriorityBlockingQueue");
                        CtMethod method = ctClass.getDeclaredMethod("writeObject");
                        method.setBody("{ $1.defaultWriteObject();}");

                        byteCode = ctClass.toBytecode();
                        ctClass.detach();

                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (CannotCompileException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return byteCode; // No change
            }}, true);
    }
}
