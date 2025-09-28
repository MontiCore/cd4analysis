/* (c) https://github.com/MontiCore/monticore */
package TestInheritanceVisitor;

import org.junit.jupiter.api.Assertions;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Visitor implements ITestInheritanceVisitorInheritanceVisitor {
  
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
  //hierarchy
  public int countVisitLevel0class = 0;
  public int countEndVisitLevel0class = 0;
  
  public int countVisitLevel1Interface = 0;
  public int countEndVisitLevel1Interface = 0;
  
  public int countVisitLevel2class = 0;
  public int countEndVisitLevel2class = 0;
  public int countVisitLevel2Interface1 = 0;
  public int countEndVisitLevel2Interface1 = 0;
  public int countVisitLevel2Interface2 = 0;
  public int countEndVisitLevel2Interface2 = 0;
  public int countVisitLevel2Interface = 0;
  public int countEndVisitLevel2Interface = 0;
  
  public int countVisitLevel3class = 0;
  public int countEndVisitLevel3class = 0;
  public int countVisitLevel3Interface1 = 0;
  public int countEndVisitLevel3Interface1 = 0;
  public int countVisitLevel3Interface2 = 0;
  public int countEndVisitLevel3Interface2 = 0;
  
  public int countVisitLevel4class = 0;
  public int countEndVisitLevel4class = 0;
  public int countVisitLevel4Interface = 0;
  public int countEndVisitLevel4Interface = 0;
  
  public int countVisitLevel5class = 0;
  public int countEndVisitLevel5class = 0;
  
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
  Stack<Level0class> stackLevel0class = new Stack<>();
  Stack<Level1Interface> stackLevel1Interface = new Stack<>();
  Stack<Level2class> stackLevel2class = new Stack<>();
  Stack<Level2Interface1> stackLevel2Interface1 = new Stack<>();
  Stack<Level2Interface2> stackLevel2Interface2 = new Stack<>();
  Stack<Level2Interface> stackLevel2Interface = new Stack<>();
  Stack<Level3class> stackLevel3class = new Stack<>();
  Stack<Level3Interface1> stackLevel3Interface1 = new Stack<>();
  Stack<Level3Interface2> stackLevel3Interface2 = new Stack<>();
  Stack<Level4class> stackLevel4class = new Stack<>();
  Stack<Level4Interface> stackLevel4Interface = new Stack<>();
  Stack<Level5class> stackLevel5class = new Stack<>();
  
  public boolean isAllEmpty() {
    return stackClassWithMap.empty() && stackClassCircular1.empty() && stackClassCircular2.empty()
        && stackClassWith2DimList.empty() && stackClassWith2DimOptional.empty()
        && stackClassWith2DimSet.empty() && stackClassWith2DimMap.empty() && stackClassWith3DimArray
            .empty() && stackClassWithArray.empty() && stackClassWithAssociation.empty()
        && stackClassWithComposition.empty() && stackClassWithList.empty() && stackClassWithOptional
            .empty() && stackClassWithPojoClassType.empty() && stackClassWithPrimitiveType.empty()
        && stackClassWithSet.empty() && stackClassWithString.empty() && stackAllTogether.empty()
        && stackB.empty() && stackClassToBeTopped.empty() && stackLevel0class.empty()
        && stackLevel1Interface.empty() && stackLevel2class.empty() && stackLevel2Interface1.empty()
        && stackLevel2Interface2.empty() && stackLevel2Interface.empty() && stackLevel3class.empty()
        && stackLevel3Interface1.empty() && stackLevel3Interface2.empty() && stackLevel4class
            .empty() && stackLevel4Interface.empty() && stackLevel5class.empty();
  }
  
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
  
  @Override
  public void visit(Level0class node) {
    countVisitLevel0class++;
    stackLevel0class.push(node);
  }
  
  @Override
  public void endVisit(Level0class node) {
    countEndVisitLevel0class++;
    if (stackLevel0class.empty() || stackLevel0class.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel0class.pop();
    }
  }
  
  @Override
  public void visit(Level1Interface node) {
    countVisitLevel1Interface++;
    stackLevel1Interface.push(node);
  }
  
  @Override
  public void endVisit(Level1Interface node) {
    countEndVisitLevel1Interface++;
    if (stackLevel1Interface.empty() || stackLevel1Interface.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel1Interface.pop();
    }
  }
  
  @Override
  public void visit(Level2class node) {
    countVisitLevel2class++;
    stackLevel2class.push(node);
  }
  
  @Override
  public void endVisit(Level2class node) {
    countEndVisitLevel2class++;
    if (stackLevel2class.empty() || stackLevel2class.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel2class.pop();
    }
  }
  
  @Override
  public void visit(Level2Interface1 node) {
    countVisitLevel2Interface1++;
    stackLevel2Interface1.push(node);
  }
  
  @Override
  public void endVisit(Level2Interface1 node) {
    countEndVisitLevel2Interface1++;
    if (stackLevel2Interface1.empty() || stackLevel2Interface1.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel2Interface1.pop();
    }
  }
  
  @Override
  public void visit(Level2Interface2 node) {
    countVisitLevel2Interface2++;
    stackLevel2Interface2.push(node);
  }
  
  @Override
  public void endVisit(Level2Interface2 node) {
    countEndVisitLevel2Interface2++;
    if (stackLevel2Interface2.empty() || stackLevel2Interface2.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel2Interface2.pop();
    }
  }
  
  @Override
  public void visit(Level2Interface node) {
    countVisitLevel2Interface++;
    stackLevel2Interface.push(node);
  }
  
  @Override
  public void endVisit(Level2Interface node) {
    countEndVisitLevel2Interface++;
    if (stackLevel2Interface.empty() || stackLevel2Interface.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel2Interface.pop();
    }
  }
  
  @Override
  public void visit(Level3class node) {
    countVisitLevel3class++;
    stackLevel3class.push(node);
  }
  
  @Override
  public void endVisit(Level3class node) {
    countEndVisitLevel3class++;
    if (stackLevel3class.empty() || stackLevel3class.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel3class.pop();
    }
  }
  
  @Override
  public void visit(Level3Interface1 node) {
    countVisitLevel3Interface1++;
    stackLevel3Interface1.push(node);
  }
  
  @Override
  public void endVisit(Level3Interface1 node) {
    countEndVisitLevel3Interface1++;
    if (stackLevel3Interface1.empty() || stackLevel3Interface1.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel3Interface1.pop();
    }
  }
  
  @Override
  public void visit(Level3Interface2 node) {
    countVisitLevel3Interface2++;
    stackLevel3Interface2.push(node);
  }
  
  @Override
  public void endVisit(Level3Interface2 node) {
    countEndVisitLevel3Interface2++;
    if (stackLevel3Interface2.empty() || stackLevel3Interface2.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel3Interface2.pop();
    }
  }
  
  @Override
  public void visit(Level4class node) {
    countVisitLevel4class++;
    stackLevel4class.push(node);
  }
  
  @Override
  public void endVisit(Level4class node) {
    countEndVisitLevel4class++;
    if (stackLevel4class.empty() || stackLevel4class.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel4class.pop();
    }
  }
  
  @Override
  public void visit(Level4Interface node) {
    countVisitLevel4Interface++;
    stackLevel4Interface.push(node);
  }
  
  @Override
  public void endVisit(Level4Interface node) {
    countEndVisitLevel4Interface++;
    if (stackLevel4Interface.empty() || stackLevel4Interface.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel4Interface.pop();
    }
  }
  
  @Override
  public void visit(Level5class node) {
    countVisitLevel5class++;
    stackLevel5class.push(node);
  }
  
  @Override
  public void endVisit(Level5class node) {
    countEndVisitLevel5class++;
    if (stackLevel5class.empty() || stackLevel5class.peek() != node) {
      Assertions.fail();
    }
    else {
      stackLevel5class.pop();
    }
  }
  
}
