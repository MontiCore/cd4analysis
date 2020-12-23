/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdassociation;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._symboltable.ICDAssociationGlobalScope;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;
import org.junit.Before;

import java.nio.file.Paths;

public class CDAssociationTestBasis extends TestBasis {
  protected TestCDAssociationParser p;
  protected CDAssociationCoCos cdAssociationCoCos;

  @Before
  public void initObjects() {
    CDAssociationMill.reset();
    CDAssociationMill.init();
    p = new TestCDAssociationParser();

    final ICDAssociationGlobalScope globalScope = CDAssociationMill
        .globalScope();
    globalScope.clear();
    globalScope.setModelPath(new ModelPath(Paths.get(PATH)));
    globalScope.setFileExt(CD4AnalysisGlobalScope.EXTENSION);

    cdAssociationCoCos = new CDAssociationCoCos();
  }
}
