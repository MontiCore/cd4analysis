/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.od4report._parser.OD4ReportParser;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odlink._ast.ASTODLink;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class JoinLinksTrafoTest extends CDDiffTestBasis {

  @Test
  public void testEmployeesInstance() {
    try {
      ASTCDCompilationUnit cd =
          parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees" + "/Employees2.cd");
      ASTODArtifact od =
          new OD4ReportParser()
              .parse(
                  "src/cddifftest/resources/de/monticore/cddiff/JoinLinksTrafo/EmployeesInstance.od")
              .get();
      new JoinLinksTrafo(cd).transform(od);
      Assert.assertEquals(
          3,
          od.getObjectDiagram().getODElementList().stream()
              .filter(element -> element instanceof ASTODLink)
              .collect(Collectors.toSet())
              .size());
      Log.print(System.lineSeparator() + OD4ReportMill.prettyPrint(od, true));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}
