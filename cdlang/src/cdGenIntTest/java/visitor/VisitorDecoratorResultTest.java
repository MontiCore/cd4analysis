/* (c) https://github.com/MontiCore/monticore */
package visitor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import TestVisitor.*;

import java.util.*;

/**
 * Test the result of the Visitor Decorator.
 */
public class VisitorDecoratorResultTest {
  
  @Test
  public void test() {
    A a = new A();
    a.setB(newB("attr"));
    a.addManyB(newB("many1"));
    a.addManyB(newB("many2"));
    a.setOptB1(newB("opt1"));
    a.setOptB2Absent();
    a.setOneB(newB("oneB"));
    
    Map<String, Integer> counter = new HashMap<>();
    var visitor = new TestVisitorVisitorImplementation() {
      
      @Override
      public void visit(A node) {
        super.visit(node);
        counter.compute("A", (s, integer) -> integer == null ? 1 : integer + 1);
      }
      
      @Override
      public void visit(B node) {
        super.visit(node);
        counter.compute(node.getBName(), (s, integer) -> integer == null ? 1 : integer + 1);
      }
      
    };
    
    a.accept(visitor);
    
    Assertions.assertEquals(1, counter.get("A"));
    Assertions.assertEquals(1, counter.get("attr"));
    Assertions.assertEquals(1, counter.get("many1"));
    Assertions.assertEquals(1, counter.get("many2"));
    Assertions.assertEquals(1, counter.get("opt1"));
    Assertions.assertEquals(1, counter.get("oneB"));
    Assertions.assertEquals(6, counter.size());
  }
  
  @Test
  public void testBi() {
    // This test traces the visited elements
    C c = constructC();
    
    List<String> tracerC = new ArrayList<>();
    c.accept(cVisitor(tracerC));
    List<String> tracerC1 = new ArrayList<>();
    c.getManyC().iterator().next().accept(cVisitor(tracerC1));
    List<String> tracerC2 = new ArrayList<>();
    c.getOptC().accept(cVisitor(tracerC2));
    List<String> tracerC3 = new ArrayList<>();
    c.getManyManyC().iterator().next().accept(cVisitor(tracerC3));
    List<String> tracerC4 = new ArrayList<>();
    c.getManyC4().iterator().next().accept(cVisitor(tracerC4));
    List<String> tracerC5 = new ArrayList<>();
    c.getOneCAttr().accept(cVisitor(tracerC5));
    
    System.err.println(tracerC);
    System.err.println(tracerC1);
    System.err.println(tracerC2);
    System.err.println(tracerC3);
    System.err.println(tracerC4);
    System.err.println(tracerC5);
    
    Assertions.assertEquals(
        "C, C1, C, C1, C, C2, C, C3, C, C3, C, C3, C, C4, C, C4, C, C4, C, C4, C, C5", String.join(
            ", ", tracerC));
    Assertions.assertEquals(
        "C1, C, C1, C1, C, C2, C, C3, C, C3, C, C3, C, C4, C, C4, C, C4, C, C4, C, C5", String.join(
            ", ", tracerC1));
    Assertions.assertEquals(
        "C2, C, C1, C, C1, C, C2, C3, C, C3, C, C3, C, C4, C, C4, C, C4, C, C4, C, C5", String.join(
            ", ", tracerC2));
    Assertions.assertEquals(
        "C3, C, C1, C, C1, C, C2, C, C3, C3, C, C3, C, C4, C, C4, C, C4, C, C4, C, C5", String.join(
            ", ", tracerC3));
    Assertions.assertEquals(
        "C4, C, C1, C, C1, C, C2, C, C3, C, C3, C, C3, C, C4, C4, C, C4, C, C4, C, C5", String.join(
            ", ", tracerC4));
    // C5 is unidirectional
    Assertions.assertEquals("C5", String.join(", ", tracerC5));
  }
  
  protected C constructC() {
    /*
     association [0..1] C <-> (manyC) C1  [*];
     association [0..1] C <-> (optC)  C2  [0..1];
     association [*]    C <-> (manyManyC)  C3  [*];
     association        C <-> (manyC4) C4 [*];
     */
    C c = new C();
    c.addManyC(new C1());
    c.addManyC(new C1());
    c.setOptC(new C2());
    c.addManyManyC(new C3());
    c.addManyManyC(new C3());
    c.addManyManyC(new C3());
    
    c.addManyC4(new C4());
    c.addManyC4(new C4());
    c.addManyC4(new C4());
    c.addManyC4(new C4());
    
    c.setOneCAttr(new C5());
    
    return c;
  }
  
  // A visitor that writes each (start) visit call to the list
  protected ITestVisitorVisitor cVisitor(List<String> tracer) {
    return new TestVisitorVisitorImplementation() {
      
      @Override
      public void visit(A node) {
        count(node);
        super.visit(node);
      }
      
      @Override
      public void visit(C node) {
        count(node);
        super.visit(node);
      }
      
      @Override
      public void visit(C1 node) {
        count(node);
        super.visit(node);
      }
      
      @Override
      public void visit(C2 node) {
        count(node);
        super.visit(node);
      }
      
      @Override
      public void visit(C3 node) {
        count(node);
        super.visit(node);
      }
      
      @Override
      public void visit(C4 node) {
        count(node);
        super.visit(node);
      }
      
      @Override
      public void visit(C5 node) {
        count(node);
        super.visit(node);
      }
      
      void count(Object o) {
        tracer.add(o.getClass().getSimpleName());
      }
      
    };
  }
  
  protected B newB(String name) {
    B b = new B();
    b.setBName(name);
    return b;
  }
  
}
