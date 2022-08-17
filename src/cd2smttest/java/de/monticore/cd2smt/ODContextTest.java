package de.monticore.cd2smt;


import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.context.CDContext;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.context.SMTClass;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cddiff.CDDiffTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class ODContextTest  extends CDDiffTestBasis {


  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    Log.info("Start Test class CD2SMTGenerator","[CD2SMTGeneratorTest]-----------------------------------");
  }

  @Test
  public void testDeclareClass() {
    Log.info("***Test empty class declaration***", "[ClassDeclarationTest]");
    ASTCDCompilationUnit cpt2 = parseModel("src/cd2smttest/resources/de.monticore.cd2smt/car1.cd");
    CD2SMTGenerator cd2ODGenerator = new CD2SMTGenerator();
    CDContext cdContext2 = cd2ODGenerator.cd2smt(cpt2.getCDDefinition());
    Assert.assertEquals(cdContext2.getSmtClasses().size(), 1);
    Log.info("Class Number OK", "[classNumber]");
    Set<Map.Entry<ASTCDClass, SMTClass>> classes2 = cdContext2.getSmtClasses().entrySet();
    for (Map.Entry<ASTCDClass, SMTClass> entry : classes2) {
      Assert.assertEquals(entry.getValue().getAttributes().size(), 0);
      Log.info("Attribute Number OK", "[AttrNumber]");
    }

    Log.info("***Test class Declaration with attributes***", "[ClassDeclarationTest]");
    ASTCDCompilationUnit cpt1 = parseModel("src/cd2smttest/resources/de.monticore.cd2smt/car0.cd");
    CDContext cdContext = cd2ODGenerator.cd2smt(cpt1.getCDDefinition());

    Assert.assertEquals(cdContext.getSmtClasses().size(), 1);
    Log.info("Class Number OK", "[classNumber]");
    Set<Map.Entry<ASTCDClass, SMTClass>> classes = cdContext.getSmtClasses().entrySet();
    int is = 0;
    int rs = 0;
    int bs = 0;
    int ss = 0;
    for (Map.Entry<ASTCDClass, SMTClass> entry : classes) {
      Assert.assertEquals(entry.getValue().getAttributes().size(), 4);
      Log.info("Attribute Number OK", "[AttrNumber]");
      for (FuncDecl<Sort> attr : entry.getValue().getAttributes()) {
        if ((attr.getRange().equals(cdContext.getContext().mkRealSort()))) {
          rs++;
        }
        if ((attr.getRange().equals(cdContext.getContext().mkBoolSort()))) {
          bs++;
        }
        if ((attr.getRange().equals(cdContext.getContext().mkIntSort()))) {
          is++;
        }
        if ((attr.getRange().equals(cdContext.getContext().mkStringSort()))) {
          ss++;
        }
      }
      Assert.assertEquals(is, 1);
      Assert.assertEquals(rs, 1);
      Assert.assertEquals(bs, 1);
      Assert.assertEquals(ss, 1);
      Log.info("Attribute Type OK", "[AttrType]");

    }
  }
  @Test
  public void TestAssocDeclaration() {
    Log.info("***Test  Association Declaration***", "[AssocDeclarationTest]");
    Optional<ASTCDDefinition> cd = parseCD("src/cd2smttest/resources/de.monticore.cd2smt/car3.cd");
    CD2SMTGenerator cd2ODGenerator = new CD2SMTGenerator();
    assertTrue(cd.isPresent());
    CDContext cdContext = cd2ODGenerator.cd2smt(cd.get());
    Assert.assertEquals(cdContext.getSmtClasses().size(), 3);
    Log.info("Class Number OK", "[classNumber]");
    Assert.assertEquals(cdContext.getAssocFunctions().size(), 2);
    Log.info("Assoc Number OK", "[AssocNumber]");
  }

  @Test
  public  void testInheritance(){
    Log.info("***Test  Association Declaration***", "[AssocDeclarationTest]");
    Optional<ASTCDDefinition> cd = parseCD("src/cd2smttest/resources/de.monticore.cd2smt/car3.cd");
    CD2SMTGenerator cd2ODGenerator = new CD2SMTGenerator();
    CDContext cdContext2 = cd2ODGenerator.cd2smt(cd.get());

    Optional< ASTCDClass >wheel = cdContext2.getClass("Car",cd.get()) ;

    assertTrue(wheel.isPresent());

    Assert.assertEquals( cdContext2.getSmtClasses().get(wheel.get()).getAttributes().size(),4);
    Log.info("Attribute Number OK", "[AttrNumber]");
  }





  @After
  public  void  end(){
    Log.info("End Test class CD2SMTGenerator","[CD2SMTGeneratorTest]-------------------------------------");
  }

  protected Optional <ASTCDDefinition> parseCD(String filePath){
    // Given
    Path model = Paths.get(filePath);
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> optAutomaton = Optional.empty();
    try {
      optAutomaton = parser.parse(model.toString());
    } catch (IOException e) {
      fail();
    }//
    assertTrue(optAutomaton.isPresent());
    ASTCDDefinition cd = optAutomaton.get().getCDDefinition();
    return  Optional.of(cd) ;
  }


}



