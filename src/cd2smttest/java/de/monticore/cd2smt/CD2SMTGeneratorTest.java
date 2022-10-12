package de.monticore.cd2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.context.CDArtifacts.SMTCDType;
import de.monticore.cd2smt.context.CDContext;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffTestBasis;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CD2SMTGeneratorTest extends CDDiffTestBasis {
  protected final String RELATIVE_MODEL_PATH = "src/cd2smttest/resources/de/monticore/cd2smt";
  //setup

  CDContext buildCDContext(String fileName) {
    ASTCDCompilationUnit cpt2 = parseModel(Paths.get(RELATIVE_MODEL_PATH, fileName).toString());
    CD2SMTGenerator cd2ODGenerator = new CD2SMTGenerator();
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    return cd2ODGenerator.cd2smt(cpt2, new Context(cfg));
  }

  @Test
  public void testDeclare_empty_class() {
    CDContext cdContext = buildCDContext("car1.cd");
    Assert.assertEquals(cdContext.getSmtCDTypes().size(), 1);
    for (Map.Entry<ASTCDType, SMTCDType> entry : cdContext.getSmtCDTypes().entrySet()) {
      Assert.assertEquals(entry.getValue().getAttributes().size(), 0);
    }
  }

  @Test
  public void testDeclareClass_with_attribute() {

    CDContext cdContext = buildCDContext("car11.cd");

    Assert.assertEquals(cdContext.getSmtCDTypes().size(), 1);

    Set<Map.Entry<ASTCDType, SMTCDType>> classes = cdContext.getSmtCDTypes().entrySet();
    int is = 0;
    int rs = 0;
    int bs = 0;
    int ss = 0;
    for (Map.Entry<ASTCDType, SMTCDType> entry : classes) {
      Assert.assertEquals(entry.getValue().getAttributes().size(), 4);
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

    }
  }

  @Test
  public void TestAssocDeclaration() {
    CDContext cdContext = buildCDContext("car12.cd");
    Assert.assertEquals(cdContext.getSmtCDTypes().size(), 3);
    Assert.assertEquals(cdContext.getSMTAssociations().size(), 2);
  }

}


