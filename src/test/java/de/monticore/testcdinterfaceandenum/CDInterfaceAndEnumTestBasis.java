/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdinterfaceandenum;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumGlobalScope;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCreatorDelegator;
import de.monticore.cdinterfaceandenum.cocos.CDInterfaceAndEnumCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;
import org.junit.Before;

import java.nio.file.Paths;

public class CDInterfaceAndEnumTestBasis extends TestBasis {
  protected TestCDInterfaceAndEnumParser p;
  protected CDInterfaceAndEnumGlobalScope globalScope;
  protected CDInterfaceAndEnumSymbolTableCreatorDelegator symbolTableCreator;
  protected CDInterfaceAndEnumCoCos cdInterfaceAndEnumCoCos;

  @Before
  public void initObjects() {
    p = new TestCDInterfaceAndEnumParser();
    globalScope = CDInterfaceAndEnumMill
        .cDInterfaceAndEnumGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .build();
    symbolTableCreator = CDInterfaceAndEnumMill
        .cDInterfaceAndEnumSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();
    cdInterfaceAndEnumCoCos = new CDInterfaceAndEnumCoCos();
  }
}
