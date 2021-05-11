/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;

public class CD4CodeAfterParseTrafo {
  protected CD4CodeTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;

  public CD4CodeAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4CodeAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.traverser = CD4CodeMill.traverser();

    init(cdAfterParseHelper, traverser);
  }

  public static void init(CDAfterParseHelper cdAfterParseHelper, CD4CodeTraverser traverser) {
    final CDBasisDefaultPackageTrafo cdBasis = new CDBasisDefaultPackageTrafo(cdAfterParseHelper);
    traverser.add4CDBasis(cdBasis);
    traverser.setCDBasisHandler(cdBasis);
    cdBasis.setTraverser(traverser);

    CD4CodeDirectCompositionTrafo.init(cdAfterParseHelper, traverser);
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
