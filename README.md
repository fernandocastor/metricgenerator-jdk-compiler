Metric-generator Java compiler based on OpenJDK 6.
============================================

What is it? What was this made for?
--------------------------------------------
A Java compiler built upon the code of OpenJDK 1.6 with the addition of a module which generates metrics for the usage of concurrent programming constructs. It was written to help me with a research done about modularity of Java concurrent constructs in some software.

Just the *langtools* module of the OpenJDK code was brought here, which includes the *javac* compiler. It's basically this module with some code added.

There is also included a metrics calculator, written in Python, which reads the metric entries generated by the compiler and calculate the actual metrics.


Build
--------------------------------------------
Required:

* JDK 1.6 (not recommended later versions)
* Ant 1.6.5 or later

Follow this instructions:

1. Change `boot.java.home` value in the `make/build.properties` file to the path of your JDK 6.
2. Go to the `make/` directory. Run `make` if provided by your system, otherwise run `ant`.


Usage
--------------------------------------------
Build some Java program using this compiler. Be sure that the `java` command is in your system PATH.

* If using Linux, give permissions to `javac` script included here with `chmod +x javac` and run this script as an a normal *javac* executable. 
* Windows not supported yet.
* Not tested in MacOSX.

Compatible with build frameworks like Ant and Maven. Just set the compiler executable to the custom `javac` script in its settings files (like `build.xml` or `pom.xml`). If the `JAVA_HOME` environment variable is needed, point it to a standard OpenJDK 6 directory.

If just one output file was generated, just copy it to a file named `all_metrics_output.txt`. If multiple files from multiple modules were generated, merge them to an unique file. I do this with a *bash* command:

```bash
cat $(find . | grep "/metrics_output.txt") | sort -u > all_metrics_output.txt
```

Then run the `metrics_calculator.py` script to calculate the metrics (Python 2.6 or later needed). Go to the directory containing the `all_metrics_output.txt` file and run the script. It will generate a `metrics.txt` output file.

Expected results
--------------------------------------------
For each *javac* call there must be a `metrics_output.txt` file generated with some declaration list collected from your built software. This file will usually be placed in the root directory of its source.


Original README
--------------------------------------------
Check the README file.