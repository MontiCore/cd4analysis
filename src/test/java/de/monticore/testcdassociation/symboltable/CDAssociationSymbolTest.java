package de.monticore.testcdassociation.symboltable;

import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.ICDAssociationArtifactScope;
import de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.testcdassociation.TestCDAssociationMill;
import de.monticore.testcdassociation._symboltable.ITestCDAssociationArtifactScope;
import de.monticore.testcdassociation._visitor.TestCDAssociationTraverser;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDAssociationSymbolTest extends CDAssociationTestBasis {

  @Test
  public void testAssociationPackageNames() {
    ICDAssociationArtifactScope artifactScope = processModel("cdassociation/symboltable/SerializationCD.cd");

    final Optional<CDTypeSymbol> b = artifactScope.resolveCDType("A");
    assertTrue(b.isPresent());

    final Optional<CDAssociationSymbol> assocSymbol = artifactScope.resolveCDAssociation("namedAssoc");
    assertTrue(assocSymbol.isPresent());

    assertEquals(b.get().getPackageName(), assocSymbol.get().getPackageName());

    checkLogError();
  }

  public ICDAssociationArtifactScope processModel(String model) {
    final ASTCDCompilationUnit astcdCompilationUnit = parseModel(model);

    // after parse trafo
    TestCDAssociationTraverser t = TestCDAssociationMill.traverser();
    CDAssociationDirectCompositionTrafo trafo = new CDAssociationDirectCompositionTrafo();
    t.add4CDBasis(trafo);
    t.add4CDAssociation(trafo);
    astcdCompilationUnit.accept(t);

    ITestCDAssociationArtifactScope artifactScope = createSymTab(astcdCompilationUnit);
    completeSymTab(astcdCompilationUnit);

    checkLogError();
    return artifactScope;
  }
}
