package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import org.junit.Assert;
import org.junit.Test;

public class TypeDIffTest extends CDDiffTestBasis {

  //@Test
  public void testCD2() {

    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD21.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD22.cd");

    ASTCDClass bNew = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    ASTCDClass bOld = CDTestHelper.getClass("B", compilationUnitOld.getCDDefinition());
    ASTCDClass aNew = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    ASTCDClass aOld = CDTestHelper.getClass("A", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(bNew, bOld);
    CDTypeDiff typeDiff1 = new CDTypeDiff(aNew, aOld);
    // Prepare test data
    assert bNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(bNew, "age");
    assert aOld != null;
    ASTCDAttribute attributeOld = CDTestHelper.getAttribute(aOld, "age");

    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    // Invoke the method
    boolean result = typeDiff.isAdded(attributeNew, compilationUnitOld);
    boolean result2 = typeDiff1.isDeleted(attributeOld, compilationUnitNew);
    //ASTCDType result2 = typeDiff.isClassNeeded(syntaxDiff);

    // Assert the result
    System.out.print(Syn2SemDiffHelper.getSpannedInheritance(compilationUnitNew, aNew));
    Assert.assertFalse(result);
    Assert.assertFalse(result2);
  }

  //@Test
  public void testCD1() {

    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD12.cd");

    ASTCDClass cNew = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("C", compilationUnitOld.getCDDefinition());
    ASTCDClass aNew = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    ASTCDClass aOld = CDTestHelper.getClass("A", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(cNew, cOld);
    CDTypeDiff typeDiff1 = new CDTypeDiff(aNew, aOld);
    // Prepare test data
    assert cNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(cNew, "age");
    assert aOld != null;
    ASTCDAttribute attributeOld = CDTestHelper.getAttribute(aOld, "age");

    // Invoke the method
    boolean result = typeDiff.isAdded(attributeNew, compilationUnitOld);
    boolean result2 = typeDiff1.isDeleted(attributeOld, compilationUnitNew);

    // Assert the result
    Assert.assertFalse(result);
    Assert.assertTrue(result2);
  }

  //@Test
  public void testCD3(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD32.cd");

    ASTCDClass bNew = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    ASTCDClass bOld = CDTestHelper.getClass("B", compilationUnitOld.getCDDefinition());
    ASTCDClass aNew = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    ASTCDClass aOld = CDTestHelper.getClass("A", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(bNew, bOld);
    CDTypeDiff typeDiff1 = new CDTypeDiff(aNew, aOld);
    // Prepare test data
    assert bNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(bNew, "age");
    assert aOld != null;
    ASTCDAttribute astcdAttribute = CDTestHelper.getAttribute(aOld, "age");
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.setMaps();
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

  //@Test
  public void testCD4(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD41.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD42.cd");

    ASTCDClass bNew = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    ASTCDClass bOld = CDTestHelper.getClass("B", compilationUnitOld.getCDDefinition());
    ASTCDClass cNew = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("C", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(bNew, bOld);
    CDTypeDiff typeDiff2 = new CDTypeDiff(cNew, cOld);
    // Prepare test data
    assert bNew != null;
    ASTCDAttribute attributeNew = CDTestHelper.getAttribute(bNew, "age");
    assert cOld != null;
    ASTCDAttribute attributeOld = CDTestHelper.getAttribute(cOld, "name");

    // Invoke the method
    boolean result = typeDiff.isAdded(attributeNew, compilationUnitOld);
    boolean result2 = typeDiff2.isDeleted(attributeOld, compilationUnitOld);


    // Assert the result
    Assert.assertFalse(result);
    Assert.assertFalse(result2);
  }

  //@Test
  public void testCD5(){

    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD41.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD42.cd");

    ASTCDClass bNew = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    ASTCDClass bOld = CDTestHelper.getClass("B", compilationUnitOld.getCDDefinition());
    ASTCDClass cNew = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("C", compilationUnitOld.getCDDefinition());
    CDTypeDiff typeDiff = new CDTypeDiff(bNew, bOld);
    CDTypeDiff typeDiff2 = new CDTypeDiff(cNew, cOld);
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
}
