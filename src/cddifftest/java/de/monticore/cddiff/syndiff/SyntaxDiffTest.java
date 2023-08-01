package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import org.junit.Test;

public class SyntaxDiffTest extends CDDiffTestBasis {

  //@Test
  public void testCD1(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //@Test
  public void testCD2(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //@Test
  public void testCD3(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //@Test
  public void testCD4(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //@Test
  public void testCD5(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //@Test
  public void testCD6(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
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

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC));
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }
}
