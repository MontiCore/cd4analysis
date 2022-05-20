package de.monticore.sydiff2semdiff;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import de.monticore.sydiff2semdiff.cg2graph.metamodel.RelationshipEdge;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class GuavaGraphTest {

  @Test
  public void createDefaultDirectedGraph() {
    MutableValueGraph<Map<String, Object>, String> graph = ValueGraphBuilder
      .directed()
      .allowsSelfLoops(true)
      .build();

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
    graph.addNode(diffObject_Employee1);
    graph.addNode(diffObject_Manager1);
    graph.addNode(diffObject_Task1);
    graph.addNode(diffObject_Task2);

    // add edges to create a circuit
    graph.putEdgeValue(diffObject_Employee1,diffObject_Task1, "task");
    graph.putEdgeValue(diffObject_Task1,diffObject_Employee1, "person");

    graph.putEdgeValue(diffObject_Manager1,diffObject_Task2, "task");
    graph.putEdgeValue(diffObject_Task2,diffObject_Manager1, "person");

    System.out.println("-- toString output");
    System.out.println(graph.toString());
    System.out.println();
  }
}
