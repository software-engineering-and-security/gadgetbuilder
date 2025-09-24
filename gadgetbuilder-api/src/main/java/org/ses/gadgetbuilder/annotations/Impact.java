package org.ses.gadgetbuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Impact {

    String RCE = "RCE";
    String FileWrite = "FileWrite";
    String RandomFileWrite = "RandomFileWrite";
    String ZeroFile = "ZeroFile";
    String JNDI = "JNDI Lookup";
    String MethodInvoke = "Method.invoke()";
    String Instantiate = "<init>";
    String DNSLookup = "DNS lookup";
    String LoadClass = "LoadClass";
    String SSRF = "SSRF";
    String ELInvocation = "EL Invocation";
    String SetProperty = "Set Property";
    String JRMPClient = "JRMPClient";
    String JRMPListener = "JRMPListener";
    String DBConnection = "DB Connection";
    String PythonScript = "PythonScript";


    String value() default "";

}
