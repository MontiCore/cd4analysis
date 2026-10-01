/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.resolver;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class CDSupertypeResolvingTest extends CD4CodeTestBasis {
  
  String SimpleClass = """
      classdiagram CD1{
        class SimpleClassA extends SimpleClassB {
              public int valueA;
              public int[] fieldA;
          }
          class SimpleClassB {
              public int valueB;
              public int[] fieldB;
          }
      }
      """;
  
  String SimpleInterfaceClass = """
          classdiagram CD2{
           class InterfaceClassA implements I{
           }
           interface I{
            }
          }
      """;
  
  String SimpleCascadingClass = """
        classdiagram CD3{
        class SimpleClassA extends SimpleClassB {
          public int valueA;
          public int[] fieldA;
        }
        class SimpleClassB extends SimpleClassC{
          public int valueB;
          public int[] fieldB;
        }
        class SimpleClassC
        {
          public int valueC;
        }
      }
      """;
  
  String PackageClass = """
      classdiagram CDPackage{
        class ClassA extends p2.ClassB {
          public int valueA;
        }
        package p1 {
          class ClassB {
            public int valueB;
          }
        }
      }
      """;
  
  String PackageClass2 = """
      classdiagram CDPackage2{
        package p1 {
          class ClassA extends ClassB {
          public int valueA;
          }
        }
        class ClassB {
        public int valueB;
        }
      }
      """;
  
  String PackageClass3 = """
      classdiagram CDPackage3{
        package p1 {
          class ClassA extends ClassB {
            public int valueA;
          }
          class ClassB {
            public int valueB;
          }
        }
      }
      """;
  
  @BeforeAll
  public static void init() throws Exception {
    CD4CodeMill.reset();
    CD4CodeMill.init();
  }
  
  @Test
  //can SimpleClassB be resolved without using a symbol table completer?
  public void SimpleClassTest() throws IOException {
    
    final Optional<ASTCDCompilationUnit> optSimple = CD4CodeMill.parser().parse_String(SimpleClass);
    assertTrue(optSimple.isPresent());
    ASTCDCompilationUnit astSimpleClass = optSimple.get();
    
    // Build the symbol table using only the basic scopeGenitor (deliberately omitting the completer)
    CD4CodeMill.scopesGenitorDelegator().createFromAST(astSimpleClass);
    ASTCDClass simpleClassA = (ASTCDClass) astSimpleClass.getCDDefinition().getCDClassesList()
        .getFirst();
    
    // can the class be resolved without using the symboltable completer
    var resolved = simpleClassA.getEnclosingScope().resolveCDType("SimpleClassB");
    assertTrue("SuperType B can be resolved without the symbol table completer.", resolved
        .isPresent());
    
    // can an attribute be resolved without using the symboltable completer? it should not work not but who knows
    var resolvedVar = simpleClassA.getEnclosingScope().resolveVariable("valueB");
    assertFalse(
        "Attribute valueB from SuperClassB was unexpectedly resolved without the symbol table completer",
        resolvedVar.isPresent());
  }
  
  @Test
  public void SimpleInterfaceTest() throws IOException {
    final Optional<ASTCDCompilationUnit> optSimple = CD4CodeMill.parser().parse_String(
        SimpleInterfaceClass);
    assertTrue(optSimple.isPresent());
    ASTCDCompilationUnit astSimpleClass = optSimple.get();
    
    CD4CodeMill.scopesGenitorDelegator().createFromAST(astSimpleClass);
    ASTCDClass interfaceClassA = (ASTCDClass) astSimpleClass.getCDDefinition().getCDClassesList()
        .stream().filter(c -> c.getName().equals("InterfaceClassA")).findFirst().get();
    
    var resolvedInterface = interfaceClassA.getEnclosingScope().resolveCDType("I");
    assertTrue("The implemented interface I should be resolvable even without the completer.",
        resolvedInterface.isPresent());
  }
  
  @Test
  public void SimpleCascadingTest() throws IOException {
    final Optional<ASTCDCompilationUnit> optSimple = CD4CodeMill.parser().parse_String(
        SimpleCascadingClass);
    assertTrue(optSimple.isPresent());
    ASTCDCompilationUnit astCascadingClass = optSimple.get();
    
    CD4CodeMill.scopesGenitorDelegator().createFromAST(astCascadingClass);
    ASTCDClass cascadingClass = (ASTCDClass) astCascadingClass.getCDDefinition().getCDClassesList()
        .getFirst();
    
    var resolvedClassB = cascadingClass.getEnclosingScope().resolveCDType("SimpleClassB");
    assertTrue("The SuperClass ClassB should be resolvable even without the completer.",
        resolvedClassB.isPresent());
    
    var resolvedClassC = cascadingClass.getEnclosingScope().resolveCDType("SimpleClassC");
    assertTrue("The SuperClass ClassC should be resolvable even without the completer.",
        resolvedClassC.isPresent());
  }
  
  // test with packages
  @Test
  public void packageClassTest() throws IOException {
    final Optional<ASTCDCompilationUnit> opt = CD4CodeMill.parser().parse_String(PackageClass);
    assertTrue(opt.isPresent());
    ASTCDCompilationUnit ast = opt.get();
    
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ASTCDClass packageClassA = (ASTCDClass) ast.getCDDefinition().getCDClassesList().stream()
        .filter(c -> c.getName().equals("ClassA")).findFirst().get();
    
    var resolved = packageClassA.getEnclosingScope().resolveCDType("p1.ClassB");
    assertTrue("ClassA  should be able to resolve ClassB from package p1.", resolved.isPresent());
    assertEquals(resolved.get().getFullName(), "CDPackage.p1.ClassB");
  }
  
  @Test
  public void packageTest2() throws IOException {
    final Optional<ASTCDCompilationUnit> opt = CD4CodeMill.parser().parse_String(PackageClass2);
    assertTrue(opt.isPresent());
    ASTCDCompilationUnit ast = opt.get();
    
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ASTCDClass classA = (ASTCDClass) ast.getCDDefinition().getCDClassesList().stream().filter(c -> c
        .getName().equals("ClassA")).findFirst().get();
    
    var resolved = classA.getEnclosingScope().resolveCDType("ClassB");
    
    assertTrue("ClassA inside package p1 should be able to resolve ClassB outside the package.",
        resolved.isPresent());
    assertEquals("CDPackage2.ClassB", resolved.get().getFullName());
  }
  
  @Test
  public void packageTest3() throws IOException {
    final Optional<ASTCDCompilationUnit> opt = CD4CodeMill.parser().parse_String(PackageClass3);
    assertTrue(opt.isPresent());
    ASTCDCompilationUnit ast = opt.get();
    
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ASTCDClass classA = (ASTCDClass) ast.getCDDefinition().getCDClassesList().stream().filter(c -> c
        .getName().equals("ClassA")).findFirst().get();
    
    var resolved = classA.getEnclosingScope().resolveCDType("p1.ClassB");
    
    assertTrue("ClassA inside package p1 should be able to resolve ClassB inside package p1.",
        resolved.isPresent());
    assertEquals("CDPackage3.p1.ClassB", resolved.get().getFullName());
  }
  
  //--------------Tests with packages and symboltable completer-----------------------------------
  
  @Test
  public void packageSTCTest() throws IOException {
    final Optional<ASTCDCompilationUnit> opt = CD4CodeMill.parser().parse_String(PackageClass3);
    assertTrue(opt.isPresent());
    ASTCDCompilationUnit ast = opt.get();
    
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    //TODO
  }
  
}
