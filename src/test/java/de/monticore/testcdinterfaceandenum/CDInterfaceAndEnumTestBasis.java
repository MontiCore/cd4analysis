/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum;

import de.monticore.cd.TestBasis;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._symboltable.ICDInterfaceAndEnumGlobalScope;
import de.monticore.cdinterfaceandenum.cocos.CDInterfaceAndEnumCoCos;
import de.monticore.io.paths.MCPath;
import de.monticore.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;
import java.nio.file.Paths;
import org.junit.Before;

public class CDInterfaceAndEnumTestBasis extends TestBasis {
  protected TestCDInterfaceAndEnumParser p;
  protected CDInterfaceAndEnumCoCos cdInterfaceAndEnumCoCos;

  @Before
  public void initObjects() {
    CDInterfaceAndEnumMill.reset();
    CDInterfaceAndEnumMill.init();
    p = new TestCDInterfaceAndEnumParser();

    final ICDInterfaceAndEnumGlobalScope globalScope = CDInterfaceAndEnumMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    cdInterfaceAndEnumCoCos = new CDInterfaceAndEnumCoCos();
  }
}
