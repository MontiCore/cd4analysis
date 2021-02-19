/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdinterfaceandenum;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._symboltable.ICDBasisGlobalScope;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._symboltable.ICDInterfaceAndEnumGlobalScope;
import de.monticore.cdinterfaceandenum.cocos.CDInterfaceAndEnumCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;
import org.junit.Before;

import java.nio.file.Paths;

public class CDInterfaceAndEnumTestBasis extends TestBasis {
  protected TestCDInterfaceAndEnumParser p;
  protected CDInterfaceAndEnumCoCos cdInterfaceAndEnumCoCos;

  @Before
  public void initObjects() {
    CDInterfaceAndEnumMill.reset();
    CDInterfaceAndEnumMill.init();
    p = new TestCDInterfaceAndEnumParser();

    final ICDInterfaceAndEnumGlobalScope globalScope = CDInterfaceAndEnumMill
        .globalScope();
    globalScope.clear();
    globalScope.setModelPath(new ModelPath(Paths.get(PATH)));

    cdInterfaceAndEnumCoCos = new CDInterfaceAndEnumCoCos();
  }
}
