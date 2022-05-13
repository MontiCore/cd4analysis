package de.monticore.sydiff2semdiff;

import de.monticore.sydiff2semdiff.cg2graph.RelationshipEdge;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;
import org.junit.Test;

import java.io.*;
import java.net.*;
import java.util.*;

public class GraphTest {

  @Test
  public void createDefaultDirectedGraph() {
    Graph<Map<Object, HashSet<Object>>, RelationshipEdge> g = new DefaultDirectedGraph<>(RelationshipEdge.class);

    HashSet<Object> diffClass1 = new HashSet<>();
    diffClass1.add("Person");
    diffClass1.add("Employee");
    diffClass1.add("Manager");

    HashSet<Object> diffClass2 = new HashSet<>();
    diffClass2.add("Person");
    diffClass2.add("Employee");

    HashSet<Object> diffClass3 = new HashSet<>();
    diffClass3.add("Task");

    Map<Object, HashSet<Object>> diffObject_Employee1 = new HashMap<>();
    diffObject_Employee1.put("Employee1", diffClass2);

    Map<Object, HashSet<Object>> diffObject_Manager1 = new HashMap<>();
    diffObject_Manager1.put("Manager1", diffClass1);

    Map<Object, HashSet<Object>> diffObject_Task1 = new HashMap<>();
    diffObject_Task1.put("Task1", diffClass3);

    Map<Object, HashSet<Object>> diffObject_Task2 = new HashMap<>();
    diffObject_Task2.put("Task2", diffClass3);

    // add the vertices
    g.addVertex(diffObject_Employee1);
    g.addVertex(diffObject_Manager1);
    g.addVertex(diffObject_Task1);
    g.addVertex(diffObject_Task2);

    // add edges to create a circuit
    g.addEdge(diffObject_Employee1,diffObject_Task1, new RelationshipEdge("task"));
    g.addEdge(diffObject_Task1,diffObject_Employee1, new RelationshipEdge("person"));

    g.addEdge(diffObject_Manager1,diffObject_Task2, new RelationshipEdge("task"));
    g.addEdge(diffObject_Task2,diffObject_Manager1, new RelationshipEdge("person"));

    System.out.println("-- toString output");
    System.out.println(g.toString());
    System.out.println();
  }
}
