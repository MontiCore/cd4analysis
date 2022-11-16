package de.monticore.cd2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class CD2SMTGeneratorTest extends CDDiffTestBasis {
  protected final String RELATIVE_MODEL_PATH = "src/cd2smttest/resources/de/monticore/cd2smt";
  ASTCDCompilationUnit astCD;
  CD2SMTGenerator cd2SMTGenerator;
  Context context;

  // setup

  void setup(String fileName) {
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    context = new Context(cfg);
    astCD = parseModel(Paths.get(RELATIVE_MODEL_PATH, fileName).toString());
    cd2SMTGenerator = new CD2SMTGenerator();
    cd2SMTGenerator.cd2smt(astCD, context);
  }

  @Test
  public void testDeclare_empty_class() {
    setup("car1.cd");
    Sort sort = cd2SMTGenerator.getSort(astCD.getCDDefinition().getCDClassesList().get(0));
    Assert.assertNotNull(sort);
  }

  @Test
  public void testDeclareClass_with_attribute() {
    setup("car11.cd");
    ASTCDClass Class = astCD.getCDDefinition().getCDClassesList().get(0);
    Expr<? extends Sort> obj = context.mkConst("myObj", cd2SMTGenerator.getSort(Class));
    for (ASTCDAttribute attribute : Class.getCDAttributeList()) {
      Expr<? extends Sort> attr = cd2SMTGenerator.getAttribute(Class, attribute.getName(), obj);
      Assert.assertNotNull(attr);
      switch (attribute.getName()) {
        case "price":
          Assert.assertEquals(attr.getSort(), context.mkRealSort());
          break;
        case "manufacturer":
          Assert.assertEquals(attr.getSort(), context.mkStringSort());
          break;
        case "numberOfWheel":
          Assert.assertEquals(attr.getSort(), context.mkIntSort());
          break;
        case "isExpensive":
          Assert.assertEquals(attr.getSort(), context.mkBoolSort());
          break;
      }
    }
  }
}
