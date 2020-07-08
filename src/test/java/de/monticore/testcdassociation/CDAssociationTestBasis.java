/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdassociation;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._symboltable.CDAssociationGlobalScope;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCreatorDelegator;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;

import java.nio.file.Paths;

public class CDAssociationTestBasis extends TestBasis {
  protected final TestCDAssociationParser p = new TestCDAssociationParser();
  protected final CDAssociationGlobalScope globalScope = CDAssociationMill
      .cDAssociationGlobalScopeBuilder()
      .setModelPath(new ModelPath(Paths.get(PATH)))
      .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
      .build();
  protected final CDAssociationSymbolTableCreatorDelegator symbolTableCreator = CDAssociationMill
      .cDAssociationSymbolTableCreatorDelegatorBuilder()
      .setGlobalScope(globalScope)
      .build();
  protected final CDAssociationCoCos cdAssociationCoCos = new CDAssociationCoCos();
}
