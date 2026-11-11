/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.*;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.runtime.junit.MCAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VisitorDecoratorTest extends AbstractDecoratorTest {
  
  /**
   * Test the {@link VisitorDecorator} by applying it to a CD. The
   * cdlang/src/cdGenIntTest/java/visitor/VisitorDecoratorResultTest then tests the generated result
   */
  @Test
  void testVisitor() throws Exception {
    var opt = CD4CodeMill.parser().parse_String("""
         <<getter,setter,visitor>>
         classdiagram TestVisitor {
         public class A {
          int prim;
          B b;
           -> (manyB) B [*] public;
           -> (optB1) B [0..1] public;
           -> (optB2) B [0..1] public;
           -> (oneB) B [1] public;
           // -> (privateB) B [1] private; // TODO: Compilation error!
         }
         public class B {
          String bName;
         }
         public class C {
          //<-> (manyC) C1 [*] public; - bidirectional not part of CDDirectComposition
          //<-> (optC) C2 [0..1] public; - bidirectional not part of CDDirectComposition
          -> (oneCAttr) C5 [1] public;
         }
         public class C1 {}
         public class C2 {}
         public class C3 {}
         public class C4 {}
         public class C5 {}
         association [0..1] C <-> (manyC) C1  [*];
         association [0..1] C <-> (optC)  C2  [0..1];
         association [*]    C <-> (manyManyC)  C3  [*];
         association        C <-> (manyC4) C4 [*];
        
        }""");
    
    Assertions.assertTrue(opt.isPresent());
    
    super.doTest(opt.get());
    
    MCAssertions.assertNoFindings();
  }
  
  @Test
  void testIncorrect() throws Exception {
    var opt = CD4CodeMill.parser().parse_String("""
         classdiagram IncorrectTestVisitor {
         <<getter,visitor>>
         public class C {
          //<-> (manyC) C1 [*] public; - bidirectional not part of CDDirectComposition
          //<-> (optC) C2 [0..1] public; - bidirectional not part of CDDirectComposition
          -> (oneC) C3 [1] public;
         }
         // No getter and or visitor
         <<getter>>
         public class C1 {}
         public class C2 {}
         public class C3 {}
         public class C4 {}
         association [0..1] C (manyC) <-> C1 [*];
         association [0..1] C (optC) <-> C2 [*];
         association [0..1] C (oneC) <-> C3 [*];
         association [0..1] C (manyC4) <-> (optC4) C4 [*];
        
        
        }""");
    
    Assertions.assertTrue(opt.isPresent());
    
    super.doTest(opt.get());
    
    MCAssertions.assertHasFindingsStartingWith(
        "0xTODO: Visitor implementation of association `IncorrectTestVisitor.C.c1` is not possible due to missing visitor of the class `IncorrectTestVisitor.C1`");
    MCAssertions.assertHasFindingsStartingWith(
        "0xTODO: Visitor implementation of association `IncorrectTestVisitor.C.c2` is not possible due to missing visitor of the class `IncorrectTestVisitor.C2`");
    MCAssertions.assertHasFindingsStartingWith(
        "0xTODO: Visitor implementation of association `IncorrectTestVisitor.C.c3` is not possible due to missing visitor of the class `IncorrectTestVisitor.C3`");
    MCAssertions.assertHasFindingsStartingWith(
        "0xTODO: Visitor implementation of association `IncorrectTestVisitor.C.optC4` is not possible due to missing visitor of the class `IncorrectTestVisitor.C4`");
    MCAssertions.assertHasFindingsStartingWith(
        "0xTODO: Visitor implementation of association `IncorrectTestVisitor.C.oneC` is not possible due to missing visitor of the class `IncorrectTestVisitor.C3`");
  }
  
  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {
    config.withCopyCreator().defaultApply();
    config.withDecorator(new GetterDecorator());
    config.configApplyMatchName(GetterDecorator.class, "getter");
    config.configIgnoreMatchName(GetterDecorator.class, "noGetter");
    config.withDecorator(new SetterDecorator());
    config.configApplyMatchName(SetterDecorator.class, "setter");
    config.configIgnoreMatchName(SetterDecorator.class, "noSetter");
    config.withDecorator(new NavigableSetterDecorator());
    config.configApplyMatchName(NavigableSetterDecorator.class, "setter");
    config.configIgnoreMatchName(NavigableSetterDecorator.class, "noSetter");
    config.withDecorator(new VisitorDecorator());
    config.configApplyMatchName(VisitorDecorator.class, "visitor");
    config.configIgnoreMatchName(VisitorDecorator.class, "noVisitor");
    config.withDecorator(new VisitorImplementationDecorator());
    config.configApplyMatchName(VisitorImplementationDecorator.class, "visitor");
    config.configIgnoreMatchName(VisitorImplementationDecorator.class, "noVisitor");
    config.withDecorator(new CardinalityDefaultDecorator());
    config.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);
  }
  
}
