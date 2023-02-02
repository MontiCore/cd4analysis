/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.misc.CDBasisDirectCompositionTrafo;
import de.monticore.cd4analysis.trafo.CDInterfaceAndEnumDirectCompositionTrafo;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class CD4CodeDirectCompositionTrafo {
  protected CD4CodeTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;

  public CD4CodeDirectCompositionTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4CodeDirectCompositionTrafo(CDAfterParseHelper cdAfterParseHelper) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.traverser = CD4CodeMill.traverser();

    init(cdAfterParseHelper, traverser);
  }

  public static void init(CDAfterParseHelper cdAfterParseHelper, CD4CodeTraverser traverser) {
    final CDBasisDirectCompositionTrafo cdBasisDirectCompositionTrafo =
        new CDBasisDirectCompositionTrafo(cdAfterParseHelper);
    traverser.add4CDBasis(cdBasisDirectCompositionTrafo);
    traverser.setCDBasisHandler(cdBasisDirectCompositionTrafo);
    cdBasisDirectCompositionTrafo.setTraverser(traverser);

    final CDAssociationDirectCompositionTrafo cdAssociation =
        new CDAssociationDirectCompositionTrafo();
    traverser.add4CDAssociation(cdAssociation);
    traverser.add4CDBasis(cdAssociation);

    final CDInterfaceAndEnumDirectCompositionTrafo cDInterfaceAndEnum =
        new CDInterfaceAndEnumDirectCompositionTrafo(cdAfterParseHelper);
    traverser.add4CDInterfaceAndEnum(cDInterfaceAndEnum);
    traverser.setCDInterfaceAndEnumHandler(cDInterfaceAndEnum);
    cDInterfaceAndEnum.setTraverser(traverser);
  }

  public CD4CodeTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeTraverser traverser) {
    this.traverser = traverser;
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    compilationUnit.accept(getTraverser());
  }
}
