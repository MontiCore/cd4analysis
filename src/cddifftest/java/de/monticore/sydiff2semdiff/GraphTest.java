package de.monticore.sydiff2semdiff;

import de.monticore.sydiff2semdiff.cg2graph.metamodel.RelationshipEdge;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.junit.Test;

import java.util.*;

public class GraphTest {

  @Test
  public void createDefaultDirectedGraph() {
    Graph<Map<String, Object>, RelationshipEdge> g = new DefaultDirectedGraph<>(RelationshipEdge.class);

    HashSet<Object> diffClassSet1 = new HashSet<>();
    diffClassSet1.add("Person");
    diffClassSet1.add("Employee");
    diffClassSet1.add("Manager");

    HashSet<Object> diffClassSet2 = new HashSet<>();
    diffClassSet2.add("Person");
    diffClassSet2.add("Employee");

    HashSet<Object> diffClassSet3 = new HashSet<>();
    diffClassSet3.add("Task");

    Map<String, Object> diffObject_Employee1 = new TreeMap<>();
    diffObject_Employee1.put("entity", "Employee1");
    diffObject_Employee1.put("class", "Employee");
    diffObject_Employee1.put("diffClassSet", diffClassSet2);

    Map<String, Object> diffObject_Manager1 = new TreeMap<>();
    diffObject_Manager1.put("entity", "Manager1");
    diffObject_Manager1.put("class", "Mangaer");
    diffObject_Manager1.put("diffClassSet", diffClassSet1);

    Map<String, Object> diffObject_Task1 = new TreeMap<>();
    diffObject_Task1.put("entity", "Task1");
    diffObject_Task1.put("class", "Task");
    diffObject_Task1.put("diffClassSet", diffClassSet3);

    Map<String, Object> diffObject_Task2 = new TreeMap<>();
    diffObject_Task2.put("entity", "Task2");
    diffObject_Task2.put("class", "Task");
    diffObject_Task2.put("Task2", diffClassSet3);

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
    System.out.println("edge: " + g.edgesOf(diffObject_Employee1));
  }
}
