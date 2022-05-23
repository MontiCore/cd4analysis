package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.collect.Sets;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class CD2DGGeneratorTest extends CDDiffTestBasis {

  @Test
  public void testCreateDiffClassForSimpleClassAndAbstractClass() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    Map<String, DiffClass> diffClassGroup = cd2DGGenerator.createDiffClassForSimpleClassAndAbstractClass(cd, scope);
  }

  @Test
  public void testCreateDiffClassForInterface() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    Map<String, DiffClass> diffClassGroup = cd2DGGenerator.createDiffClassForInterface(cd, scope);
  }

  @Test
  public void testCreateDiffClassForEnum() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    cd2DGGenerator.createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    cd2DGGenerator.createDiffClassForInterface(cd, scope);
    Map<String, DiffClass> diffClassGroup = cd2DGGenerator.createDiffClassForEnum(cd, scope);
    System.out.println(diffClassGroup);
  }

  @Test
  public void testCreateInheritanceGraph() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    cd2DGGenerator.createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    cd2DGGenerator.createDiffClassForInterface(cd, scope);
    cd2DGGenerator.createDiffClassForEnum(cd, scope);
    System.out.println(cd2DGGenerator.inheritanceGraph.toString());
  }

  @Test
  public void testCreatEnumClassMapHelper() {
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    cd2DGGenerator.enumClassMap.put("DiffEnum_PositionKind", Sets.newHashSet("DiffClass_Manager"));
    cd2DGGenerator.creatEnumClassMapHelper("DiffEnum_PositionKind", "DiffClass_Employee");
    Map<String, Set<String>> enumClassMap = cd2DGGenerator.creatEnumClassMapHelper("DiffEnum_Department", "DiffClass_Employee");
    Map<String, Set<String>> map = new HashMap<>();
    map.put("DiffEnum_PositionKind", Sets.newHashSet("DiffClass_Manager", "DiffClass_Employee"));
    map.put("DiffEnum_Department", Sets.newHashSet("DiffClass_Employee"));
    Assert.assertTrue(enumClassMap.equals(map));
  }

  @Test
  public void testCreateDiffAssociation() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    cd2DGGenerator.createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    cd2DGGenerator.createDiffClassForInterface(cd, scope);
    cd2DGGenerator.createDiffClassForEnum(cd, scope);
    Map<String, DiffAssociation> diffAssociationGroup = cd2DGGenerator.createDiffAssociation(cd);
    System.out.println(diffAssociationGroup);
  }

  @Test
  public void testGetAllInheritancePath4DiffClass() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    cd2DGGenerator.createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    cd2DGGenerator.createDiffClassForInterface(cd, scope);
    cd2DGGenerator.createDiffClassForEnum(cd, scope);
    cd2DGGenerator.createDiffAssociation(cd);
    DiffClass diffClass = cd2DGGenerator.diffClassGroup.get("DiffClass_Manager");
    List<List<String>> list = cd2DGGenerator.getAllInheritancePath4DiffClass(diffClass);
    list.forEach(s -> System.out.println(s));
  }

  @Test
  public void testGetAllBottomNode() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    cd2DGGenerator.createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    cd2DGGenerator.createDiffClassForInterface(cd, scope);
    cd2DGGenerator.createDiffClassForEnum(cd, scope);
    cd2DGGenerator.createDiffAssociation(cd);
    Set<String> set = cd2DGGenerator.getAllBottomNode();
    System.out.println(set);
  }

  @Test
  public void testSolveInheritance() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    cd2DGGenerator.createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    cd2DGGenerator.createDiffClassForInterface(cd, scope);
    cd2DGGenerator.createDiffClassForEnum(cd, scope);
    cd2DGGenerator.createDiffAssociation(cd);
    cd2DGGenerator.solveInheritance();
    System.out.println(cd2DGGenerator.diffClassGroup);
    System.out.println(cd2DGGenerator.diffAssociationGroup);
  }

  @Test
  public void testGenerateDifferentGroup() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    DifferentGroup differentGroup = cd2DGGenerator.generateDifferentGroup(cd, DifferentGroup.DifferentGroupType.SINGLE_INSTANCE);
    System.out.println(differentGroup);
  }
}
