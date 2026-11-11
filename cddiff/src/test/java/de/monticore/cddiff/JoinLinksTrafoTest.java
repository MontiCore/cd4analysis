/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odlink._ast.ASTODLink;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class JoinLinksTrafoTest extends CDDiffTestBasis {
  
  @Test
  public void testEmployeesInstance() {
    try {
      ASTCDCompilationUnit cd = parseModel("src/test/resources/de/monticore/cddiff/Employees"
          + "/Employees2.cd");
      OD4ReportMill.init();
      Optional<ASTODArtifact> od = OD4ReportMill.parser().parse(
          "src/test/resources/de/monticore/cddiff/JoinLinksTrafo/EmployeesInstance.od");
      
      assertTrue(od.isPresent());
      new JoinLinksTrafo(cd).transform(od.get());
      assertEquals(3, od.get().getObjectDiagram().getODElementList().stream().filter(
          element -> element instanceof ASTODLink).collect(Collectors.toSet()).size());
      Log.print(System.lineSeparator() + OD4ReportMill.prettyPrint(od.get(), true));
    }
    catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
}
