/* (c) https://github.com/MontiCore/monticore */
package TestVisitor;

import org.junit.jupiter.api.Assertions;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Visitor implements ITestVisitorVisitor {
  
  /**
   * Because we deal with an interface, we cannot save the already traversed elements in it.
   * Therefore, we need to overwrite the getTraversedElements method and handle the traversed
   * elements on the Visitor side.
   */
  Set<Object> traversedElements = new HashSet<>();
  
  /**
   * same as for traversedElements
   */
  @Override
  public Set<Object> getTraversedElements() { return traversedElements; }
  
  public int countVisitClassWithMap = 0;
  public int countEndVisitClassWithMap = 0;
  public int countVisitClassCircular1 = 0;
  public int countEndVisitClassCircular1 = 0;
  public int countVisitClassCircular2 = 0;
  public int countEndVisitClassCircular2 = 0;
  public int countVisitClassWith2DimList = 0;
  public int countEndVisitClassWith2DimList = 0;
  public int countVisitClassWith2DimOptional = 0;
  public int countEndVisitClassWith2DimOptional = 0;
  public int countVisitClassWith2DimSet = 0;
  public int countEndVisitClassWith2DimSet = 0;
  public int countVisitClassWith2DimMap = 0;
  public int countEndVisitClassWith2DimMap = 0;
  public int countVisitClassWith3DimArray = 0;
  public int countEndVisitClassWith3DimArray = 0;
  public int countVisitClassWithArray = 0;
  public int countEndVisitClassWithArray = 0;
  public int countVisitClassWithAssociation = 0;
  public int countEndVisitClassWithAssociation = 0;
  public int countVisitClassWithComposition = 0;
  public int countEndVisitClassWithComposition = 0;
  public int countVisitClassWithList = 0;
  public int countEndVisitClassWithList = 0;
  public int countEndVisitClassWithOptional = 0;
  public int countVisitClassWithOptional = 0;
  public int countEndVisitClassWithPojoClassType = 0;
  public int countVisitClassWithPojoClassType = 0;
  public int countEndVisitClassWithPrimitiveType = 0;
  public int countVisitClassWithPrimitiveType = 0;
  public int countEndVisitClassWithSet = 0;
  public int countVisitClassWithSet = 0;
  public int countEndVisitClassWithString = 0;
  public int countVisitClassWithString = 0;
  public int countVisitAllTogether = 0;
  public int countEndVisitAllTogether = 0;
  public int countVisitB = 0;
  public int countEndVisitB = 0;
  public int countVisitClassToBeTopped = 0;
  public int countEndVisitClassToBeTopped = 0;
  
  Stack<ClassWithMap> stackClassWithMap = new Stack<>();
  Stack<ClassCircular1> stackClassCircular1 = new Stack<>();
  Stack<ClassCircular2> stackClassCircular2 = new Stack<>();
  Stack<ClassWith2DimList> stackClassWith2DimList = new Stack<>();
  Stack<ClassWith2DimOptional> stackClassWith2DimOptional = new Stack<>();
  Stack<ClassWith2DimSet> stackClassWith2DimSet = new Stack<>();
  Stack<ClassWith2DimMap> stackClassWith2DimMap = new Stack<>();
  Stack<ClassWith3DimArray> stackClassWith3DimArray = new Stack<>();
  Stack<ClassWithArray> stackClassWithArray = new Stack<>();
  Stack<ClassWithAssociation> stackClassWithAssociation = new Stack<>();
  Stack<ClassWithComposition> stackClassWithComposition = new Stack<>();
  Stack<ClassWithList> stackClassWithList = new Stack<>();
  Stack<ClassWithOptional> stackClassWithOptional = new Stack<>();
  Stack<ClassWithPojoClassType> stackClassWithPojoClassType = new Stack<>();
  Stack<ClassWithPrimitiveType> stackClassWithPrimitiveType = new Stack<>();
  Stack<ClassWithSet> stackClassWithSet = new Stack<>();
  Stack<ClassWithString> stackClassWithString = new Stack<>();
  Stack<AllTogether> stackAllTogether = new Stack<>();
  Stack<B> stackB = new Stack<>();
  Stack<ClassToBeTopped> stackClassToBeTopped = new Stack<>();
  
  @Override
  public void visit(ClassWith2DimMap node) {
    countVisitClassWith2DimMap++;
    stackClassWith2DimMap.push(node);
  }
  
  @Override
  public void endVisit(ClassWith2DimMap node) {
    countEndVisitClassWith2DimMap++;
    if (stackClassWith2DimMap.empty() || stackClassWith2DimMap.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWith2DimMap.pop();
    }
  }
  
  @Override
  public void visit(B node) {
    countVisitB++;
    stackB.push(node);
  }
  
  @Override
  public void endVisit(B node) {
    countEndVisitB++;
    if (stackB.empty() || stackB.peek() != node) {
      Assertions.fail();
    }
    else {
      stackB.pop();
    }
  }
  
  @Override
  public void endVisit(ClassWithMap node) {
    countEndVisitClassWithMap++;
    if (stackClassWithMap.empty() || stackClassWithMap.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithMap.pop();
    }
  }
  
  @Override
  public void visit(ClassWithMap node) {
    countVisitClassWithMap++;
    stackClassWithMap.push(node);
  }
  
  @Override
  public void endVisit(ClassWithString node) {
    countEndVisitClassWithString++;
    if (stackClassWithString.empty() || stackClassWithString.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithString.pop();
    }
  }
  
  @Override
  public void visit(ClassWithString node) {
    countVisitClassWithString++;
    stackClassWithString.push(node);
  }
  
  @Override
  public void endVisit(ClassWith3DimArray node) {
    countEndVisitClassWith3DimArray++;
    if (stackClassWith3DimArray.empty() || stackClassWith3DimArray.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWith3DimArray.pop();
    }
  }
  
  @Override
  public void visit(ClassWith3DimArray node) {
    countVisitClassWith3DimArray++;
    stackClassWith3DimArray.push(node);
  }
  
  @Override
  public void endVisit(ClassWithArray node) {
    countEndVisitClassWithArray++;
    if (stackClassWithArray.empty() || stackClassWithArray.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithArray.pop();
    }
  }
  
  @Override
  public void visit(ClassWithArray node) {
    countVisitClassWithArray++;
    stackClassWithArray.push(node);
  }
  
  @Override
  public void endVisit(ClassWithComposition node) {
    countEndVisitClassWithComposition++;
    if (stackClassWithComposition.empty() || stackClassWithComposition.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithComposition.pop();
    }
  }
  
  @Override
  public void visit(ClassWithComposition node) {
    countVisitClassWithComposition++;
    stackClassWithComposition.push(node);
  }
  
  @Override
  public void endVisit(ClassWithAssociation node) {
    countEndVisitClassWithAssociation++;
    if (stackClassWithAssociation.empty() || stackClassWithAssociation.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithAssociation.pop();
    }
  }
  
  @Override
  public void visit(ClassWithAssociation node) {
    countVisitClassWithAssociation++;
    stackClassWithAssociation.push(node);
  }
  
  @Override
  public void endVisit(ClassCircular2 node) {
    countEndVisitClassCircular2++;
    if (stackClassCircular2.empty() || stackClassCircular2.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassCircular2.pop();
    }
  }
  
  @Override
  public void visit(ClassCircular2 node) {
    countVisitClassCircular2++;
    stackClassCircular2.push(node);
  }
  
  @Override
  public void endVisit(ClassCircular1 node) {
    countEndVisitClassCircular1++;
    if (stackClassCircular1.empty() || stackClassCircular1.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassCircular1.pop();
    }
  }
  
  @Override
  public void visit(ClassCircular1 node) {
    countVisitClassCircular1++;
    stackClassCircular1.push(node);
  }
  
  @Override
  public void endVisit(ClassWithList node) {
    countEndVisitClassWithList++;
    if (stackClassWithList.empty() || stackClassWithList.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithList.pop();
    }
  }
  
  @Override
  public void visit(ClassWithList node) {
    countVisitClassWithList++;
    stackClassWithList.push(node);
  }
  
  @Override
  public void endVisit(ClassWithSet node) {
    countEndVisitClassWithSet++;
    if (stackClassWithSet.empty() || stackClassWithSet.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithSet.pop();
    }
  }
  
  @Override
  public void visit(ClassWithSet node) {
    countVisitClassWithSet++;
    stackClassWithSet.push(node);
  }
  
  @Override
  public void endVisit(ClassWithPrimitiveType node) {
    countEndVisitClassWithPrimitiveType++;
    if (stackClassWithPrimitiveType.empty() || stackClassWithPrimitiveType.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithPrimitiveType.pop();
    }
  }
  
  @Override
  public void visit(ClassWithPrimitiveType node) {
    countVisitClassWithPrimitiveType++;
    stackClassWithPrimitiveType.push(node);
  }
  
  @Override
  public void endVisit(ClassWithPojoClassType node) {
    countEndVisitClassWithPojoClassType++;
    if (stackClassWithPojoClassType.empty() || stackClassWithPojoClassType.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithPojoClassType.pop();
    }
  }
  
  @Override
  public void visit(ClassWithPojoClassType node) {
    countVisitClassWithPojoClassType++;
    stackClassWithPojoClassType.push(node);
  }
  
  @Override
  public void endVisit(ClassWith2DimOptional node) {
    countEndVisitClassWith2DimOptional++;
    if (stackClassWith2DimOptional.empty() || stackClassWith2DimOptional.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWith2DimOptional.pop();
    }
  }
  
  @Override
  public void visit(ClassWith2DimOptional node) {
    countVisitClassWith2DimOptional++;
    stackClassWith2DimOptional.push(node);
  }
  
  @Override
  public void endVisit(ClassWithOptional node) {
    countEndVisitClassWithOptional++;
    if (stackClassWithOptional.empty() || stackClassWithOptional.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWithOptional.pop();
    }
  }
  
  @Override
  public void visit(ClassWithOptional node) {
    countVisitClassWithOptional++;
    stackClassWithOptional.push(node);
  }
  
  @Override
  public void endVisit(ClassWith2DimSet node) {
    countEndVisitClassWith2DimSet++;
    if (stackClassWith2DimSet.empty() || stackClassWith2DimSet.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWith2DimSet.pop();
    }
  }
  
  @Override
  public void visit(ClassWith2DimSet node) {
    countVisitClassWith2DimSet++;
    stackClassWith2DimSet.push(node);
  }
  
  @Override
  public void endVisit(ClassWith2DimList node) {
    countEndVisitClassWith2DimList++;
    if (stackClassWith2DimList.empty() || stackClassWith2DimList.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassWith2DimList.pop();
    }
  }
  
  @Override
  public void visit(ClassWith2DimList node) {
    countVisitClassWith2DimList++;
    stackClassWith2DimList.push(node);
  }
  
  @Override
  public void endVisit(AllTogether node) {
    countEndVisitAllTogether++;
    if (stackAllTogether.empty() || stackAllTogether.peek() != node) {
      Assertions.fail();
    }
    else {
      stackAllTogether.pop();
    }
  }
  
  @Override
  public void visit(AllTogether node) {
    countVisitAllTogether++;
    stackAllTogether.push(node);
  }
  
  @Override
  public void endVisit(ClassToBeTopped node) {
    countEndVisitClassToBeTopped++;
    if (stackClassToBeTopped.empty() || stackClassToBeTopped.peek() != node) {
      Assertions.fail();
    }
    else {
      stackClassToBeTopped.pop();
    }
    
  }
  
  @Override
  public void visit(ClassToBeTopped node) {
    countVisitClassToBeTopped++;
    stackClassToBeTopped.push(node);
  }
  
}
