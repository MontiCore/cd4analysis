/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntaxdiff;

import static org.junit.Assert.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CDSyntaxDiffTest extends CDDiffTestBasis {
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

  protected final ASTCDCompilationUnit cd1 =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/FieldDiffTest1.cd");

  protected final ASTCDCompilationUnit cd2 =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/FieldDiffTest2.cd");

  protected final ASTCDCompilationUnit cd1ClassMatch =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/ClassMatch1.cd");

  protected final ASTCDCompilationUnit cd2ClassMatch =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/ClassMatch2.cd");

  protected final ASTCDCompilationUnit cd1EnumMatch =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/EnumMatch1.cd");

  protected final ASTCDCompilationUnit cd2EnumMatch =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/EnumMatch2.cd");

  protected final ASTCDCompilationUnit cd1ClassInterMatch =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/ClassToInterface1.cd");

  protected final ASTCDCompilationUnit cd2ClassInterMatch =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/ClassToInterface2.cd");

  @Override
  protected ASTCDCompilationUnit parseModel(String modelFile) {
    Path model = Paths.get(modelFile);
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      // assertFalse(parser.hasErrors());
      assertTrue(optAutomaton.isPresent());

      return optAutomaton.get();
    } catch (Exception e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + modelFile + ": " + e.getMessage());
    }

    return null;
  }

  @Before
  public void buildSymTable() {
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);
    new CD4CodeDirectCompositionTrafo().transform(cd1ClassMatch);
    new CD4CodeDirectCompositionTrafo().transform(cd2ClassMatch);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1ClassMatch);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2ClassMatch);

    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1EnumMatch);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2EnumMatch);

    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1ClassInterMatch);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2ClassInterMatch);
  }

  @Test
  public void testClassMatching() {
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(cd1ClassMatch, cd2ClassMatch);
    List<CDTypeDiff<ASTCDClass, ASTCDClass>> matchedClasses = syntaxDiff.getMatchedClassList();
    List<ASTCDClass> deletedClasses = syntaxDiff.getDeletedClasses();
    List<ASTCDClass> addedClasses = syntaxDiff.getAddedClasses();

    Assert.assertEquals(
        matchedClasses.get(0).getCd1Element().getName().equals("MatchAttributeChange"),
        matchedClasses.get(0).getCd2Element().getName().equals("MatchAttributeChange"));

    Assert.assertEquals(
        matchedClasses.get(1).getCd1Element().getName().equals("MatchSignatureChange"),
        matchedClasses.get(1).getCd2Element().getName().equals("MatchSignatureChange"));

    Assert.assertEquals(
        matchedClasses.get(2).getCd1Element().getName().equals("NoChanges"),
        matchedClasses.get(2).getCd2Element().getName().equals("NoChanges"));

    assertEquals("DeletedClass", deletedClasses.get(0).getName());
    assertEquals("AddedClass", addedClasses.get(0).getName());
  }

  @Test
  public void testEnumMatching() {
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(cd1EnumMatch, cd2EnumMatch);
    List<CDTypeDiff<ASTCDEnum, ASTCDEnum>> matched = syntaxDiff.getMatchedEnumList();
    List<ASTCDEnum> deleted = syntaxDiff.getDeletedEnums();
    List<ASTCDEnum> added = syntaxDiff.getAddedEnums();

    Assert.assertEquals(
        matched.get(0).getCd1Element().getName().equals("EnumConstReorder"),
        matched.get(0).getCd2Element().getName().equals("EnumConstReorder"));

    Assert.assertEquals(
        matched.get(1).getCd1Element().getName().equals("EnumConstDelReorder"),
        matched.get(1).getCd2Element().getName().equals("EnumConstDelReorder"));

    assertEquals("DeletedEnum", deleted.get(0).getName());
    assertEquals("AddedEnum", added.get(0).getName());
  }

  @Test
  public void testClassToInterfaceMatching() {
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(cd1ClassInterMatch, cd2ClassInterMatch);
    List<CDTypeDiff<ASTCDClass, ASTCDInterface>> matchedClassInter =
        syntaxDiff.getMatchedClassInterfaceList();
    List<CDTypeDiff<ASTCDInterface, ASTCDClass>> matchedInterClass =
        syntaxDiff.getMatchedInterfaceClassList();

    Assert.assertEquals(
        matchedClassInter.get(0).getInterpretationList().get(0),
        CDSyntaxDiff.Interpretation.REPURPOSED);
    Assert.assertEquals(
        matchedInterClass.get(0).getInterpretationList().get(0),
        CDSyntaxDiff.Interpretation.REPURPOSED);
  }

  @Test
  public void testAssociationMatching() {
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(cd1ClassMatch, cd2ClassMatch);
    List<CDAssociationDiff> matchedAssos = syntaxDiff.getMatchedAssos();
    List<ASTCDAssociation> deletedAssos = syntaxDiff.getDeletedAssos();
    List<ASTCDAssociation> addedAssos = syntaxDiff.getAddedAssos();

    Assert.assertEquals(
        matchedAssos
            .get(0)
            .getCd1Element()
            .getLeft()
            .getMCQualifiedType()
            .getNameList()
            .get(0)
            .equals("MatchAttributeChange"),
        matchedAssos
            .get(0)
            .getCd2Element()
            .getLeft()
            .getMCQualifiedType()
            .getNameList()
            .get(0)
            .equals("MatchAttributeChange"));

    assertEquals(
        "DeletedClass", deletedAssos.get(0).getRight().getMCQualifiedType().getNameList().get(0));
    assertEquals(
        "AddedClass", addedAssos.get(0).getRight().getMCQualifiedType().getNameList().get(0));
  }

  @Test
  public void testFieldDiff() {

    // Associations
    List<ASTCDAssociation> cd1AssociationsList = cd1.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> cd2AssociationsList = cd2.getCDDefinition().getCDAssociationsList();

    // Asso Type
    ASTNodeDiff<ASTCDAssocType, ASTCDAssocType> assoTypeEqual =
        new ASTNodeDiff<>(
            Optional.of(cd1AssociationsList.get(0).getCDAssocType()),
            Optional.of(cd2AssociationsList.get(0).getCDAssocType()));
    Assert.assertFalse(assoTypeEqual.isPresent());

    ASTNodeDiff<ASTCDAssocType, ASTCDAssocType> assoTypeUnequal =
        new ASTNodeDiff<>(
            Optional.of(cd1AssociationsList.get(1).getCDAssocType()),
            Optional.of(cd2AssociationsList.get(1).getCDAssocType()));
    Assert.assertEquals(assoTypeUnequal.getOperation().get(), CDSyntaxDiff.Op.CHANGE);

    // Cardinality
    ASTNodeDiff<ASTCDCardinality, ASTCDCardinality> assoCardiEqual =
        new ASTNodeDiff<>(
            Optional.of(cd1AssociationsList.get(0).getLeft().getCDCardinality()),
            Optional.of(cd2AssociationsList.get(0).getLeft().getCDCardinality()));
    Assert.assertFalse(assoCardiEqual.isPresent());

    ASTNodeDiff<ASTCDAssociationNode, ASTCDAssociationNode> assoCardiDelete =
        new ASTNodeDiff<>(
            Optional.of(cd1AssociationsList.get(1).getLeft().getCDCardinality()), Optional.empty());
    Assert.assertEquals(assoCardiDelete.getOperation().get(), CDSyntaxDiff.Op.DELETE);

    ASTNodeDiff<ASTCDAssociationNode, ASTCDAssociationNode> assoCardiAdd =
        new ASTNodeDiff<>(
            Optional.empty(),
            Optional.of(cd2AssociationsList.get(1).getRight().getCDCardinality()));
    Assert.assertEquals(assoCardiAdd.getOperation().get(), CDSyntaxDiff.Op.ADD);

    ASTNodeDiff<ASTCDAssociationNode, ASTCDAssociationNode> assoCardiChange =
        new ASTNodeDiff<>(
            Optional.of(cd1AssociationsList.get(0).getRight().getCDCardinality()),
            Optional.of(cd2AssociationsList.get(0).getRight().getCDCardinality()));
    Assert.assertEquals(assoCardiChange.getOperation().get(), CDSyntaxDiff.Op.CHANGE);

    // Role
    ASTNodeDiff<ASTCDRole, ASTCDRole> assoRoleEqual =
        new ASTNodeDiff<>(
            Optional.of(cd1AssociationsList.get(0).getLeft().getCDRole()),
            Optional.of(cd2AssociationsList.get(0).getLeft().getCDRole()));
    Assert.assertFalse(assoRoleEqual.isPresent());

    ASTNodeDiff<ASTCDAssociationNode, ASTCDAssociationNode> assoRoleDelete =
        new ASTNodeDiff<>(
            Optional.of(cd1AssociationsList.get(0).getRight().getCDRole()), Optional.empty());
    Assert.assertEquals(assoRoleDelete.getOperation().get(), CDSyntaxDiff.Op.DELETE);

    ASTNodeDiff<ASTCDAssociationNode, ASTCDAssociationNode> assoRoleAdd =
        new ASTNodeDiff<>(
            Optional.empty(), Optional.of(cd1AssociationsList.get(0).getRight().getCDRole()));
    Assert.assertEquals(assoRoleAdd.getOperation().get(), CDSyntaxDiff.Op.ADD);

    ASTNodeDiff<ASTCDRole, ASTCDRole> assoRoleUnequal =
        new ASTNodeDiff<>(
            Optional.of(cd1AssociationsList.get(1).getRight().getCDRole()),
            Optional.of(cd2AssociationsList.get(1).getRight().getCDRole()));
    Assert.assertEquals(assoRoleUnequal.getOperation().get(), CDSyntaxDiff.Op.CHANGE);
  }
}
