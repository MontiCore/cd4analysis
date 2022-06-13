package de.monticore.sydiff2semdiff.cg2od;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.od4data.OD4DataMill;
import de.monticore.od4data.prettyprinter.OD4DataFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import de.monticore.odlink.ODLinkMill;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sydiff2semdiff.cd2dg.CD2DGGenerator;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import de.monticore.sydiff2semdiff.dg2cg.DG2CGGenerator;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompareGroup;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CG2ODGeneratorTest extends CDDiffTestBasis {
  DifferentGroup dg1 = null;
  DifferentGroup dg2 = null;
  CompareGroup cg1 = null;
  CompareGroup cg2 = null;

  protected void generateCompareGroupTemp(String folder, String cd1Name, String cd2Name) {
    ASTCDCompilationUnit cd1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/GenerateOD/" + folder + "/" + cd1Name);

    ASTCDCompilationUnit cd2 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/GenerateOD/" + folder + "/" + cd2Name);
    CD2DGGenerator cd1Generator = new CD2DGGenerator();
    CD2DGGenerator cd2Generator = new CD2DGGenerator();
    dg1 = cd1Generator.generateDifferentGroup(cd1, DifferentGroup.DifferentGroupType.SINGLE_INSTANCE);
    dg2 = cd2Generator.generateDifferentGroup(cd2, DifferentGroup.DifferentGroupType.SINGLE_INSTANCE);
    DG2CGGenerator dg2CGGenerator4dg1Withdg2 = new DG2CGGenerator();
    DG2CGGenerator dg2CGGenerator4dg2Withdg1 = new DG2CGGenerator();
    cg1 = dg2CGGenerator4dg1Withdg2.generateCompareGroup(dg1, dg2);
    cg2 = dg2CGGenerator4dg2Withdg1.generateCompareGroup(dg2, dg1);
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  @Test
  public void testGenerateODByClass() {
    generateCompareGroupTemp("Class","Class1A.cd", "Class1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    for(int i = 0; i < resultList.size() ; i++) {
      System.out.println(resultList.get(i));
    }
  }

  /********************************************************************
   *******************    Start for Association    ********************
   *******************************************************************/

  @Test
  public void testGenerateODByAssociation() {
    generateCompareGroupTemp("Association","Association1A.cd", "Association1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    for(int i = 0; i < resultList.size() ; i++) {
      System.out.println(resultList.get(i));
    }
  }

  @Test
  public void testGenerateODByCircleAssociation() {
    generateCompareGroupTemp("Association","CircleTest1A.cd", "CircleTest1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    for(int i = 0; i < resultList.size() ; i++) {
      System.out.println(resultList.get(i));
    }
  }

  /********************************************************************
   *********************    Start for Combination    ******************
   *******************************************************************/

}
