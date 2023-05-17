/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.DataWrapperTest;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cddiff.CDDiffTestBasis;
import de.se_rwth.commons.logging.Log;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AssocInhertianceTest extends CDDiffTestBasis {

  protected final String RELATIVE_MODEL_PATH =
      "src/cd2smttest/resources/de/monticore/cd2smt/DataWrapper/inheritance";
  protected CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator();
  protected Context ctx;
  protected ASTCDDefinition cd;

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.init();

    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");

    ASTCDCompilationUnit ast =
        parseModel(Paths.get(RELATIVE_MODEL_PATH, "/association/AssocInheritance.cd").toString());
    cd2SMTGenerator.cd2smt(ast, new Context(cfg));
    ctx = cd2SMTGenerator.getContext();
    cd = cd2SMTGenerator.getClassDiagram().getCDDefinition();
  }

  public void checkLink(String left, String role, String right) {
    ASTCDClass type1 = CDHelper.getClass(left, cd);
    ASTCDClass type2 = CDHelper.getClass(right, cd);
    ASTCDAssociation association = CDHelper.getAssociation(type1, role, cd);
    Expr<? extends Sort> obj1 = ctx.mkConst("obj1", cd2SMTGenerator.getSort(type1));
    Expr<? extends Sort> obj2 = ctx.mkConst("obj2", cd2SMTGenerator.getSort(type2));

    Optional<Expr<? extends Sort>> link1 =
        Optional.ofNullable(cd2SMTGenerator.evaluateLink(association, type1, type2, obj1, obj2));
    Assertions.assertTrue(link1.isPresent());
    Optional<Expr<? extends Sort>> link2 =
        Optional.ofNullable(cd2SMTGenerator.evaluateLink(association, type2, type1, obj2, obj1));
    Assertions.assertTrue(link2.isPresent());
  }

  @ParameterizedTest
  @MethodSource("links")
  public void testCarAssociations(String left, String role, String right) {
    checkLink(left, role, right);
  }

  @Test
  public void test_inheritance_AssocFrom_interface_right2() {
    checkLink("BigCar", "abstractPerson", "Person");
  }

  public static Stream<Arguments> links() {
    return Stream.of(
        Arguments.of("Car", "abstractPerson", "AbstractPerson"),
        Arguments.of("Person", "abstractCar", "AbstractCar"),
        Arguments.of("Car", "abstractPerson", "Person"),
        Arguments.of("Person", "abstractCar", "Car"),
        Arguments.of("Car", "color", "Color"),
        Arguments.of("Person", "auction", "Auction"),
        Arguments.of("BigCar", "person", "Person"),
        Arguments.of("BigCar", "abstractPerson", "AbstractPerson"),
        Arguments.of("BigCar", "abstractPerson", "Person"),
        Arguments.of("AbstractPerson", "abstractCar", "Car"),
        Arguments.of("AbstractPerson", "abstractCar", "BigCar"));
  }
}
