package org.ses.gadgetbuilder.factory;

import org.reflections.Reflections;
import org.ses.gadgetbuilder.adapters.SinkAdapter;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.Trampoline;
import org.ses.gadgetbuilder.exceptions.AdapterMismatchException;
import org.ses.gadgetbuilder.exceptions.NoSuchChainException;
import org.ses.gadgetbuilder.exceptions.NoSuchTrampolineException;
import org.ses.gadgetbuilder.exceptions.TrampolineMismatchException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class GadgetBuilderFactory {

    private static GadgetBuilderFactory instance;
    private static final Reflections Reflections = new Reflections("org.ses.gadgetbuilder");


    private GadgetBuilderFactory() {
    }

    public static GadgetBuilderFactory getInstance() {
        if (GadgetBuilderFactory.instance == null) GadgetBuilderFactory.instance = new GadgetBuilderFactory();
        return GadgetBuilderFactory.instance;
    }

    private static class ClassNameComparator implements Comparator<Class> {
        @Override
        public int compare(Class o1, Class o2) {
            return o1.getSimpleName().compareTo(o2.getSimpleName());
        }
    }


    public static List<Class<? extends GadgetChain>> getChainImplementations() {
        List<Class<? extends GadgetChain>> loadedChains = new ArrayList<>();
        for (Class<? extends GadgetChain> clazz : Reflections.getSubTypesOf(GadgetChain.class)) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                loadedChains.add(clazz);
            }
        }
        loadedChains.sort(new ClassNameComparator());

        return loadedChains;
    }

    public static Set<String> getImpacts() {
        Set<String> impacts = new HashSet<>();
        getChainImplementations().forEach(chain -> {
            Impact impactAnnotation = chain.getAnnotation(Impact.class);
            if (impactAnnotation != null) impacts.add(impactAnnotation.value());
        });

        return impacts;
    }

    public static List<Class<? extends Trampoline>> getTrampolineImplementations() {
        List<Class<? extends Trampoline>> loadedTrampolines = new ArrayList<>();
        for (Class<? extends Trampoline> clazz : Reflections.getSubTypesOf(Trampoline.class)) {
            if (!Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isInterface(clazz.getModifiers())) {
                loadedTrampolines.add(clazz);
            }
        }
        loadedTrampolines.sort(new ClassNameComparator());
        return loadedTrampolines;
    }

    public static List<Class<? extends Trampoline>> getTrampolineImplementations(Class<? extends Trampoline> type) {
        List<Class<? extends Trampoline>> subTrampolines = new ArrayList<>();
        getTrampolineImplementations().forEach(trampoline -> {
            if (type.isAssignableFrom(trampoline)) {
                subTrampolines.add(trampoline);
            }
        });
        return subTrampolines;
    }

    public static List<Class<? extends SinkAdapter>> getAdapterImplementations() {
        List<Class<? extends SinkAdapter>> loadedAdapters = new ArrayList<>();
        for (Class<? extends SinkAdapter> clazz : Reflections.getSubTypesOf(SinkAdapter.class)) {
            if (!Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isInterface(clazz.getModifiers())) {
                loadedAdapters.add(clazz);
            }
        }
        loadedAdapters.sort(new ClassNameComparator());
        return loadedAdapters;
    }

    public static List<Class<? extends SinkAdapter>> getAdapterImplementations(Class<? extends SinkAdapter> type) {

        List<Class<? extends SinkAdapter>> subAdapters = new ArrayList<>();
        getAdapterImplementations().forEach(adapter -> {
            if (type.isAssignableFrom(adapter)) {
                subAdapters.add(adapter);
            }
        });
        return subAdapters;
    }


    public static Class<? extends GadgetChain> getChainByName(String chain){
        for (Class<? extends GadgetChain> chainClass : getChainImplementations()) {
            if (chainClass.getSimpleName().equals(chain)) {
                return chainClass;
            }
        }
        return null;
    }

    public static Class<? extends Trampoline> getTrampolineByName(String trampoline){
        for (Class<? extends Trampoline> trampolineClass : getTrampolineImplementations()) {
            if (trampolineClass.getSimpleName().equals(trampoline)) {
                return trampolineClass;
            }
        }
        return null;
    }

    public static Class<? extends SinkAdapter> getAdapterByName(String adapter){
        for (Class<? extends SinkAdapter> adapterClass : getAdapterImplementations()) {
            if (adapterClass.getSimpleName().equals(adapter)) {
                return adapterClass;
            }
        }
        return null;
    }

    public static GadgetChain buildChain(Class<? extends GadgetChain> baseChainClass, Class<? extends Trampoline> trampolineClass, Class<? extends SinkAdapter> adapterClass) throws NoSuchChainException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchTrampolineException, TrampolineMismatchException, AdapterMismatchException, NoSuchMethodException {
        Constructor<?> defaultChainConstructor = null;

        for (Constructor<?> ctor : baseChainClass.getDeclaredConstructors()) {
            for (Class param : ctor.getParameterTypes()) {
                if (Trampoline.class.isAssignableFrom(param)) {
                    if (!param.isAssignableFrom(trampolineClass))
                        throw new TrampolineMismatchException(baseChainClass, param);
                    defaultChainConstructor = ctor;
                }
                if (SinkAdapter.class.isAssignableFrom(param)) {
                    if (!param.isAssignableFrom(adapterClass))
                        throw new AdapterMismatchException(baseChainClass, param);
                }
            }
        }

        Trampoline trampolineImpl;

        if (trampolineClass == null || trampolineClass == NoTrampoline.class)
            trampolineImpl = NoTrampoline.getInstance();
        else
            trampolineImpl = trampolineClass.getDeclaredConstructor().newInstance();

        if (adapterClass == null) {
            return (GadgetChain) defaultChainConstructor.newInstance(trampolineImpl);
        } else {
            SinkAdapter sinkAdapterImpl = adapterClass.getDeclaredConstructor().newInstance();
            return (GadgetChain) defaultChainConstructor.newInstance(trampolineImpl, sinkAdapterImpl);
        }
    }



    public static GadgetChain buildChain(String baseChain, String trampoline, String adapter) throws NoSuchChainException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchTrampolineException, TrampolineMismatchException, AdapterMismatchException, NoSuchMethodException {

        Class<? extends GadgetChain> gadgetChainClass = getChainByName(baseChain);
        if (gadgetChainClass == null) throw new NoSuchChainException(baseChain);

        Class<? extends Trampoline> trampolineClass;

        if (trampoline != null && !trampoline.isEmpty()) {
            trampolineClass = getTrampolineByName(trampoline);
            if (trampolineClass == null) throw new NoSuchTrampolineException(trampoline);
        } else  {
            trampolineClass = NoTrampoline.class;
        }

        Class<? extends SinkAdapter> adapterClass = null;
        if (adapter != null && !adapter.isEmpty()) {
            adapterClass = getAdapterByName(adapter);
        }

        return buildChain(gadgetChainClass, trampolineClass, adapterClass);
    }

}
