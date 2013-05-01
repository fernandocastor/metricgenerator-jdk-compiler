package com.sun.tools.javac.metrics;

public class Structure {
    public String type;
    public String name;
    public int linesOfCode;
    public Structure(String type, String name, int linesOfCode) {
        this.type = type;
        this.name = name;
        this.linesOfCode = linesOfCode;
    }
    @Override
    public String toString() {
        return this.type + ';' + this.name;// + ';' + this.linesOfCode;
    }
    public String shortToString() {
        return this.type + ';' + this.name;
    }
}
