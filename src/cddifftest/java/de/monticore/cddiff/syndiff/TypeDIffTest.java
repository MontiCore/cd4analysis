package de.monticore.cddiff.syndiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

public class TypeDIffTest extends CDDiffTestBasis {

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }
  //TODO: add test for added/deleted inheritance
  /*@Test
  public void testCD2() {

    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD21.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD22.cd");
    ICD4CodeArtifactScope scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    ICD4CodeArtifactScope scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();

    ASTCDClass bNew = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    ASTCDClass bOld = CDTestHelper.getClass("B", compilationUnitOld.getCDDefinition());
    ASTCDClass aNew = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    ASTCDClass aOld = CDTestHelper.getClass("A", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(bNew, bOld, scopeNew, scopeOld);
    CDTypeDiff typeDiff1 = new CDTypeDiff(aNew, aOld, scopeNew, scopeOld);
    // Prepare test data
    assert bNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(bNew, "age");
    assert aOld != null;
    ASTCDAttribute attributeOld = CDTestHelper.getAttribute(aOld, "age");

    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    // Invoke the method
    boolean result = typeDiff.isAdded(attributeNew, compilationUnitOld);
    boolean result2 = typeDiff1.isDeleted(attributeOld, compilationUnitNew);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    //ASTCDType result3 = typeDiff.isClassNeeded();

    // Assert the result
    System.out.println(syntaxDiff.getHelper().getTrgMap().keySet());
    System.out.println(syntaxDiff.getHelper().getSrcMap().keySet());
    Assert.assertFalse(result);
    Assert.assertFalse(result2);
    for (ASTCDClass astcdClass : compilationUnitNew.getCDDefinition().getCDClassesList()){
      System.out.println(astcdClass.getName());
    }
    System.out.println(attributeNew.printType());
    //Assert.assertTrue(syntaxDiff.getHelper().getSrcMap().containsKey(aNew));
    //Assert.assertNull(result3);
  }

  @Test
  public void testCD1() {

    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD12.cd");

    ASTCDClass cNew = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("C", compilationUnitOld.getCDDefinition());
    ASTCDClass aNew = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    ASTCDClass aOld = CDTestHelper.getClass("A", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(cNew, cOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    CDTypeDiff typeDiff1 = new CDTypeDiff(aNew, aOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    // Prepare test data
    assert cNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(cNew, "age");
    assert aOld != null;
    ASTCDAttribute attributeOld = CDTestHelper.getAttribute(aOld, "age");

    // Invoke the method
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    boolean result = typeDiff.isAdded(attributeNew, compilationUnitOld);
    boolean result2 = typeDiff1.isDeleted(attributeOld, compilationUnitNew);

    // Assert the result
    Assert.assertFalse(result);
    Assert.assertTrue(result2);
  }

  @Test
  public void testCD3(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD32.cd");

    ASTCDClass bNew = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    ASTCDClass bOld = CDTestHelper.getClass("B", compilationUnitOld.getCDDefinition());
    ASTCDClass aNew = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    ASTCDClass aOld = CDTestHelper.getClass("A", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(bNew, bOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    CDTypeDiff typeDiff1 = new CDTypeDiff(aNew, aOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    // Prepare test data
    assert bNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(bNew, "age");
    assert aOld != null;
    ASTCDAttribute astcdAttribute = CDTestHelper.getAttribute(aOld, "age");
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    //Error in getAllSuper(only in setMaps()) from CDDiffUtil - Nullpointer


    // Invoke the method
    boolean result = typeDiff.isAdded(attributeNew, compilationUnitOld);
    ASTCDType result2 = typeDiff1.isClassNeeded();
    boolean result3 = typeDiff1.isDeleted(astcdAttribute, compilationUnitNew);


    // Assert the result
    Assert.assertTrue(result);
    Assert.assertNotNull(result2);
    Assert.assertTrue(result3);
  }

  @Test
  public void testCD4(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD41.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD42.cd");

    ASTCDClass bNew = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    ASTCDClass bOld = CDTestHelper.getClass("B", compilationUnitOld.getCDDefinition());
    ASTCDClass cNew = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("C", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(bNew, bOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    CDTypeDiff typeDiff2 = new CDTypeDiff(cNew, cOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    // Prepare test data
    assert bNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(bNew, "age");
    assert cOld != null;
    ASTCDAttribute attributeOld = CDTestHelper.getAttribute(cOld, "name");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    // Invoke the method
    boolean result = typeDiff.isAdded(attributeNew, compilationUnitOld);
    boolean result2 = typeDiff2.isDeleted(attributeOld, compilationUnitOld);


    // Assert the result
    Assert.assertFalse(result);
    Assert.assertFalse(result2);
  }

  @Test
  public void testCD5(){

    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD41.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD42.cd");

    ASTCDClass bNew = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    ASTCDClass bOld = CDTestHelper.getClass("B", compilationUnitOld.getCDDefinition());
    ASTCDClass cNew = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("C", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(bNew, bOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    CDTypeDiff typeDiff2 = new CDTypeDiff(cNew, cOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    // Prepare test data
    assert bNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(bNew, "age");
    assert cOld != null;
    ASTCDAttribute attributeOld = CDTestHelper.getAttribute(cOld, "name");

    // Invoke the method
    boolean result = typeDiff.isAdded(attributeNew, compilationUnitOld);
    boolean result2 = typeDiff2.isDeleted(attributeOld, compilationUnitOld);
    ASTCDClass result3 = (ASTCDClass) typeDiff2.isClassNeeded();

    // Assert the result
    Assert.assertFalse(result);
    Assert.assertFalse(result2);
    Assert.assertNotNull(result3);
  }

  @Test
  public void testCD7(){

    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD71.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD72.cd");
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    ASTCDClass aNew = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    ASTCDClass aOld = CDTestHelper.getClass("A", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(aNew, aOld, (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope(), (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    // Prepare test data
    assert aNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(aNew, "age");
    assert aOld != null;
    ASTCDAttribute attributeOld = CDTestHelper.getAttribute(aOld, "age");

    CDMemberDiff memberDiff = new CDMemberDiff(attributeNew, attributeOld);

    // Invoke the method
    System.out.println(syntaxDiff.helper.minDiffWitness(aNew).getName());
    Pair<ASTCDClass, ASTCDAttribute> result = typeDiff.findMemberDiff(memberDiff);

    // Assert the result
    System.out.println(result);
    Assert.assertNotNull(result);
  }*/

  /*--------------------------------------------------------------------*/
  //Syntax Diff Tests

  public static final String dir = "src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;
  @Test
  public void testType1() {
    parseModels("Source1.cd", "Target1.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("A", tgt.getCDDefinition());
    ICD4CodeArtifactScope scopeSrcCD = (ICD4CodeArtifactScope) src.getEnclosingScope();
    ICD4CodeArtifactScope scopeTgtCD = (ICD4CodeArtifactScope) tgt.getEnclosingScope();

    CDTypeDiff typeDiff = new CDTypeDiff(astcdClass, astcdClass1, scopeSrcCD, scopeTgtCD);
    System.out.println(typeDiff.printTgtCD());
    System.out.println(typeDiff.getBaseDiff());
    System.out.println(typeDiff.getSuperTypes());
    //System.out.println(typeDiff.getChangedMembers());
    //System.out.println(typeDiff.getMatchedAttributes());
  }

  @Test
  public void testType2() {
    parseModels("Source2.cd", "Target2.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("A", tgt.getCDDefinition());
    ICD4CodeArtifactScope scopeSrcCD = (ICD4CodeArtifactScope) src.getEnclosingScope();
    ICD4CodeArtifactScope scopeTgtCD = (ICD4CodeArtifactScope) tgt.getEnclosingScope();

    CDTypeDiff typeDiff = new CDTypeDiff(astcdClass, astcdClass1, scopeSrcCD, scopeTgtCD);
    System.out.println(typeDiff.printTgtCD());
    System.out.println(typeDiff.getSuperTypes());
    //System.out.println(typeDiff.printCD1());
    System.out.println(typeDiff.getBaseDiff());
  }

  @Test
  public void testType3() {
    parseModels("Source3.cd", "Target3.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("A", tgt.getCDDefinition());
    ICD4CodeArtifactScope scopeSrcCD = (ICD4CodeArtifactScope) src.getEnclosingScope();
    ICD4CodeArtifactScope scopeTgtCD = (ICD4CodeArtifactScope) tgt.getEnclosingScope();

    CDTypeDiff typeDiff = new CDTypeDiff(astcdClass, astcdClass1, scopeSrcCD, scopeTgtCD);
    System.out.println(typeDiff.printSrcCD());
    System.out.println(typeDiff.printTgtCD());
  }

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src =
        CD4CodeMill.parser().parseCDCompilationUnit(dir + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(src.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(tgt.get());
        src.get().accept(new CD4CodeSymbolTableCompleter(src.get()).getTraverser());
        tgt.get().accept(new CD4CodeSymbolTableCompleter(tgt.get()).getTraverser());
        this.tgt = tgt.get();
        this.src = src.get();
      } else {
        fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
