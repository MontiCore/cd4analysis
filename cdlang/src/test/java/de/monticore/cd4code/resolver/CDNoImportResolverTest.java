/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.resolver;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class CDNoImportResolverTest extends CD4CodeTestBasis {
  
  String ModelA = """
      classdiagram CD2 {
          package p2 {
              package p3 {
                  class ClassA {
                      public int value;
                  }
              }
              class ClassC {
                  public p2.p3.ClassA value;
              }
          }
      }
      """;
  
  String ModelB = """
      classdiagram CD1 {
           package p1 {
               class ClassB {
                   private p2.p3.ClassA myAttribute;
               }
           }
      }
      """;
  //With Import
  String ModelBImport = """
      package p1;
      import p2.p3.ClassA;
      
      classdiagram CD1 {
          class ClassB {
              private ClassA myAttribute;
          }
      }
      """;
  
  @BeforeAll
  public static void init() throws Exception {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    
  }
  
  @Test
  public void resolvingWithoutImport() throws IOException {
    //parse Models
    final Optional<ASTCDCompilationUnit> optA = CD4CodeMill.parser().parse_String(ModelA);
    assertTrue(optA.isPresent());
    ASTCDCompilationUnit astA = optA.get();
    
    Optional<ASTCDCompilationUnit> optB = CD4CodeMill.parser().parse_String(ModelB);
    assertTrue(optB.isPresent());
    ASTCDCompilationUnit astB = optB.get();
    
    //initalize the Scopes
    CD4CodeMill.scopesGenitorDelegator().createFromAST(astA);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(astB);
    
    CD4CodeSymbolTableCompleter completerB = new CD4CodeSymbolTableCompleter(astA);
    astA.accept(completerB.getTraverser());
    
    CD4CodeSymbolTableCompleter completerA = new CD4CodeSymbolTableCompleter(astB);
    astB.accept(completerA.getTraverser());
    
    var resolved = astB.getEnclosingScope().resolveCDType("p2.p3.ClassA");
    
    assertTrue("ClassA could not be found!", resolved.isPresent());
    
    var siblingSymbol = astA.getEnclosingScope().resolveCDType("p2.ClassC");
    assertTrue(siblingSymbol.isPresent());
    
    var res2 = siblingSymbol.get().getSpannedScope().resolveCDType("p3.ClassA");
    assertTrue(res2.isPresent());
  }
  
  @Test
  public void resolvingWithImport() throws IOException {
    //parse Models
    final Optional<ASTCDCompilationUnit> optA = CD4CodeMill.parser().parse_String(ModelA);
    assertTrue(optA.isPresent());
    ASTCDCompilationUnit astA = optA.get();
    
    Optional<ASTCDCompilationUnit> optB = CD4CodeMill.parser().parse_String(ModelBImport);
    assertTrue(optB.isPresent());
    ASTCDCompilationUnit astB = optB.get();
    
    //initalize the Scopes
    CD4CodeMill.scopesGenitorDelegator().createFromAST(astA);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(astB);
    
    CD4CodeSymbolTableCompleter completerB = new CD4CodeSymbolTableCompleter(astA);
    astA.accept(completerB.getTraverser());
    
    CD4CodeSymbolTableCompleter completerA = new CD4CodeSymbolTableCompleter(astB);
    astB.accept(completerA.getTraverser());
    
    var resolved = astB.getEnclosingScope().resolveCDType("p2.p3.ClassA");
    
    assertTrue("ClassA could not be found!", resolved.isPresent());
  }
  
}
