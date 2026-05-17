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
    
    Assertions.assertEquals(Arrays.stream(
        "C, C1, C, C1, C, C2, C, C3, C, C3, C, C3, C, C4, C, C4, C, C4, C, C4, C5, C".split(","))
        .map(String::trim).toList(), tracerC);
    Assertions.assertEquals(Arrays.stream(
        "C1, C, C1, C, C2, C, C3, C, C3, C, C3, C, C4, C, C4, C, C4, C, C4, C5, C, C1".split(","))
        .map(String::trim).toList(), tracerC1);
    
    // TODO: Test C2...C5
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
  
  protected ITestVisitorVisitor cVisitor(List<String> tracer) {
    return new TestVisitorVisitorImplementation() {
      
      @Override
      public void visit(A node) {
        super.visit(node);
        count(node);
      }
      
      @Override
      public void visit(C node) {
        super.visit(node);
        count(node);
      }
      
      @Override
      public void visit(C1 node) {
        super.visit(node);
        count(node);
      }
      
      @Override
      public void visit(C2 node) {
        super.visit(node);
        count(node);
      }
      
      @Override
      public void visit(C3 node) {
        super.visit(node);
        count(node);
      }
      
      @Override
      public void visit(C4 node) {
        super.visit(node);
        count(node);
      }
      
      @Override
      public void visit(C5 node) {
        super.visit(node);
        count(node);
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
