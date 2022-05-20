package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.collect.Sets;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDInheritanceHelper;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffSuperClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

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
  public void testCreateDiffSuperClass() {
    ASTCDCompilationUnit cd = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Employees1.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    cd2DGGenerator.createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    cd2DGGenerator.createDiffClassForInterface(cd, scope);
    cd2DGGenerator.createDiffClassForEnum(cd, scope);
    System.out.println(cd2DGGenerator.diffSuperClassGroup);
    System.out.println("aaa");

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
}
