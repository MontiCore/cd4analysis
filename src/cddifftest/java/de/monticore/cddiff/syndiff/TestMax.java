package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.OD.ODHelper;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cddiff.syndiff.imp.TestHelper;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;
import org.junit.Test;

import java.util.Set;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;

public class TestMax extends CDDiffTestBasis {
  @Test
  public void test5() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/Performance/5A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    diff.getHelper().setMaps();
    diff.findOverlappingAssocs();
//    ASTCDClass a = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
//    ASTCDClass a1 = CDTestHelper.getClass("A1", compilationUnitNew.getCDDefinition());
//    ASTCDClass a2 = CDTestHelper.getClass("A2", compilationUnitNew.getCDDefinition());
//    ASTCDClass a3 = CDTestHelper.getClass("A3", compilationUnitNew.getCDDefinition());
//    ASTCDClass a4 = CDTestHelper.getClass("A4", compilationUnitNew.getCDDefinition());
//
//    ASTCDClass a1old = CDTestHelper.getClass("A1", compilationUnitOld.getCDDefinition());
//    ASTCDClass a2old = CDTestHelper.getClass("A2", compilationUnitOld.getCDDefinition());
//    ASTCDClass a5 = CDTestHelper.getClass("A5", compilationUnitOld.getCDDefinition());
//    ASTCDClass a3old = CDTestHelper.getClass("A3", compilationUnitOld.getCDDefinition());
//    ASTCDClass a4old = CDTestHelper.getClass("A4", compilationUnitOld.getCDDefinition());
//
//    assert a2 != null;
//    ASTCDAttribute i = CDTestHelper.getAttribute(a2, "i");
//    assert a3 != null;
//    ASTCDAttribute s = CDTestHelper.getAttribute(a3, "s");
//    assert a4 != null;
//    ASTCDAttribute doub = CDTestHelper.getAttribute(a4, "d");
//    assert a4old != null;
//
//
//
//    ASTCDAssociation a1a2 = CDTestHelper.getAssociation(a1, "a2", compilationUnitNew.getCDDefinition());
//    ASTCDAssociation a2a4 = CDTestHelper.getAssociation(a2, "a4", compilationUnitNew.getCDDefinition());
//    ASTCDAssociation a4a3 = CDTestHelper.getAssociation(a4, "a3", compilationUnitNew.getCDDefinition());
//    ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());
//
//    ASTCDAssociation a1a2old = CDTestHelper.getAssociation(a1old, "a2", compilationUnitOld.getCDDefinition());
//    ASTCDAssociation a2a4old = CDTestHelper.getAssociation(a2old, "a4", compilationUnitOld.getCDDefinition());
//    ASTCDAssociation a2a3old = CDTestHelper.getAssociation(a2old, "a3", compilationUnitOld.getCDDefinition());
//    ASTCDAssociation a3a5old = CDTestHelper.getAssociation(a3old, "a5", compilationUnitOld.getCDDefinition());
//
//
//
//    assert a != null;
//    boolean isClassAdded = diff.isSupClass(a);
//    CDTypeDiff typeDiff = new CDTypeDiff(a2, a2old, scopeNew, scopeOld);
//    boolean addedAtt = typeDiff.isAdded(i, compilationUnitNew);
//
//    CDTypeDiff typeDiff2 = new CDTypeDiff(a3, a3old, scopeNew, scopeOld);
//    boolean addedAtt2 = typeDiff2.isAdded(s, compilationUnitNew);
//    CDTypeDiff typeDiff3 = new CDTypeDiff(a4, a4old, scopeNew, scopeOld);
//    typeDiff3.setChangedMembers(new ArrayList<>());
//    Pair<ASTCDClass, List<ASTCDAttribute>> changedAtt = typeDiff3.changedAttribute();
//
//    CDAssocDiff assocDiff = new CDAssocDiff(a1a2, a1a2old);
//    Pair<ASTCDAssociation, List<Pair<ClassSide, Integer>>> cradDiff = assocDiff.getCardDiff();
//    CDAssocDiff assocDiff2 = new CDAssocDiff(a2a4, a2a4old);
//    Pair<ASTCDAssociation, List<Pair<ClassSide, Integer>>> cardDiff2 = assocDiff2.getCardDiff();
//    CDAssocDiff assocDiff3 = new CDAssocDiff(a2a3, a2a3old);
//    Pair<ASTCDAssociation, List<Pair<ClassSide, Integer>>> cardDiff3 = assocDiff3.getCardDiff();
//
//    Assert.assertFalse(isClassAdded);
//    Assert.assertTrue(addedAtt);
//    Assert.assertTrue(addedAtt2);
//    System.out.println(changedAtt);
//    System.out.println(cradDiff);
//    System.out.println(cardDiff2);
//    System.out.println(cardDiff3);
//
//    assert a4a3 != null;
//    boolean isAddedAssoc = diff.isAddedAssoc(a4a3);
//    ASTCDClass isAssocDeleted = diff.isAssocDeleted(a3a5old, a3old);
//    Assert.assertTrue(isAddedAssoc);
//    System.out.println(isAssocDeleted);

    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void test10() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/Performance/10A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/10B.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    diff.getHelper().setMaps();
    diff.findOverlappingAssocs();
    TestHelper testHelper = new TestHelper(diff);
    for (CDAssocDiff association : diff.getChangedAssocs()){
      System.out.println(getConnectedClasses(association.getSrcElem(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(association.getSrcElem(), diff.getSrcCD()).b.getName());
    }
    for (CDTypeDiff typeDiff : diff.getChangedTypes()){
      System.out.println(typeDiff.getSrcElem().getName());
    }
//    testHelper.staDiff();
//    testHelper.deletedAssocs();
//    testHelper.srcExistsTgtNot();
//    testHelper.changedTypes();
//    testHelper.inheritanceDiffs();
//    testHelper.changedAssocs();
//    testHelper.addedConstants();
//    testHelper.addedClasses();
//    testHelper.addedAssocs();
  }

  @Test
  public void test15() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/Performance/15A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/15B.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void test20() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/Performance/20A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/20B.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void test25() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/Performance/25A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/25B.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testDE() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/DEv2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/DEv1.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testEA() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/EAv2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/EAv1.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testEMT() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/EMTv1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/EMTv2.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testLibrary1() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV1.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testLibrary2() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV3.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV2.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testLibrary3() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV4.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV3.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testLibrary4() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV5.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV4.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testManagement() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/ManagementV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/ManagementV1.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testMyCompany() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/MyCompanyV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/MyCompanyV1.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testMyExample() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/MyExampleV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/MyExampleV1.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testMyLife() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/MyLifeV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/MyLifeV1.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testTeaching() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/TeachingV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/TeachingV1.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testBuilder() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/ODBuilder.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    diff.getHelper().setMaps();

    ODHelper odHelper = new ODHelper();

    ASTCDClass a2 = CDTestHelper.getClass("A4", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

//    for (AssocStruct assocStruct : diff.getHelper().getSrcMap().get(a2)) {
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).b.getName());
//    }
//    Set<Package> test = odHelper.createChains(new HashSet<>(), diff.getHelper().getSrcMap().get(a2));
//    List<Package> test1 = new ArrayList<>(test);
//    System.out.println(getConnectedClasses(test1.get(0).getAstcdAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(test1.get(0).getAstcdAssociation(), diff.getSrcCD()).b.getName());
    Set<ASTODElement> set = odHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
      if (element instanceof ASTODObject){
        System.out.println("Object");
        System.out.println(((ASTODObject) element).getMCObjectType().printType());
      }
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
      }
    }
  }

  @Test
  public void testBuilder2(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/Builder2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    diff.getHelper().setMaps();

    ODHelper odHelper = new ODHelper();

    ASTCDClass a2 = CDTestHelper.getClass("A5", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = odHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
      }
    }
  }
}
