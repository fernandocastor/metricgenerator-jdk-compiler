package com.sun.tools.javac.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import javax.lang.model.element.ElementKind;
import com.sun.source.util.TreeScanner;
import com.sun.source.tree.*;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;

abstract class GenericVisitor<T> extends TreeScanner<T, T> {
    @Override
    public T reduce(T obj1, T obj2) {
        return (obj1 != null) ? obj1 : obj2;
    }
}

public class ConcurrentElementsVisitor extends GenericVisitor<Map<Structure, Set<Concern>>> {

    private int countLines(String code) {
        int lineBreaks = 0;
        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == '\n') {
                lineBreaks++;
            }
        }
        return lineBreaks;
    }

    @Override
    public Map<Structure, Set<Concern>> visitClass(ClassTree node,
                                                   Map<Structure, Set<Concern>> map) {
        JCTree.JCClassDecl tree = (JCTree.JCClassDecl) node;
        if (tree.sym.name.len > 0) { //not an anonymous class

            Set<Concern> concernsOfClass = tree.accept(new BodyVisitor(),
                                                       new HashSet<Concern>());
            String type = tree.sym.owner.getKind() == ElementKind.PACKAGE ? "class"
                                                                          : "innerclass";
            map.put(
                new Structure(type, tree.sym.toString(), this.countLines(tree.toString())),
                concernsOfClass != null ? concernsOfClass : new HashSet<Concern>()
            );

            for (JCTree member: tree.getMembers()) {
                if (member instanceof JCTree.JCMethodDecl) {
                    JCTree.JCMethodDecl meth = (JCTree.JCMethodDecl) member;
                    Set<Concern> concernsOfMethod = meth.accept(new BodyVisitor(),
                                                                new HashSet<Concern>());
                    map.put(
                        new Structure("method", tree.sym.toString()+'.'+meth.sym.toString(),
                                      this.countLines(meth.toString())),
                        concernsOfMethod != null ? concernsOfMethod : new HashSet<Concern>()
                    );
                }
            }

        }

        super.visitClass(node, map);
        return map;
    }

}

class BodyVisitor extends GenericVisitor<Set<Concern>> {

    private String fullTypeName(JCTree tree) {
        Type type = tree.type;
        if (type != null) {
            String name = type.toString();
            int delimiterIndex = (name.indexOf('<') > -1) ? name.indexOf('<')
                                                          : name.length();
            return name.substring(0, delimiterIndex);
        } else {
            return "";
        }
    }

    private String methodName(JCTree meth) {
        String name = meth.toString();
        int delimiter1Index = (name.indexOf('(') > -1) ? name.indexOf('(')
                                                       : name.length();
        name = name.substring(0, delimiter1Index);
        int delimiter2Index = (name.lastIndexOf('.') > -1) ? name.lastIndexOf('.')+1
                                                           : 0;
        return name.substring(delimiter2Index);
    }

    private Set<Concern> findConcern(Tree node, Set<Concern> set) {
        JCTree tree = (JCTree) node;
        String fullTypeName = this.fullTypeName(tree);

        Concern interfaceConcern = ConcurrentElements.interfaces.get(fullTypeName);
        if (interfaceConcern != null) {
            set.add(interfaceConcern);
        } else {
            Concern classConcern = ConcurrentElements.classes.get(fullTypeName);
            if (classConcern != null) {
                set.add(classConcern);
            }
        }

        if (tree instanceof JCTree.JCMethodInvocation) {
            JCTree.JCMethodInvocation meth = (JCTree.JCMethodInvocation) tree;
            List<ConcurrentElements.ArgType> types = new ArrayList<ConcurrentElements.ArgType>();
            for (JCTree arg: meth.args) {
                types.add(new ConcurrentElements.ArgType(this.fullTypeName(arg)));
            }
            Concern methodConcern = ConcurrentElements.methods.get(
                    new ConcurrentElements.Method(this.methodName(meth), types));
            if (methodConcern != null) {
                set.add(methodConcern);
            }
        }

        return set;
    }

    @Override
    public Set<Concern> scan(Tree node, Set<Concern> set) {
        return (node != null)
                ? node.accept(this, this.findConcern(node, set))
                : set;
    }

    @Override
    public Set<Concern> visitModifiers(ModifiersTree node, Set<Concern> set) {
        JCTree.JCModifiers tree = (JCTree.JCModifiers) node;
        if (Flags.toString(tree.flags).contains("synchronized")) {
            set.add(Concern.MUTEX);
        }
        super.visitModifiers(node, set);
        return set;
    }

    @Override
    public Set<Concern> visitSynchronized(SynchronizedTree node, Set<Concern> set) {
        set.add(Concern.MUTEX);
        super.visitSynchronized(node, set);
        return set;
    }

}
