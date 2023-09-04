/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.parser;

import static org.junit.Assert.*;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.testcdassociation.TestCDAssociationMill;
import de.monticore.testcdassociation._visitor.TestCDAssociationTraverser;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

public class TestCDAssociationParserTest extends CDAssociationTestBasis {

  @Test
  public void parseCDAssociation() throws IOException {
    final Optional<ASTCDAssociation> astcdAssociation =
        p.parse_StringCDAssociation("association [*] A -> [[id]] S [1];");
    checkNullAndPresence(p, astcdAssociation);
  }

  @Test
  public void parseCDElement() throws IOException {
    final Optional<ASTCDElement> astcdElement =
        p.parse_StringCDElement("composition a [*] A -> [[id]] S [1];");
    checkNullAndPresence(p, astcdElement);
  }

  @Test
  public void parseCDMember() throws IOException {
    final Optional<ASTCDMember> astcdMember = p.parse_StringCDMember("-> (r) B [*];");
    checkNullAndPresence(p, astcdMember);
  }

  @Test
  public void parseCardinalitiesMult() throws IOException {
    ASTCDAssociation mult = p.parse_StringCDAssociation("association [*] A -> S [*];").get();
      var traverser = CDAssociationMill.inheritanceTraverser();
      var visitor = new CDAssociationVisitor2() {
        @Override
        public void visit(ASTCDCardinality node) {
          assertTrue(node instanceof ASTCDCardMult);
        }
      };
      traverser.add4CDAssociation(visitor);
      mult.accept(traverser);
  }

  @Test
  public void parseCardinalitiesOne() throws IOException {
    ASTCDAssociation one = p.parse_StringCDAssociation("association [1] A -> S [1];").get();
    var traverser = CDAssociationMill.inheritanceTraverser();
    var visitor = new CDAssociationVisitor2() {
      @Override
      public void visit(ASTCDCardinality node) {
        assertTrue(node instanceof ASTCDCardOne);
      }
    };
    traverser.add4CDAssociation(visitor);
    one.accept(traverser);
  }

  @Test
  public void parseCardinalitiesAtLeastOne() throws IOException {
    ASTCDAssociation atLeastOne = p.parse_StringCDAssociation("association [1..*] A -> S [1..*];").get();
    var traverser = CDAssociationMill.inheritanceTraverser();
    var visitor = new CDAssociationVisitor2() {
      @Override
      public void visit(ASTCDCardinality node) {
        assertTrue(node instanceof ASTCDCardAtLeastOne);
      }
    };
    traverser.add4CDAssociation(visitor);
    atLeastOne.accept(traverser);
  }

  @Test
  public void parserCardinalitiesOpt() throws IOException {
    ASTCDAssociation opt = p.parse_StringCDAssociation("association [0..1] A -> S [0..1];").get();
    var traverser = CDAssociationMill.inheritanceTraverser();
    var visitor = new CDAssociationVisitor2() {
      @Override
      public void visit(ASTCDCardinality node) {
        assertTrue(node instanceof ASTCDCardOpt);
      }
    };
    traverser.add4CDAssociation(visitor);
    opt.accept(traverser);
  }

  @Test
  public void parserCardinalitiesOther() throws IOException {
    ASTCDAssociation other = p.parse_StringCDAssociation("association [2..3] A -> S [4..5];").get();
    var traverser = CDAssociationMill.inheritanceTraverser();
    var visitor = new CDAssociationVisitor2() {
      @Override
      public void visit(ASTCDCardinality node) {
        assertTrue(node instanceof ASTCDCardOther);
        assertTrue(
          (node.toCardinality().getLowerBound() == 2 && node.toCardinality().getUpperBound() == 3)
          || (node.toCardinality().getLowerBound() == 4 && node.toCardinality().getUpperBound() == 5)
        );
      }
    };
    traverser.add4CDAssociation(visitor);
    other.accept(traverser);
  }

  @Test
  public void parseCDAssociationDirection() throws IOException {
    final Optional<ASTCDAssocDir> leftToRightDir = p.parse_StringCDAssocDir("->");
    checkNullAndPresence(p, leftToRightDir);
    final Optional<ASTCDAssocDir> rightToLeftDir = p.parse_StringCDAssocDir("<-");
    checkNullAndPresence(p, rightToLeftDir);
    final Optional<ASTCDAssocDir> biDir = p.parse_StringCDAssocDir("<->");
    checkNullAndPresence(p, biDir);
    final Optional<ASTCDAssocDir> unspecifiedDir = p.parse_StringCDAssocDir("--");
    checkNullAndPresence(p, unspecifiedDir);
  }

  @Test
  public void parseCDAssocType() throws IOException {
    final Optional<ASTCDAssocType> association = p.parse_StringCDAssocType("association");
    checkNullAndPresence(p, association);
    final Optional<ASTCDAssocType> composition = p.parse_StringCDAssocType("composition");
    checkNullAndPresence(p, composition);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> parse =
        p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, parse);
  }

  @Test
  public void directCompositionTrafoTest() throws IOException {
    final Optional<ASTCDCompilationUnit> parse =
        p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, parse);

    final ASTCDCompilationUnit node = parse.get();

    // class B has 2 direct compositions
    assertEquals(2, ((ASTCDClass) node.getCDDefinition().getCDElement(2)).sizeCDMembers());
    // the cd has 7 associations or compositions
    assertEquals(7, node.getCDDefinition().getCDAssociationsList().size());

    TestCDAssociationTraverser t = TestCDAssociationMill.traverser();
    CDAssociationDirectCompositionTrafo trafo = new CDAssociationDirectCompositionTrafo();
    t.add4CDAssociation(trafo);
    t.add4CDBasis(trafo);
    node.accept(t);

    TestCDAssociationMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    // class B has 0 direct compositions
    assertEquals(0, ((ASTCDClass) node.getCDDefinition().getCDElement(2)).sizeCDMembers());
    // the cd has 9 associations or compositions
    assertEquals(9, node.getCDDefinition().getCDAssociationsList().size());
    // make sure the transformation did not add a package
    assertEquals(0, node.getCDDefinition().getCDPackagesList().size());
  }
}
