package org.ses.gadgetbuilder.client;

import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.noparam.MapEntrySetTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.MapGetTrampoline;
import org.ses.gadgetbuilder.factory.GadgetBuilderFactory;
import org.ses.gadgetbuilder.impl.adapters.getters.TemplatesImplMethodInvokeAdapter;
import org.ses.gadgetbuilder.impl.adapters.initializers.TemplatesImplInitializeAdapter;
import org.ses.gadgetbuilder.impl.trampolines.AnnotationInvocationHandlerMapEntrySet;
import org.ses.gadgetbuilder.impl.trampolines.AnnotationInvocationHandlerMapGet;
import org.ses.gadgetbuilder.impl.trampolines.CompositeInvocationHandlerMapGet;
import org.ses.gadgetbuilder.impl.trampolines.compare.PriorityQueueCompare;
import org.ses.gadgetbuilder.impl.trampolines.equals.HashtableEquals;
import org.ses.gadgetbuilder.impl.trampolines.hashcode.HashCodeHashMapTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.tostring.UIDefaultsToStringTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.InvocationTargetException;

public class ConfigUtil {
    public static String DefaultHashCodeTrampoline = HashCodeHashMapTrampoline.class.getSimpleName();
    public static String DefaultEqualsTrampoline = HashtableEquals.class.getSimpleName();
    public static String DefaultToStringTrampoline = UIDefaultsToStringTrampoline.class.getSimpleName();
    public static String DefaultMapEntrySetTrampoline = AnnotationInvocationHandlerMapEntrySet.class.getSimpleName();
    public static String DefaultMapGetTrampoline = CompositeInvocationHandlerMapGet.class.getSimpleName();
    public static String DefaultCompareTrampoline = PriorityQueueCompare.class.getSimpleName();

    public static String DefaultMethodInvocationAdapter = TemplatesImplMethodInvokeAdapter.class.getSimpleName();
    public static String DefaultInstantiateAdapter = TemplatesImplInitializeAdapter.class.getSimpleName();

    private static final String KEY_HASHCODE = "hashCode";


    public static void readConfig(String fileName) {
        //TODO: read defaults from file option
    }


    public static String getDefaultAdapterFor(String chainName) {
        try {
            GadgetChain dummyChain = Reflections.createWithoutConstructor(GadgetBuilderFactory.getChainByName(chainName));
            String adapterName = "";
            if (dummyChain.getAdapterType() == null) return adapterName;

            if (dummyChain.getAdapterType().equals(GetterMethodInvokeAdapter.class) || dummyChain.getAdapterType().equals(MethodInvokeAdapter.class)) {
                adapterName = DefaultMethodInvocationAdapter;
            } else if (dummyChain.getAdapterType().equals(InitializeAdapter.class)) {
                adapterName = DefaultInstantiateAdapter;
            }

            if (!adapterName.isEmpty()) System.out.println("Using default sink adapter: " + adapterName);
            return  adapterName;

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDefaultTrampolineFor(String chainName) {
        try {
            GadgetChain dummyChain = Reflections.createWithoutConstructor(GadgetBuilderFactory.getChainByName(chainName));
            if (dummyChain.getTrampolineType() == null || dummyChain.getTrampolineType().equals(NoTrampoline.class)) return "";

            String trampolineName = "";



            if (dummyChain.getTrampolineType().equals(HashCodeTrampoline.class)) {
                trampolineName = DefaultHashCodeTrampoline;
            } else if (dummyChain.getTrampolineType().equals(EqualsTrampoline.class)) {
                trampolineName = DefaultEqualsTrampoline;
            } else if (dummyChain.getTrampolineType().equals(CompareTrampoline.class)) {
                trampolineName = DefaultCompareTrampoline;
            } else if (dummyChain.getTrampolineType().equals(MapGetTrampoline.class)) {
                trampolineName = DefaultMapGetTrampoline;
            } else if (dummyChain.getTrampolineType().equals(MapEntrySetTrampoline.class)) {
                trampolineName = DefaultMapEntrySetTrampoline;
            } else if (dummyChain.getTrampolineType().equals(ToStringTrampoline.class)) {
                trampolineName = DefaultToStringTrampoline;
            }

            if (!trampolineName.isEmpty()) System.out.println("Using default trampoline: " + trampolineName);

            return trampolineName;

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
