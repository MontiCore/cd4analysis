package de.monticore.cd2smt;

import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CDHelperTest extends CD2SMTAbstractTest {
  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {"car3.cd"})
  public void check(String file) {
    checkSubTypes("BigBigCar", 0);
    checkSubTypes("Car", 2);
    checkSubTypes("AutoInterface2", 4);
    checkSubTypes("CarAbstract", 3);
    checkSuperTypes("BigBigCar", 7);
    checkSuperTypes("Car", 5);
    checkSuperTypes("CarInterface", 2);
    checkSuperTypes("AutoInterface1", 0);
  }

  public void checkSuperTypes(String type, int number) {
    ASTCDCompilationUnit ast = parseModel("car3.cd");

    ASTCDType astcdType = CDHelper.getASTCDType(type, ast.getCDDefinition());
    Assertions.assertNotNull(astcdType);
    Set<ASTCDType> superTypeList = CDHelper.getSuperTypeAllDeep(astcdType, ast.getCDDefinition());
    Assertions.assertEquals(superTypeList.size(), number);
  }

  public void checkSubTypes(String type, int number) {
    ASTCDCompilationUnit ast = parseModel("car3.cd");
    ASTCDType astcdType = CDHelper.getASTCDType(type, ast.getCDDefinition());
    Assertions.assertNotNull(astcdType);
    Set<ASTCDType> subTypeAllDeep = CDHelper.getSubTypeAllDeep(astcdType, ast);
    Assertions.assertEquals(subTypeAllDeep.size(), number);
  }
}
