package de.monticore.cddiff.syntaxdiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.syntaxdiff.AssoDiff;
import de.monticore.syntaxdiff.ClassDiff;
import de.monticore.syntaxdiff.FieldDiff;
import de.monticore.syntaxdiff.SyntaxDiff;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class SyntaxDiffTest extends CDDiffTestBasis {

  protected final ASTCDCompilationUnit cd1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/syntaxdiff/FieldDiffTest1.cd");

  protected final ASTCDCompilationUnit cd2 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/syntaxdiff/FieldDiffTest2.cd");

  protected final ASTCDCompilationUnit cd1Match = parseModel(
    "src/cddifftest/resources/de/monticore/cddiff/syntaxdiff/ClassMatch1.cd");

  protected final ASTCDCompilationUnit cd2Match = parseModel(
    "src/cddifftest/resources/de/monticore/cddiff/syntaxdiff/ClassMatch2.cd");

  @Override
  protected ASTCDCompilationUnit parseModel(String modelFile) {
    Path model = Paths.get(modelFile);
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      //assertFalse(parser.hasErrors());
      assertTrue(optAutomaton.isPresent());

      return optAutomaton.get();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + modelFile + ": " + e.getMessage());
    }

    return null;
  }

  @Before
  public void buildSymTable(){
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);
  }

  @Test
  public void testClassMatching(){
    SyntaxDiff syntaxDiff = new SyntaxDiff(cd1Match,cd2Match);
    List<ClassDiff> matchedClasses = syntaxDiff.getMatchedClassList();
    List<ASTCDClass> deletedClasses = syntaxDiff.getDeletedClasses();
    List<ASTCDClass> addedClasses = syntaxDiff.getAddedClasses();

    Assert.assertEquals(matchedClasses.get(0).getCd1Element().getName().equals("MatchAttributeChange")
      ,matchedClasses.get(0).getCd2Element().getName().equals("MatchAttributeChange"));

    Assert.assertEquals(matchedClasses.get(1).getCd1Element().getName().equals("MatchSignatureChange")
      ,matchedClasses.get(1).getCd2Element().getName().equals("MatchSignatureChange"));

    Assert.assertEquals(matchedClasses.get(2).getCd1Element().getName().equals("NoChanges")
      ,matchedClasses.get(2).getCd2Element().getName().equals("NoChanges"));

    assertEquals("DeletedClass", deletedClasses.get(0).getName());
    assertEquals("AddedClass", addedClasses.get(0).getName());
  }

  @Test
  public void testAssociationMatching(){
    SyntaxDiff syntaxDiff = new SyntaxDiff(cd1Match,cd2Match);
    List<AssoDiff> matchedAssos = syntaxDiff.getMatchedAssos();
    List<ASTCDAssociation> deletedAssos = syntaxDiff.getDeletedAssos();
    List<ASTCDAssociation> addedAssos = syntaxDiff.getAddedAssos();

    Assert.assertEquals(matchedAssos.get(0).getCd1Element().getName().equals("addStar")
      ,matchedAssos.get(0).getCd2Element().getName().equals("addStar"));

    assertEquals("deletedAssociation", deletedAssos.get(0).getName());
    assertEquals("insertedAssociation", addedAssos.get(0).getName());
  }


  @Test
  public void testFieldDiff() {

    // Associations
    List<ASTCDAssociation> cd1AssociationsList = cd1.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> cd2AssociationsList = cd2.getCDDefinition().getCDAssociationsList();


    // Asso Type
    FieldDiff<ASTCDAssocType> assoTypeEqual = new FieldDiff<>(
      Optional.of(cd1AssociationsList.get(0).getCDAssocType())
      ,Optional.of(cd2AssociationsList.get(0).getCDAssocType()));
    Assert.assertFalse(assoTypeEqual.isPresent());

    FieldDiff<ASTCDAssocType> assoTypeUnequal = new FieldDiff<>(
      Optional.of(cd1AssociationsList.get(1).getCDAssocType())
      ,Optional.of(cd2AssociationsList.get(1).getCDAssocType()));
    Assert.assertEquals(assoTypeUnequal.getOperation().get(), SyntaxDiff.Op.CHANGE);

    // Cardinality
    FieldDiff<ASTCDCardinality> assoCardiEqual = new FieldDiff<>(
      Optional.of(cd1AssociationsList.get(0).getLeft().getCDCardinality())
      ,Optional.of(cd2AssociationsList.get(0).getLeft().getCDCardinality()));
    Assert.assertFalse(assoCardiEqual.isPresent());

    FieldDiff<ASTCDAssociationNode> assoCardiDelete = new FieldDiff<>(
      Optional.of(cd1AssociationsList.get(1).getLeft().getCDCardinality())
      ,Optional.empty());
    Assert.assertEquals(assoCardiDelete.getOperation().get(), SyntaxDiff.Op.DELETE);

    FieldDiff<ASTCDAssociationNode> assoCardiAdd = new FieldDiff<>(
      Optional.empty()
      ,Optional.of(cd2AssociationsList.get(1).getRight().getCDCardinality()));
    Assert.assertEquals(assoCardiAdd.getOperation().get(), SyntaxDiff.Op.ADD);

    FieldDiff<ASTCDAssociationNode> assoCardiChange = new FieldDiff<>(
      Optional.of(cd1AssociationsList.get(0).getRight().getCDCardinality())
      ,Optional.of(cd2AssociationsList.get(0).getRight().getCDCardinality()));
    Assert.assertEquals(assoCardiChange.getOperation().get(), SyntaxDiff.Op.CHANGE);

    // Role
    FieldDiff<ASTCDRole> assoRoleEqual = new FieldDiff<>(
      Optional.of(cd1AssociationsList.get(0).getLeft().getCDRole())
      ,Optional.of(cd2AssociationsList.get(0).getLeft().getCDRole()));
    Assert.assertFalse(assoRoleEqual.isPresent());

    FieldDiff<ASTCDAssociationNode> assoRoleDelete = new FieldDiff<>(
      Optional.of(cd1AssociationsList.get(0).getRight().getCDRole())
      ,Optional.empty());
    Assert.assertEquals(assoRoleDelete.getOperation().get(), SyntaxDiff.Op.DELETE);

    FieldDiff<ASTCDAssociationNode> assoRoleAdd = new FieldDiff<>(
      Optional.empty()
      ,Optional.of(cd1AssociationsList.get(0).getRight().getCDRole()));
    Assert.assertEquals(assoRoleAdd.getOperation().get(), SyntaxDiff.Op.ADD);

    FieldDiff<ASTCDRole> assoRoleUnequal = new FieldDiff<>(
      Optional.of(cd1AssociationsList.get(1).getRight().getCDRole())
      ,Optional.of(cd2AssociationsList.get(1).getRight().getCDRole()));
    Assert.assertEquals(assoRoleUnequal.getOperation().get(), SyntaxDiff.Op.CHANGE);
  }
}
