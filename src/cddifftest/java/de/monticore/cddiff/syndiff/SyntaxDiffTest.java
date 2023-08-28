package de.monticore.cddiff.syndiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.matcher.*;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SyntaxDiffTest extends CDDiffTestBasis {

  NameTypeMatcher nameTypeMatch;
  StructureTypeMatcher structureTypeMatch;
  SuperTypeMatcher superTypeMatch;
  NameAssocMatcher nameAssocMatch;
  SrcTgtAssocMatcher associationSrcTgtMatch;
  List<MatchingStrategy<ASTCDType>> typeMatchers;
  List<MatchingStrategy<ASTCDAssociation>> assocMatchers;
  ICD4CodeArtifactScope scopeNew;
  ICD4CodeArtifactScope scopeOld;



  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }
  @Test
  public void ini(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC));
    System.out.println("------------");
    for (AssocStruct astcdClass : syntaxDiff.getHelper().getSrcMap().get(classC)){
      System.out.print(Syn2SemDiffHelper.getConnectedClasses(astcdClass.getUnmodifiedAssoc(), compilationUnitNew).a.getName() + "" + Syn2SemDiffHelper.getConnectedClasses(astcdClass.getUnmodifiedAssoc(), compilationUnitNew).b.getName());
    }
    //System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  @Test
  public void testCD1(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    for (AssocStruct assocStruct : syntaxDiff.getHelper().getSrcMap().get(classC)){
      System.out.println(assocStruct.getAssociation().getLeft().getCDRole().getName());
    }
    System.out.println("------------");
    for (ASTCDClass astcdClass : syntaxDiff.getHelper().getNotInstanClassesSrc()){
      System.out.print(astcdClass.getName());
    }
    //System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //@Test
  public void testCD2(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
    assertThrows(Exception.class, syntaxDiff::findOverlappingAssocs);
  }

  @Test
  public void testCD3(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC).size());
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA).size());
    for (AssocStruct astcdClass : syntaxDiff.getHelper().getSrcMap().get(classC)){
      System.out.print(Syn2SemDiffHelper.getConnectedClasses(astcdClass.getAssociation(), compilationUnitNew).a.getName() + "" + Syn2SemDiffHelper.getConnectedClasses(astcdClass.getAssociation(), compilationUnitNew).b.getName());
    }
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  @Test
  public void testCD4(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA));
    for (AssocStruct astcdClass : syntaxDiff.getHelper().getSrcMap().get(classA)){
      System.out.print(Syn2SemDiffHelper.getConnectedClasses(astcdClass.getAssociation(), compilationUnitNew).a.getName() + "" + Syn2SemDiffHelper.getConnectedClasses(astcdClass.getAssociation(), compilationUnitNew).b.getName());
      System.out.println(astcdClass.getAssociation().getRight().getCDCardinality().toString() + " " + astcdClass.getAssociation().getRight().getCDRole().getName());
    }
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  @Test
  public void testCD5(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  @Test
  public void testCD6(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    for (AssocStruct astcdClass : syntaxDiff.getHelper().getSrcMap().get(classA)){
      System.out.print(Syn2SemDiffHelper.getConnectedClasses(astcdClass.getAssociation(), compilationUnitNew).a.getName() + "" + Syn2SemDiffHelper.getConnectedClasses(astcdClass.getAssociation(), compilationUnitNew).b.getName());
      System.out.println(astcdClass.getAssociation().getRight().getCDCardinality().toString() + " " + astcdClass.getAssociation().getRight().getCDRole().getName());
      System.out.println(astcdClass.getAssociation().getLeft().getCDCardinality().toString() + " " + astcdClass.getAssociation().getLeft().getCDRole().getName());
    }
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //@Test
  public void testCD7(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD7.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD7.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  @Test
  public void test11(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD12.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classD = CDTestHelper.getClass("D", compilationUnitNew.getCDDefinition());
    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    assert classD != null;
    boolean isadded = syntaxDiff.isSupClass(classD);
    boolean isClassAdded = syntaxDiff.isInheritanceAdded(classD, classA);

    Assert.assertFalse(isadded);
    Assert.assertFalse(isClassAdded);
  }

  @Test
  public void test21(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD21.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD22.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classD = CDTestHelper.getClass("D", compilationUnitOld.getCDDefinition());
    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    boolean isClassDeleted = syntaxDiff.isClassDeleted(classD, classA);

    Assert.assertFalse(isClassDeleted);
  }

  @Test
  public void test31(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD32.cd");

    nameTypeMatch = new NameTypeMatcher(compilationUnitOld);
    structureTypeMatch = new StructureTypeMatcher(compilationUnitOld);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, compilationUnitNew, compilationUnitOld);
    nameAssocMatch = new NameAssocMatcher(compilationUnitOld);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, compilationUnitNew, compilationUnitOld);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    ASTCDClass classD1 = CDTestHelper.getClass("D", compilationUnitNew.getCDDefinition());
    ASTCDClass classD2 = CDTestHelper.getClass("D", compilationUnitOld.getCDDefinition());
    ASTCDAssociation associationNew = CDTestHelper.getAssociation(classD1, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation associationOld = CDTestHelper.getAssociation(classD2, "r", compilationUnitOld.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    scopeNew = (ICD4CodeArtifactScope) compilationUnitNew.getEnclosingScope();
    scopeOld = (ICD4CodeArtifactScope) compilationUnitOld.getEnclosingScope();
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, scopeNew, scopeOld, typeMatchers, assocMatchers);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    assert associationNew != null;
    boolean isAssocAdded = syntaxDiff.isAddedAssoc(associationNew);
    assert associationOld != null;
    Pair<ASTCDAssociation, List<ASTCDClass>> isAssocDeleted = syntaxDiff.deletedAssoc(associationOld);

    Assert.assertTrue(isAssocAdded);
    Assert.assertNull(isAssocDeleted);
  }

  /*--------------------------------------------------------------------*/
  //Syntax Diff Tests

  public static final String dir = "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;


  @Test
  public void testSyntax1() {
    parseModels("Source1.cd", "Target1.cd");

    nameTypeMatch = new NameTypeMatcher(tgt);
    structureTypeMatch = new StructureTypeMatcher(tgt);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, src, tgt);
    nameAssocMatch = new NameAssocMatcher(tgt);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, src, tgt);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);

    CDDiffUtil.refreshSymbolTable(src);
    CDDiffUtil.refreshSymbolTable(tgt);
    ICD4CodeArtifactScope scopeSrcCD = (ICD4CodeArtifactScope) src.getEnclosingScope();
    ICD4CodeArtifactScope scopeTgtCD = (ICD4CodeArtifactScope) tgt.getEnclosingScope();

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt, scopeSrcCD, scopeTgtCD, typeMatchers, assocMatchers);
    //System.out.println(syntaxDiff.print());
    System.out.println("Matched Assocs");
    System.out.println(syntaxDiff.getMatchedAssocs());
    System.out.println("Matched Classes");
    System.out.println(syntaxDiff.getMatchedClasses());
    System.out.println("Changed Classes");
    System.out.println(syntaxDiff.getChangedClasses());
    System.out.println(syntaxDiff.printSrcCD());
    System.out.println(syntaxDiff.printTgtCD());
    System.out.println(syntaxDiff.getBaseDiff());
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
