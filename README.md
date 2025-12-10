# GadgetBuilder 

<figure> <img src="gadgetbuilder.png"   alt="Gadget Builder Logo image" width="300"></figure> 

A new Java Deserialization Gadget Chain payload generator. **GadgetBuilder** is an overhaul of [Ysoserial](https://github.com/frohoff/ysoserial) with:

- A new developer-friendly API
- New gadget chains which have been discovered since Ysoserial's last update in 2020
- A construction approach to create gadget chains from a **Trampoline Gadget** + **Main Gadget Chain** + **Sink Method Adapters**. This leads to more chain varieties and fosters code reuse for further gadget chain additions.

For more information, we refer to the reference publication.

## Contents

1. [Usage](#usage)
2. [Build Instructions](#build-instructions)
3. [Contributing](#contributing)
4. [Using GadgetBuilder as a gadget chain benchmark for gc detectors](#using-gadgetbuilder-as-a-gadget-chain-benchmark-for-gc-detectors)
5. [Referencing this Work](#referencing-this-work)
6. [Troubleshooting and FAQs](#troubleshooting-and-faqs)

## Usage

Get the latest gadgetbuilder version from the releases page.

### Getting started

```bash
java -jar gadgetbuilder.jar --help

# list all available gadget chains and trampolines
java -jar gadgetbuilder.jar -l

# get detailed information about a single chain
java -jar gadgetbuilder.jar -g <chainName> --help
```

### Simple Usage Example

If you are overwhelmed by the fragment construction (trampoline + chain + sinkadapter), do not worry! We set default values ([here](gadgetbuilder-client/src/main/java/org/ses/gadgetbuilder/client/ConfigUtil.java)) for trampolines and adapters, so you can run without configuring those to start with.

```bash
java -jar gadgetbuilder.jar -g <chainName> -c <command>

java -jar gadgetbuilder.jar -g Clojure1 -c "touch proof.txt" --b64
# run with the --test option to see if the payload executes on your machine
java -jar gadgetbuilder.jar -g Clojure1 -c "touch proof.txt" --test
```


### Output options

- ``--b64``: output serialized payload in base64
- ``-o <fileName>``: write binary output into file

## Build Instructions

Make sure you are using [JDK 8](https://www.oracle.com/java/technologies/javase/javase8u211-later-archive-downloads.html) for building. If you want to very sure - we used JDK8u381.

```bash
# set JAVA_HOME env variable if necessary. Points to the home dir, not the java binary.
export JAVA_HOME=/path/to/JDK_8
mvn clean package
```

## Contributing

In general, same as [Ysoserial](https://github.com/frohoff/ysoserial/tree/master?tab=readme-ov-file#contributing):

1. Fork it
2. Create your feature branch (``git checkout -b my-new-gadgetchain``)
3. Commit your changes (``git commit -am 'Add gadget chain'``)
4. Push to the branch (``git push origin my-new-gadgetchain``)
5. Create new Pull Request

### Contributing new Gadget Chains

Create a new class extending ``GadgetChain<Trampoline>``, ``MethodInvokeGadgetChain<Trampoline,MethodInvokeAdapter>`` or ``InstantiateGadgetChain<Trampoline,InitializeAdapter>`` in the main **chains1** module if:

1. it creates no dependency conflict with any of the other gadget chains in the module.
2. it is a gadget chain to a more recent dependency version. In this case rotate the old gadget chains to the legacy module **chains2** or if needed further down to **chains3** (create modules as necessary).

Otherwise add it to the legacy modules. For implementation examples, check out the following gadget chains:

- A gadget chain with no trampoline: [C3P0](gadgetbuilder-chains1/src/main/java/org/ses/gadgetbuilder/chains1/C3P0.java) - notice the usage of ``GadgetBuilder<NoTrampoline>``
- A gadget chain using a trampoline: [Beanshell1](https://github.com/brunok-cs/gadgetbuilder/blob/main/gadgetbuilder-chains1/src/main/java/org/ses/gadgetbuilder/chains1/BeanShell1.java) - notice the return value ``TrampolineConnector``. Supply parameters to the trampoline connector according to the trampoline method call, e.g.:
  - ``toString()``: the trampoline only needs to know on which object toString needs to be invoked, so ``return new TrampolineConnector(payload)``
  - ``Comparator.compare(Object a, Object b)``: ``return new TrampolineConnector(comparator, a, b)``.
- A gadget chain with a MethodInvokeAdapter: [CommonsBeanutils1](gadgetbuilder-chains1/src/main/java/org/ses/gadgetbuilder/chains1/CommonsBeanutils1.java) - use ``MethodInvokeGadgetChain<CompareTrampoline, GetterMethodInvokeAdapter>`` if the invocation target can only be an arbitrary getter, otherwise ``MethodInvokeGadgetChain<CompareTrampoline, MethodInvokeAdapter>``
- A gadget chain with an InstantiateAdapter (constructor sink): [CommonsCollections3](gadgetbuilder-chains1/src/main/java/org/ses/gadgetbuilder/chains1/CommonsCollections3.java)


### Contributing Trampolines

In general, we would consider a trampoline any sub-gadget-chain leading to a highly polymorphic method call within the Java Class Library. For instance: ``Object.hashCode``, ``Object.toString`` or ``Map.get``. The main trampoline types are defined in the gadgetbuilder-api, [here](gadgetbuilder-api/src/main/java/org/ses/gadgetbuilder/chains/trampolines). If a trampoline you discovered does not relate to any of the trampoline types defined in the API, you can add the new trampoline type to the API as part of the pull request. E.g., you find a trampoline path to ``Runnable.run``, first add the interface ``RunnableTrampoline`` in the [noparam](gadgetbuilder-api/src/main/java/org/ses/gadgetbuilder/chains/trampolines/noparam) directory.

Then, add the trampoline implementation in the [gadgetbuilder-impl](gadgetbuilder-impl/src/main/java/org/ses/gadgetbuilder/impl/trampolines) module. Check out the examples in the packages. Try to write the trampolines such that upon wrapping the payload object for the main gadget chain, they do not invoke that chain. This is often achieved using Java Reflection after the trampoline gadget was constructed, to add the payload to the respective properties.

### Contributing Sink Adapters

This is mostly analogous to contributing trampolines. If the adapter type does not exist in the [API](gadgetbuilder-api/src/main/java/org/ses/gadgetbuilder/adapters), add it there. Then place the new sink adapter implementation in the *impl* module in the [adapters package](gadgetbuilder-impl/src/main/java/org/ses/gadgetbuilder/impl/adapters)


## Using GadgetBuilder as a gadget chain benchmark for gc detectors

In line with *Ysoserial*, we list the dependencies for which the gadget chains are know to exist with the ``@Dependencies`` annotation. The respective gadget chains are in the chain release modules ``gadgetbuilder-chains<x>/src/main/java/org/ses/gadgetbuilder/chains<x>``, for instance:

- [Chains1](gadgetbuilder-chains1/src/main/java/org/ses/gadgetbuilder/chains1)
- [Chains2](gadgetbuilder-chains1/src/main/java/org/ses/gadgetbuilder/chains2)

The chains come with a ``getStackTrace()`` method, showing the gadget chain one would to confirm from the gc detector output. You can also get all this information from the CLI with:

```bash
# Example gadget chain
java -jar gadgetbuilder.jar -g CommonsBeanutils1 -h

Name:   CommonsBeanutils1
Dependencies:   [commons-beanutils:commons-beanutils:1.11.0]
Authors:        [frohoff, k4n5ha0]
Impact: Method.invoke()
Trampoline Type:        CompareTrampoline
Available Trampolines:  CC4Compare,ConcurrentSkipListMapCompare,PriorityQueueCompare
Variation count:        12
Adapter:        GetterMethodInvokeAdapter
Available Adapters:     JdbcRowSetMethodInvokeAdapter,PostgresqlMethodInvokeAdapter,TemplatesImplMethodInvokeAdapter,URLMethodInvokeAdapter
Usage Example:  java -jar gadgetbuilder.jar -g CommonsBeanutils1 -c "Command format depends on sink adapter" -t CC4Compare -a JdbcRowSetMethodInvokeAdapter
Chain:
------------------
org.apache.commons.beanutils.BeanComparator.compare()
org.apache.commons.beanutils.PropertyUtils.getProperty()
org.apache.commons.beanutils.PropertyUtilsBean.getProperty()
org.apache.commons.beanutils.PropertyUtilsBean.getNestedProperty()
org.apache.commons.beanutils.PropertyUtilsBean.getSimpleProperty()
org.apache.commons.beanutils.PropertyUtilsBean.invokeMethod()
java.lang.reflect.Method.invoke()
------------------


```

## Referencing this Work

Credit where credit is due: we insist on referencing this work in combination with the original Ysoserial repository.

### Ysoserial Citation

```
@article{frohoffysoserial,
  title={ysoserial--A proof-of-concept tool for generating payloads that exploit unsafe Java object deserialization, 2016},
  author={Frohoff, Chris and Lawrence, G},
  url={URL: https://github.com/frohoff/ysoserial}
}
```

> Frohoff, C., & Lawrence, G. ysoserial–A proof-of-concept tool for generating payloads that exploit unsafe Java object deserialization, 2016. URL: https://github.com/frohoff/ysoserial.

### GadgetBuilder Citation

Open access PDF: [nordsec2025-gadgetBuilder.pdf](https://www.abartel.net/static/p/nordsec2025-gadgetBuilder.pdf)

To keep things clean, we removed the evaluation data from this repository. If you need to access it, it is still available in the anonymous artifact source zip on [FigShare](https://figshare.com/s/ea18b680a4790356d304).

```
@inproceedings{kreyssig2025gadgetBuilder,
  title = {GadgetBuilder: An Overhaul of the Greatest Java Deserialization Exploitation Tool},
  author = {Kreyssig, Bruno and Houy, Sabine and Zhang, Hantang and Riom, Timothée and Bartel, Alexandre},
  booktitle = {{30th Nordic Conference on Secure IT Systems (NordSec'25)}},
  year = {2025},
  doi = {}
}
```


### Further Sources and Contributions

- [New Exploit Technique In Java Deserialization Attack](https://www.blackhat.com/eu-19/briefings/schedule/#new-exploit-technique-in-java-deserialization-attack-17321): **JXPath**, **Clojure2**, **HTMLParser**
- [Bofei Chen](https://github.com/BofeiC/JDD-PocLearning): **Vaadin2**, **Groovy2**, **Hibernate2** and **UIDeafultsToStringTrampoline**
- [Ben Lincoln](https://github.com/BishopFox/ysoserial-bf/tree/master): **MozillaRhino3**, **Jython2**, **Jython3**
- [new-gadgets](https://github.com/frohoff/ysoserial/tree/newgadgets): **Scala1**, **Scala2**, **Atomikos**, **SpringJTA**, **CommonsCollections8**, **Ceylon**,  (*CommonsBeanutils* without CC dependency)
- [Wildfly Pull request](https://github.com/frohoff/ysoserial/pull/177): **Wildfly1**
- [Alternatives to TemplatesImpl gadget](https://mogwailabs.de/en/blog/2023/04/look-mama-no-templatesimpl/)

## Troubleshooting and FAQs

### I am getting an Exception: ``module does not "opens ..." to unnamed module @6d13a606``

This is because since JDK 16, Java strongly encapsulates its internals as a part of project JigSaw. You can either run gadgetbuilder with an older JDK (e.g., 8, 11) or refer to the original [Ysoserial issue](https://github.com/frohoff/ysoserial/issues/203).

An easy workaround is to create an environment variable with all add-opens:

```bash
export ADDOPENS="--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED --add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.runtime=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.management/javax.management=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED --add-opens=java.sql.rowset/com.sun.rowset=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/sun.reflect.annotation=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport.tcp=ALL-UNNAMED"
```

which you can then use in all further executions:

```bash
java $ADDOPENS -jar gadgetbuilder.jar <...>
```





