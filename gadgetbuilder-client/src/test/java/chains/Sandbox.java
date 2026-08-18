package chains;

import org.ses.gadgetbuilder.util.Serialization;

import java.io.*;
import java.net.SocketPermission;
import java.net.URLPermission;
import java.security.Permission;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;

public class Sandbox {

    public static boolean SandboxSuccess = false;

    public static final String TARGET_SYSTEM_PROPERTY = "gadgetbuilder";
    public static final String TARGET_URL = "localhost";

    public static void serializeDeserialize(Object o) throws Exception {
        byte[] payloadBytes = Serialization.serialize((Serializable) o);

        loadSecurityManager();
        Serialization.deserialize(payloadBytes);
    }

    public static void loadSecurityManager() {
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        System.setProperty("jdk.xml.enableTemplatesImplDeserialization", "true");
        Policy.setPolicy(new SMPolicy());
        System.setSecurityManager(new LoggingSecurityManager());
    }

    static class LoggingSecurityManager extends SecurityManager {

        @Override
        public void checkPermission(java.security.Permission perm) {
            try {
                super.checkPermission(perm);
            } catch (SecurityException e) {
                Sandbox.SandboxSuccess = true;
                throw e;
            }
        }

        @Override
        public void checkPermission(java.security.Permission perm, Object context) {
            try {
                super.checkPermission(perm, context);
            } catch (SecurityException e) {
                Sandbox.SandboxSuccess = true;
                throw e;
            }
        }
    }

    static class SMPolicy extends Policy {



        @Override
        public boolean implies(ProtectionDomain domain, Permission permission) {

            boolean isMatchingStackTraceElement = false;

            for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
                if ("java.io.ObjectInputStream".equals(elem.getClassName()) && "readObject".equals(elem.getMethodName()) ) {
                    isMatchingStackTraceElement = true;
                    break;
                }
            }
            if (!isMatchingStackTraceElement) return true;




            if (permission instanceof SocketPermission) {

                SocketPermission sp = (SocketPermission) permission;
                return !(sp.toString().contains(TARGET_URL));
            }

            if (permission instanceof URLPermission) {
                URLPermission perm = (URLPermission)  permission;
                return !(perm.toString().contains(TARGET_URL));
            }

            if (permission instanceof FilePermission) {
                FilePermission filePerm = (FilePermission) permission;

                // disallow Runtime.exec calls
                if (filePerm.getActions().contains("execute")) {
                    return false;
                }

                // Disallow file write/delete
                if (filePerm.getActions().contains("write") || filePerm.getActions().contains("delete")) {
                    return false;
                }
            }

            if (permission instanceof PropertyPermission) {
                return !permission.getName().contains(TARGET_SYSTEM_PROPERTY);
            }

            return true;
        }

    }


}
