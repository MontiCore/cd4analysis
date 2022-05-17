package de.monticore.sydiff2semdiff.cd2dg;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.ow2cw.CDInheritanceHelper;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;

public class CDParserTest extends CDDiffTestBasis {

  @Test
  public void testReadCD() {

    ASTCDCompilationUnit cd1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/Test.cd");

    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);

    for (ASTCDClass astcdClass: cd1.getCDDefinition().getCDClassesList()) {
      System.out.println("ClassName: " + astcdClass.getName());
      List<ASTCDType> superList = CDInheritanceHelper.getAllSuper(astcdClass, scope1).stream().distinct().collect(Collectors.toList());
      superList.remove(astcdClass);
      superList.forEach(s -> System.out.println("SuperClass: " + s.getName()));


      astcdClass.getCDAttributeList().forEach(s -> System.out.println("attributes: " + s.getMCType()+ " " + s.getName()));
      System.out.println("----------------------------");
    }

  }
}
