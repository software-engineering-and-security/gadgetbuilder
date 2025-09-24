package org.ses.gadgetbuilder.chains.main;

import org.ses.gadgetbuilder.adapters.SinkAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.command.*;
import org.ses.gadgetbuilder.chains.trampolines.*;
import org.ses.gadgetbuilder.factory.GadgetBuilderFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public abstract class GadgetChain<T extends Trampoline> {

    public GadgetChain(T _trampoline) {
        this.trampolineChain = _trampoline;
        this.commandFormat = CommandFormat.getCommandFormatFromImpact(this.getImpact());
    }



    protected CommandFormat commandFormat;
    protected T trampolineChain;

    public void setTrampoline(T _trampolineChain) {
        this.trampolineChain = _trampolineChain;
    }

    public T getTrampoline() {        return trampolineChain;    }

    public Object build(String command) throws Exception {

        if (!this.commandFormat.isValidCommandFormat(command)) {
            throw new IllegalArgumentException(String.format("Command syntax is: %s", commandFormat.getCommandFormat()));
        }

        TrampolineConnector innerPayload = this.createPayload(command);
        Object payload = null;

        if (trampolineChain == null || trampolineChain instanceof NoTrampoline)
            payload = innerPayload.base;
        else if (trampolineChain instanceof NoParameterTrampoline)
            payload =  ((NoParameterTrampoline) trampolineChain).wrapPayload(innerPayload.base);
        else if (trampolineChain instanceof SingleParameterTrampoline)
            payload = ((SingleParameterTrampoline) trampolineChain).wrapPayload(innerPayload.base, innerPayload.param1);
        else if (trampolineChain instanceof DoubleParameterTrampoline)
            payload = ((DoubleParameterTrampoline) trampolineChain).wrapPayload(innerPayload.base, innerPayload.param1, innerPayload.param2);

        postProcessPayload();

        return payload;
    }

    protected abstract TrampolineConnector createPayload(String command) throws Exception;

    protected abstract void postProcessPayload() throws Exception;



    protected String[] getDependencies() {
        Dependencies dependenciesAnnotation = this.getClass().getAnnotation(Dependencies.class);
        if (dependenciesAnnotation == null) return new String[0];
        return dependenciesAnnotation.value();
    }

    protected String[] getAuthors() {
        Authors authorsAnnotation = this.getClass().getAnnotation(Authors.class);
        if (authorsAnnotation == null) return new String[0];
        return authorsAnnotation.value();
    }

    public String getImpact() {
        Impact impactAnnotation = this.getClass().getAnnotation(Impact.class);
        if (impactAnnotation == null) return "Undefined";
        else return impactAnnotation.value();
    }

    public Class<? extends Trampoline> getTrampolineType() {
        return (Class<? extends Trampoline>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<? extends SinkAdapter> getAdapterType() {
        Type[] genericTypes = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        if (genericTypes.length > 1) {
            return (Class<? extends SinkAdapter>) genericTypes[1];
        }
        return null;
    }

    public int getVariationCount() {
        int trampolineVariants = this.getTrampolineType().equals(NoTrampoline.class) ? 1 : GadgetBuilderFactory.getTrampolineImplementations(this.getTrampolineType()).size();
        int adapterVariants = this.getAdapterType() == null ? 1 : GadgetBuilderFactory.getAdapterImplementations(this.getAdapterType()).size();

        return trampolineVariants * adapterVariants;
    }

    @Override
    public String toString() {

        String usageExample = String.format("java -jar gadgetbuilder.jar -g %s -c \"%s\"", this.getClass().getSimpleName(), this.getCommandFormat());

        StringBuilder output = new StringBuilder();
        output.append("Name:\t").append(this.getClass().getSimpleName()).append("\n");
        output.append("Dependencies:\t").append(Arrays.toString(this.getDependencies())).append("\n");
        output.append("Authors:\t").append(Arrays.toString(this.getAuthors())).append("\n");
        output.append("Impact:\t").append(this.getImpact()).append("\n");
        output.append("Trampoline Type:\t").append(this.getTrampolineType().getSimpleName()).append("\n");

        StringBuilder availableTrampolines = new StringBuilder();
        GadgetBuilderFactory.getTrampolineImplementations(this.getTrampolineType()).forEach(
                trampoline -> availableTrampolines.append(trampoline.getSimpleName()).append(","));

        availableTrampolines.deleteCharAt(availableTrampolines.length() -1);
        output.append("Available Trampolines:\t").append(availableTrampolines.toString()).append("\n");
        output.append("Variation count:\t").append(this.getVariationCount()).append("\n");

        usageExample += " -t " + availableTrampolines.toString().split(",")[0];

        Class<? extends SinkAdapter> adapterClass = this.getAdapterType();

        if (adapterClass != null) {
            output.append("Adapter:\t").append(this.getAdapterType().getSimpleName()).append("\n");

            StringBuilder availableAdapters = new StringBuilder();
            GadgetBuilderFactory.getAdapterImplementations(adapterClass).forEach(
                    adapter -> availableAdapters.append(adapter.getSimpleName()).append(","));
            availableAdapters.deleteCharAt(availableAdapters.length() -1);
            output.append("Available Adapters:\t").append(availableAdapters.toString()).append("\n");

            usageExample += " -a " + availableAdapters.toString().split(",")[0];
        }

        output.append("Usage Example:\t").append(usageExample).append("\n");
        output.append("Chain:\n");
        output.append("------------------\n");
        output.append(this.getStackTrace()).append("\n");
        output.append("------------------");
        return output.toString();
    }

    protected String getStackTrace() {
        return "Stack trace not defined.";
    }

    protected String getCommandFormat() {
        if (this.commandFormat == null) {
            return CommandFormat.getCommandFormatFromImpact(this.getImpact()).getCommandFormat();
        }
        return this.commandFormat.getCommandFormat();
    }

}
