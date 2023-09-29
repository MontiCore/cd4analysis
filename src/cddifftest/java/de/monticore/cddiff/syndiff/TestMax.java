package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syndiff.OD.DiffHelper;
import de.monticore.cddiff.syndiff.OD.DiffWitnessGenerator;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cddiff.syndiff.imp.TestHelper;
import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;

public class TestMax extends CDDiffTestBasis {
  @Test
  public void test5() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/Performance/5A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
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

  //@Test
  public void test10() {
    ASTCDCompilationUnit cd1 = parseModel("src/cddifftest/resources/validation/Performance/10A.cd");
    ASTCDCompilationUnit cd2 = parseModel("src/cddifftest/resources/validation/Performance/10B.cd");

    ASTCDCompilationUnit original1 = cd1.deepClone();
    ASTCDCompilationUnit original2 = cd2.deepClone();


    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

//    for (ASTODArtifact od : witnesses) {
//      Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//    }

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }

    // reduction-based
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(cd1, cd2);

//    for (ASTODArtifact od : witnesses) {
//      if (!new OD2CDMatcher()
//        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
//        System.out.println("Open World Fail");
//        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//        Assertions.fail();
//      }
//    }
}

  @Test
  public void test15() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/Performance/15A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/15B.cd");

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

  //@Test
  //@Disabled
  public void testMyLife() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/MyLifeV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/MyLifeV1.cd");

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
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/Builder.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator diffHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("A4", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

//    for (AssocStruct assocStruct : diff.getHelper().getSrcMap().get(a2)) {
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).b.getName());
//    }
//    Set<Package> test = odHelper.createChains(new HashSet<>(), diff.getHelper().getSrcMap().get(a2));
//    List<Package> test1 = new ArrayList<>(test);
//    System.out.println(getConnectedClasses(test1.get(0).getAstcdAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(test1.get(0).getAstcdAssociation(), diff.getSrcCD()).b.getName());
    Set<ASTODElement> set = diffHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
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

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator diffHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("A5", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = diffHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
      }
    }
  }

  @Test
  public void testBuilder3(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/Builder3.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator diffHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("A5", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = diffHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
      }
    }
  }

  @Test
  public void testBuilder4(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/Builder4.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator diffHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("A5", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = diffHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println("left RN: " + ((ASTODLink) element).getODLinkLeftSide().getRole());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
        System.out.println("right RN: " +((ASTODLink) element).getODLinkRightSide().getRole());
      }
    }
//    for (AssocStruct assocStruct : odHelper.getOtherAssoc(a2)){
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).b.getName());
//    }
  }

  @Test
  public void testBuilder5(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/Builder5.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator diffHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("A2", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = diffHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println("left RN: " + ((ASTODLink) element).getODLinkLeftSide().getRole());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
        System.out.println("right RN: " +((ASTODLink) element).getODLinkRightSide().getRole());
      }
    }
//    for (AssocStruct assocStruct : odHelper.getOtherAssoc(a2)){
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).b.getName());
//    }
  }

  @Test
  public void testBuilder6(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/Builder6.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator diffHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("A2", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = diffHelper.getObjForOD(a2);
    System.out.println(set.size());
    for (ASTODElement element : set) {
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println("left RN: " + ((ASTODLink) element).getODLinkLeftSide().getRole());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
        System.out.println("right RN: " +((ASTODLink) element).getODLinkRightSide().getRole());
      }
      if (element instanceof ASTODObject){
        System.out.println("Object");
        System.out.println(((ASTODObject) element).getName());
      }
    }
//    for (AssocStruct assocStruct : odHelper.getOtherAssoc(a2)){
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).b.getName());
//    }
  }

  @Test
  public void testBuilder7(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/Builder7.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator diffHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("A2", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = diffHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println("left RN: " + ((ASTODLink) element).getODLinkLeftSide().getRole());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
        System.out.println("right RN: " +((ASTODLink) element).getODLinkRightSide().getRole());
      }
    }
//    for (AssocStruct assocStruct : odHelper.getOtherAssoc(a2)){
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).b.getName());
//    }
  }

  @Test
  public void testBuilder8(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/Builder8.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator diffHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("B", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = diffHelper.getObjForOD(a2);
    for (ASTODElement element : set) {
      if (element instanceof ASTODLink){
        System.out.println("Link");
        System.out.println(((ASTODLink) element).getLeftReferenceNames());
        System.out.println("left RN: " + ((ASTODLink) element).getODLinkLeftSide().getRole());
        System.out.println(((ASTODLink) element).getRightReferenceNames());
        System.out.println("right RN: " +((ASTODLink) element).getODLinkRightSide().getRole());
      }
    }
//    for (AssocStruct assocStruct : odHelper.getOtherAssoc(a2)){
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).b.getName());
//    }
  }

  @Test
  public void testBuilder9(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/Performance/5A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);

    DiffWitnessGenerator odHelper = new DiffWitnessGenerator();

    ASTCDClass a2 = CDTestHelper.getClass("A3", compilationUnitNew.getCDDefinition());
    //ASTCDAssociation a2a3 = CDTestHelper.getAssociation(a2, "a3", compilationUnitNew.getCDDefinition());

    Set<ASTODElement> set = odHelper.getObjForOD(a2);
    ASTODArtifact artifact = DiffHelper.generateArtifact("test", new ArrayList<>(set), "someStereotype");


//    for (AssocStruct assocStruct : diff.helper.getSrcMap().get(a2)){
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).a.getName() + "====" + getConnectedClasses(assocStruct.getAssociation(), diff.getSrcCD()).b.getName());
//    }
  }

  //@Test
  public void testSem2OD(){
    ASTCDCompilationUnit cd1 = parseModel("src/cddifftest/resources/validation/Performance/5A.cd");
    ASTCDCompilationUnit cd2 = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    ASTCDCompilationUnit original1 = cd1.deepClone();
    ASTCDCompilationUnit original2 = cd2.deepClone();


    // reduction-based

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

//    for (ASTODArtifact od : witnesses) {
//      Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//    }

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(cd1, cd2);
//    for (ASTODArtifact od : witnesses) {
//      if (!new OD2CDMatcher()
//        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
//        System.out.println("Open World Fail");
//        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//        Assertions.fail();
//      }
//    }
  }
}
