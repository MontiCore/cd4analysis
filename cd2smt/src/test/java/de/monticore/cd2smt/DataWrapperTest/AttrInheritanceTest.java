/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.DataWrapperTest;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.CD2SMTAbstractTest;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.se_rwth.commons.logging.Log;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AttrInheritanceTest extends CD2SMTAbstractTest {
  protected CD2SMTGenerator cd2SMTGenerator;
  protected Context ctx;
  protected ASTCDDefinition cd;

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.init();

    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");

    ASTCDCompilationUnit ast =
        parseModel(Paths.get("DataWrapper/inheritance/attribute/AttrInheritance.cd").toString());
    CD2SMTMill.initDefault();
    cd2SMTGenerator = CD2SMTMill.cd2SMTGenerator();
    cd2SMTGenerator.cd2smt(ast, new Context(cfg));
    ctx = cd2SMTGenerator.getContext();
    cd = cd2SMTGenerator.getClassDiagram().getCDDefinition();
  }

  public void checkAttribute(ASTCDClass Class, String attrname, Expr<? extends Sort> obj) {
    Optional<Expr<? extends Sort>> attribute =
        Optional.ofNullable(cd2SMTGenerator.getAttribute(Class, attrname, obj));
    Assertions.assertTrue(attribute.isPresent());
  }

  @Test
  public void test_Attribute_Inheritance_Hierarchy_1() {
    ASTCDClass Car = CDHelper.getClass("Car", cd);
    Expr<? extends Sort> car = ctx.mkConst("bigbigcar", cd2SMTGenerator.getSort(Car));

    checkAttribute(Car, "isCarInterface1", car);
    checkAttribute(Car, "isCarInterface2", car);
    checkAttribute(Car, "isVehicle", car);
  }

  @Test
  public void test_Attribute_Inheritance_Hierarchy_2() {
    ASTCDClass BigCar = CDHelper.getClass("BigCar", cd);
    Expr<? extends Sort> bigCar = ctx.mkConst("bigcar", cd2SMTGenerator.getSort(BigCar));

    checkAttribute(BigCar, "isCarInterface1", bigCar);
    checkAttribute(BigCar, "isCarInterface2", bigCar);
    checkAttribute(BigCar, "isVehicle", bigCar);
    checkAttribute(BigCar, "isCar", bigCar);
    checkAttribute(BigCar, "isBigCarInterface", bigCar);
    checkAttribute(BigCar, "isBigCar", bigCar);
  }

  @Test
  public void test_Attribute_Inheritance_Hierarchy_3() {
    ASTCDClass BigBigCar = CDHelper.getClass("BigBigCar", cd);
    Expr<? extends Sort> bigBigCar = ctx.mkConst("bigBigCar", cd2SMTGenerator.getSort(BigBigCar));

    checkAttribute(BigBigCar, "isCarInterface1", bigBigCar);
    checkAttribute(BigBigCar, "isCarInterface2", bigBigCar);
    checkAttribute(BigBigCar, "isVehicle", bigBigCar);
    checkAttribute(BigBigCar, "isCar", bigBigCar);
    checkAttribute(BigBigCar, "isBigCarInterface", bigBigCar);
    checkAttribute(BigBigCar, "isBigCar", bigBigCar);
    checkAttribute(BigBigCar, "isBigBigCar", bigBigCar);
  }
}
