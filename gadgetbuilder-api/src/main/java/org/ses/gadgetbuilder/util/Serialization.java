package org.ses.gadgetbuilder.util;

import java.io.*;
import java.util.Base64;

public class Serialization {

    public static byte[] serialize(Serializable payload) {

        byte[] output = new byte[0];

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(payload);
            output = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public static byte[] externalize(Externalizable payload) {

        byte[] output = new byte[0];

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            payload.writeExternal(oos);
            output = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }


    public static void deserialize(byte[] serializedBytes) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serializedBytes));
            ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String serializeToBase64(Serializable payload) {
        return Base64.getEncoder().encodeToString(serialize(payload));
    }

    public static String externalizeToBase64(Externalizable payload) {
        return Base64.getEncoder().encodeToString(externalize(payload));
    }


    public static void serializeToFile(Serializable payload, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(payload);
            oos.flush();
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void externalizeToFile(Externalizable payload, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            payload.writeExternal(oos);
            oos.flush();
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
