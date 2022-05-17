package de.monticore.sydiff2semdiff.cg2graph.metamodel;

import org.jgrapht.Graph;
import java.util.Deque;
import java.util.Map;

enum genKind {
  ALL, NO_INHERITANCE
}

public class IntermediateGraph {
  public Deque<Map<Object,genKind>> classStack;
  public Deque<Map<Object,genKind>> relationStack;
  public Graph<Map<String, Object>, RelationshipEdge> graph;
}
