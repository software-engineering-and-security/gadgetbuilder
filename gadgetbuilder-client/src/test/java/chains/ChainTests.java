package chains;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ses.gadgetbuilder.adapters.SinkAdapter;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.InstantiateGadgetChain;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.Trampoline;
import org.ses.gadgetbuilder.client.ChainGenUtil;
import org.ses.gadgetbuilder.exceptions.AdapterMismatchException;
import org.ses.gadgetbuilder.factory.GadgetBuilderFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class ChainTests {

    @BeforeAll
    public static void setup() {
        ChainGenUtil.defaultSetPropertyCommand = "gadgetbuilder:gadgetbuilder";
    }

    @Test
    public void test_all_loaded_chains() {

        System.out.println("Testing all loaded gadget chain, trampoline and sinkAdapter combinations");
        System.out.println("--------------");

        try {

            List<Class<? extends GadgetChain>> loadedChains = GadgetBuilderFactory.getChainImplementations();

            for (Class<? extends GadgetChain> chainClass : loadedChains) {

                Sandbox.SandboxSuccess = false;
                String chainClassName = chainClass.getSimpleName();

                Type[] typeParameters =  ((ParameterizedType) chainClass.getGenericSuperclass()).getActualTypeArguments();

                Class<? extends Trampoline> trampolineType = (Class<? extends Trampoline>) typeParameters[0];
                Class<? extends SinkAdapter> adapterType = null;
                if (typeParameters.length == 2) {
                    adapterType = (Class<? extends SinkAdapter>) typeParameters[1];
                }


                for (Class<? extends Trampoline> trampolineClass : GadgetBuilderFactory.getTrampolineImplementations(trampolineType)) {
                    String trampolineName = trampolineClass.getSimpleName();

                    if (adapterType == null) {

                        try {
                            GadgetChain chain = GadgetBuilderFactory.buildChain(chainClass, trampolineClass, adapterType);
                            Object payload = ChainGenUtil.armWithCommand(chain);
                            Sandbox.SandboxSuccess = false;

                            try {
                                Sandbox.serializeDeserialize(payload);
                            } catch (Throwable ignored) {

                            }

                            if (Sandbox.SandboxSuccess) {
                                System.out.println("[Success]: " + String.format("-g %s -t %s", chainClassName, trampolineName));
                            } else {
                                System.out.println("[Fail]: " + String.format("-g %s -t %s", chainClassName, trampolineName));
                            }
                        } catch (Throwable ignored) {
                            System.out.println("[Fail]: " + String.format("-g %s -t %s", chainClassName, trampolineName));

                        }

                    } else {

                        for (Class<? extends SinkAdapter> adapterClass : GadgetBuilderFactory.getAdapterImplementations(adapterType)) {

                            String combined = String.format("-g %s -t %s -a %s", chainClassName, trampolineName, adapterClass.getSimpleName());
                            try {

                                GadgetChain chain = GadgetBuilderFactory.buildChain(chainClass, trampolineClass, adapterClass);
                                Object payload = ChainGenUtil.armWithCommand(chain);
                                Sandbox.SandboxSuccess = false;

                                try {
                                    Sandbox.serializeDeserialize(payload);
                                } catch (Throwable ignored) {   }

                                if (Sandbox.SandboxSuccess) {
                                    System.out.println("[Success]: " + combined);
                                } else {
                                    System.out.println("[Fail]: " + combined);
                                }

                            } catch (AdapterMismatchException e) {
                                System.out.println("[Info]: skipping due to adapter mismatch " + combined);
                            } catch (Throwable ignored) {
                                System.out.println("[Fail]: " + combined);
                            }

                        }

                    }
                }
            }


        } catch (Throwable ex) {
            ex.printStackTrace();
        }





    }




}
