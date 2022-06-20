package de.monticore.cddiff.syntaxdiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.syntaxdiff.AssoDiff;
import de.monticore.syntaxdiff.FieldDiff;
import de.monticore.syntaxdiff.SyntaxDiff;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class SyntaxDiffTest extends CDDiffTestBasis {

  @Test
  public void testSyntaxDiff() {
    CD4CodeMill.globalScope().clear();

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    final ASTCDCompilationUnit cd1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/syntaxdiff/FieldDiffTest1.cd");

    final ASTCDCompilationUnit cd2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/syntaxdiff/FieldDiffTest2.cd");


    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    gscope.clear();
    BuiltInTypes.addBuiltInTypes(gscope);

    //cd1.accept(new CD4CodeSymbolTableCompleter(cd1).getTraverser());
    //cd2.accept(new CD4CodeSymbolTableCompleter(cd2).getTraverser());

    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);

    //new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(cd1);
    //new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(cd2);

    // Associations
    List<ASTCDAssociation> cd1AssociationsList = cd1.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> cd2AssociationsList = cd2.getCDDefinition().getCDAssociationsList();


    // Stereotype not present
    Optional<ASTStereotype> cd1Stereo;
    if (cd1AssociationsList.get(0).getModifier().isPresentStereotype()) cd1Stereo =
      Optional.of(cd1AssociationsList.get(0).getModifier().getStereotype());
    else cd1Stereo = Optional.empty();
    Optional<ASTStereotype> cd2Stereo;
    if (cd2AssociationsList.get(0).getModifier().isPresentStereotype()) cd2Stereo =
      Optional.of(cd2AssociationsList.get(0).getModifier().getStereotype());
    else cd2Stereo = Optional.empty();

    FieldDiff<SyntaxDiff.Op, ASTStereotype> stereotypeNotPresent = SyntaxDiff.getFieldDiff(cd1Stereo,cd2Stereo);
    Assert.assertFalse(stereotypeNotPresent.isPresent());

    /*
    Optional<ASTStereotype> cd1StereoEqual;
    if (cd1AssociationsList.get(1).getModifier().isPresentStereotype()) cd1StereoEqual =
      Optional.of(cd1AssociationsList.get(1).getModifier().getStereotype());
    else cd1StereoEqual = Optional.empty();
    Optional<ASTStereotype> cd2StereoEqual;

    if (cd2AssociationsList.get(1).getModifier().isPresentStereotype()) cd2StereoEqual =
      Optional.of(cd2AssociationsList.get(1).getModifier().getStereotype());
    else cd2StereoEqual = Optional.empty();
    FieldDiff<SyntaxDiff.Op, ASTStereotype> stereotypeEqual = SyntaxDiff.getFieldDiff(cd1StereoEqual,cd2StereoEqual);
    if (cd1StereoEqual.isPresent()){
      System.out.println(pp.prettyprint(cd1StereoEqual.get()));
    }
    if (cd2StereoEqual.isPresent()){
      System.out.println(pp.prettyprint(cd2StereoEqual.get()));
    }
    Assert.assertTrue(stereotypeEqual.isPresent());


     */
    // Stereotype present and unequal
    Optional<ASTStereotype> cd1StereoUnequal;
    if (cd1AssociationsList.get(1).getModifier().isPresentStereotype()) cd1StereoUnequal =
      Optional.of(cd1AssociationsList.get(1).getModifier().getStereotype());
    else cd1StereoUnequal = Optional.empty();

    Optional<ASTStereotype> cd2StereoUnequal;
    if (cd2AssociationsList.get(1).getModifier().isPresentStereotype()) cd2StereoUnequal =
      Optional.of(cd2AssociationsList.get(1).getModifier().getStereotype());
    else cd2StereoUnequal = Optional.empty();
    FieldDiff<SyntaxDiff.Op, ASTStereotype> stereotypeUnequal = SyntaxDiff.getFieldDiff(cd1StereoUnequal,cd2StereoUnequal);
    Assert.assertTrue(stereotypeUnequal.isPresent());
    if (cd1StereoUnequal.isPresent() && cd2StereoUnequal.isPresent()){
      Assert.assertNotEquals(cd1StereoUnequal.get(), cd2StereoUnequal.get());
    }


    //Modifier
    FieldDiff<SyntaxDiff.Op, ASTModifier> modifierEqual = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getModifier())
      ,Optional.of(cd2AssociationsList.get(0).getModifier()));
    Assert.assertFalse(modifierEqual.isPresent());


    // Asso Type
    FieldDiff<SyntaxDiff.Op, ASTCDAssocType> assoTypeEqual = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getCDAssocType())
      ,Optional.of(cd2AssociationsList.get(0).getCDAssocType()));
    Assert.assertFalse(assoTypeEqual.isPresent());

    FieldDiff<SyntaxDiff.Op, ASTCDAssocType> assoTypeUnequal = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(1).getCDAssocType())
      ,Optional.of(cd2AssociationsList.get(1).getCDAssocType()));
    Assert.assertEquals(assoTypeUnequal.getOperation().get(), SyntaxDiff.Op.CHANGE);

    /*
    // Asso Name
    FieldDiff<SyntaxDiff.Op, ASTCDAssocType> assoNameEqual = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getName())
      ,Optional.of(cd2AssociationsList.get(0).getName()));
    Assert.assertFalse(assoNameEqual.isPresent());

    FieldDiff<SyntaxDiff.Op, ASTCDAssocType> assoNameUnequal = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getName())
      ,Optional.of(cd2AssociationsList.get(1).getName()));
    Assert.assertEquals(assoNameUnequal.getOperation().get(), SyntaxDiff.Op.CHANGE);
*/
    // Ordered Equal use asso 0
    FieldDiff<SyntaxDiff.Op, ASTCDOrdered> assoOrderedEqual = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getLeft().getCDOrdered())
      ,Optional.of(cd2AssociationsList.get(0).getLeft().getCDOrdered()));
    Assert.assertFalse(assoOrderedEqual.isPresent());

    // Ordered Deleted use asso 1
    FieldDiff<SyntaxDiff.Op, ASTCDOrdered> assoOrderedDelete = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(1).getLeft().getCDOrdered()),
      Optional.empty());
    Assert.assertEquals(assoOrderedDelete.getOperation().get(), SyntaxDiff.Op.DELETE);

    // Ordered Added use asso 1
    FieldDiff<SyntaxDiff.Op, ASTCDOrdered> assoOrderedAdd = SyntaxDiff.getFieldDiff(
      Optional.empty(),
      Optional.of(cd1AssociationsList.get(1).getLeft().getCDOrdered()));
    Assert.assertEquals(assoOrderedAdd.getOperation().get(), SyntaxDiff.Op.ADD);

    // Cardinality
    FieldDiff<SyntaxDiff.Op, ASTCDCardinality> assoCardiEqual = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getLeft().getCDCardinality())
      ,Optional.of(cd2AssociationsList.get(0).getLeft().getCDCardinality()));
    Assert.assertFalse(assoCardiEqual.isPresent());

    FieldDiff<SyntaxDiff.Op, ASTCDAssociationNode> assoCardiDelete = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(1).getLeft().getCDCardinality())
      ,Optional.empty());
    Assert.assertEquals(assoCardiDelete.getOperation().get(), SyntaxDiff.Op.DELETE);

    FieldDiff<SyntaxDiff.Op, ASTCDAssociationNode> assoCardiAdd = SyntaxDiff.getFieldDiff(
      Optional.empty()
      ,Optional.of(cd2AssociationsList.get(1).getRight().getCDCardinality()));
    Assert.assertEquals(assoCardiAdd.getOperation().get(), SyntaxDiff.Op.ADD);

    FieldDiff<SyntaxDiff.Op, ASTCDAssociationNode> assoCardiChange = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getRight().getCDCardinality())
      ,Optional.of(cd2AssociationsList.get(0).getRight().getCDCardinality()));
    Assert.assertEquals(assoCardiChange.getOperation().get(), SyntaxDiff.Op.CHANGE);

    // Quali Name
    FieldDiff<SyntaxDiff.Op, ASTMCQualifiedType> assoQualiNameEqual = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getLeft().getMCQualifiedType())
      ,Optional.of(cd2AssociationsList.get(0).getLeft().getMCQualifiedType()));
    Assert.assertFalse(assoQualiNameEqual.isPresent());

    FieldDiff<SyntaxDiff.Op, ASTMCQualifiedType> assoQualiNameUnequal = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getRight().getMCQualifiedType())
      ,Optional.of(cd2AssociationsList.get(0).getRight().getMCQualifiedType()));
    //Assert.assertEquals(assoQualiNameUnequal.getOperation().get(), SyntaxDiff.Op.CHANGE);

    // Qualifier
    FieldDiff<SyntaxDiff.Op, ASTCDQualifier> assoQualifierEqual = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getLeft().getCDQualifier())
      ,Optional.of(cd2AssociationsList.get(0).getLeft().getCDQualifier()));
    Assert.assertFalse(assoQualifierEqual.isPresent());

    FieldDiff<SyntaxDiff.Op, ASTCDAssociationNode> assoQualifierDelete = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getRight().getCDQualifier())
      ,Optional.empty());
    Assert.assertEquals(assoQualifierDelete.getOperation().get(), SyntaxDiff.Op.DELETE);

    FieldDiff<SyntaxDiff.Op, ASTCDAssociationNode> assoQualifierAdd = SyntaxDiff.getFieldDiff(
      Optional.empty()
      ,Optional.of(cd1AssociationsList.get(0).getRight().getCDQualifier()));
    Assert.assertEquals(assoQualifierAdd.getOperation().get(), SyntaxDiff.Op.ADD);

    /*
    FieldDiff<SyntaxDiff.Op, ASTCDQualifier> assoQualifierUnequal = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(1).getLeft().getCDQualifier())
      ,Optional.of(cd2AssociationsList.get(1).getLeft().getCDQualifier()));
    Assert.assertEquals(assoQualifierUnequal.getOperation().get(), SyntaxDiff.Op.CHANGE);
*/
    // Role
    FieldDiff<SyntaxDiff.Op, ASTCDRole> assoRoleEqual = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getLeft().getCDRole())
      ,Optional.of(cd2AssociationsList.get(0).getLeft().getCDRole()));
    Assert.assertFalse(assoRoleEqual.isPresent());

    FieldDiff<SyntaxDiff.Op, ASTCDAssociationNode> assoRoleDelete = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(0).getRight().getCDRole())
      ,Optional.empty());
    Assert.assertEquals(assoRoleDelete.getOperation().get(), SyntaxDiff.Op.DELETE);

    FieldDiff<SyntaxDiff.Op, ASTCDAssociationNode> assoRoleAdd = SyntaxDiff.getFieldDiff(
      Optional.empty()
      ,Optional.of(cd1AssociationsList.get(0).getRight().getCDRole()));
    Assert.assertEquals(assoRoleAdd.getOperation().get(), SyntaxDiff.Op.ADD);

    FieldDiff<SyntaxDiff.Op, ASTCDRole> assoRoleUnequal = SyntaxDiff.getFieldDiff(
      Optional.of(cd1AssociationsList.get(1).getRight().getCDRole())
      ,Optional.of(cd2AssociationsList.get(1).getRight().getCDRole()));
    Assert.assertEquals(assoRoleUnequal.getOperation().get(), SyntaxDiff.Op.CHANGE);
  }
}
