/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdassociation;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCreatorDelegator;
import de.monticore.cdassociation._symboltable.ICDAssociationGlobalScope;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;
import org.junit.Before;

import java.nio.file.Paths;

public class CDAssociationTestBasis extends TestBasis {
  protected TestCDAssociationParser p;
  protected ICDAssociationGlobalScope globalScope;
  protected CDAssociationSymbolTableCreatorDelegator symbolTableCreator;
  protected CDAssociationCoCos cdAssociationCoCos;

  @Before
  public void initObjects() {
    CDAssociationMill.init();
    p = new TestCDAssociationParser();
    globalScope = CDAssociationMill
        .cDAssociationGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .build();
    symbolTableCreator = CDAssociationMill
        .cDAssociationSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();
    cdAssociationCoCos = new CDAssociationCoCos();
  }
}
