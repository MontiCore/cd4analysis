package de.monticore.sydiff2semdiff.cg2graph.metamodel;

import com.google.common.graph.MutableValueGraph;
import java.util.Deque;
import java.util.Map;

enum genKind {
  ALL, NO_INHERITANCE
}

public class IntermediateGraph {
  public Deque<Map<Object,genKind>> classStack;
  public Deque<Map<Object,genKind>> relationStack;
  MutableValueGraph<Map<String, Object>, String> graph;
}
