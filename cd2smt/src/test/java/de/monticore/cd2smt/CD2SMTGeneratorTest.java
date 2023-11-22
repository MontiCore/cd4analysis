/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTMill;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class CD2SMTGeneratorTest {
  protected final String RELATIVE_MODEL_PATH = "src/test/resources/de/monticore/cd2smt";
  ASTCDCompilationUnit astCD;
  CD2SMTGenerator cd2SMTGenerator;
  Context context;

  @Before
  public void init() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  void setup(String fileName) {
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    context = new Context(cfg);

    try {
      Optional<ASTCDCompilationUnit> optCD =
          CD4CodeMill.parser().parse(Paths.get(RELATIVE_MODEL_PATH, fileName).toString());
      Assert.assertTrue(optCD.isPresent());
      astCD = optCD.get();
      (new CD4AnalysisAfterParseTrafo()).transform(astCD);
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
    CD2SMTMill.initDefault();
    cd2SMTGenerator = CD2SMTMill.cd2SMTGenerator();
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

  @Test
  public void EnumerationTest() {
    setup("car21.cd");
    ASTCDEnum astcdEnum = (ASTCDEnum) CDHelper.getASTCDType("Color", astCD.getCDDefinition());
    Assertions.assertNotNull(astcdEnum);
    Expr<? extends Sort> enumConstant =
        cd2SMTGenerator.getEnumConstant(astcdEnum, astcdEnum.getCDEnumConstant(0));
    Assertions.assertEquals("RED", enumConstant.toString());
  }
}
