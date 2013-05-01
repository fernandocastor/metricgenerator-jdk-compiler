package com.sun.tools.javac.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

public class ConcurrentElementsHandler {
    private List<String> concurrentMetricsOutput;

    public static ConcurrentElementsHandler instance = new ConcurrentElementsHandler();

    public void outputToFile() throws IOException{
        File concFile = new File("metrics_output.txt");
        concFile.createNewFile();
        BufferedWriter concWriter = new BufferedWriter(new FileWriter(concFile, true));
        for (String outputEntry: this.concurrentMetricsOutput) {
            concWriter.write(outputEntry);
            concWriter.newLine();
        }
        concWriter.close();
    }

    private ConcurrentElementsHandler() {
        this.concurrentMetricsOutput = new LinkedList<String>();
    }

    public void computeConcurrentElementMetrics(JCTree tree) {
        Map<Structure, Set<Concern>> concerns = tree.accept(
            new ConcurrentElementsVisitor(), new HashMap<Structure, Set<Concern>>());

        for (Map.Entry<Structure, Set<Concern>> elementConcern: concerns.entrySet()) {
            this.concurrentMetricsOutput.add("declaration;" + elementConcern.getKey());
            for (Concern concern: elementConcern.getValue()) {
                this.concurrentMetricsOutput.add(elementConcern.getKey().shortToString() +
                                                 ";\"" + concern.name + "\"");
            }
        }
    }
}
