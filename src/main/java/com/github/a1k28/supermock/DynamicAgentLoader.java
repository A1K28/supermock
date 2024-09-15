package com.github.a1k28.supermock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;

public class DynamicAgentLoader {

    public static void loadAgent() {
        try {
            // Path to the agent JAR inside your fat JAR
            String agentPath = "/libs/dynamic-interceptor-agent-1.0-SNAPSHOT.jar";
            
            // Extract the agent JAR to a temporary file
            File tempFile = extractAgent(agentPath);
            
            // Get the pid of the current Java process
            String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
            String pid = nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));

            // Load the agent
            com.sun.tools.attach.VirtualMachine vm = com.sun.tools.attach.VirtualMachine.attach(pid);
            vm.loadAgent(tempFile.getAbsolutePath());
            vm.detach();
            
            // Optionally, delete the temp file when the JVM exits
            tempFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static File extractAgent(String agentPath) throws Exception {
        URL url = DynamicAgentLoader.class.getResource(agentPath);
        if (url == null) {
            throw new RuntimeException("Agent JAR not found: " + agentPath);
        }
        
        InputStream in = url.openStream();
        File tempFile = File.createTempFile("dynamic-interceptor-agent", ".jar");
        OutputStream out = new FileOutputStream(tempFile);
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        
        in.close();
        out.close();

        return tempFile;
    }
}