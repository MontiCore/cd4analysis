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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AssocInhertianceTest extends CDDiffTestBasis {

  protected final String RELATIVE_MODEL_PATH =
      "src/cd2smttest/resources/de/monticore/cd2smt/DataWrapper/inheritance";
  protected CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator();
  protected Context ctx;
  protected ASTCDDefinition cd;

  protected ASTCDClass Car;
  protected ASTCDClass Person;
  protected ASTCDClass Color;
  protected ASTCDClass Auction;

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
    Car = CDHelper.getClass("Car", cd);
    Person = CDHelper.getClass("Person", cd);
    Auction = CDHelper.getClass("Auction", cd);
    Color = CDHelper.getClass("Color", cd);
  }

  public void checkLink(ASTCDAssociation association, ASTCDClass class1, ASTCDClass class2) {
    Expr<? extends Sort> obj1 = ctx.mkConst("obj1", cd2SMTGenerator.getSort(class1));
    Expr<? extends Sort> obj2 = ctx.mkConst("obj2", cd2SMTGenerator.getSort(class2));

    Optional<Expr<? extends Sort>> link =
        Optional.ofNullable(cd2SMTGenerator.evaluateLink(association, class1, class2, obj1, obj2));
    Assertions.assertTrue(link.isPresent());
  }

  @Test
  public void test_inheritance_AssocFrom_interface_right1() {
    ASTCDAssociation association = CDHelper.getAssociation(Car, "color", cd);
    checkLink(association, Car, Color);
  }

  @Test
  public void test_inheritance_AssocFrom_interface_right2() {
    ASTCDAssociation association = CDHelper.getAssociation(Car, "color", cd);
    checkLink(association, Color, Car);
  }

  @Test
  public void test_inheritance_AssocFrom_interface_left1() {
    ASTCDAssociation association = CDHelper.getAssociation(Person, "auction", cd);
    checkLink(association, Person, Auction);
  }

  @Test
  public void test_inheritance_AssocFrom_interface_left2() {
    ASTCDAssociation association = CDHelper.getAssociation(Person, "auction", cd);
    checkLink(association, Auction, Person);
  }

  @Test
  public void test_inheritance_AssocFrom_interface_both_Side1() {
    ASTCDAssociation association = CDHelper.getAssociation(Car, "personInterface", cd);
    checkLink(association, Car, Person);
  }

  @Test
  public void test_inheritance_AssocFrom_interface_both_Side2() {
    ASTCDAssociation association = CDHelper.getAssociation(Person, "carInterface", cd);
    checkLink(association, Car, Person);
  }
}
