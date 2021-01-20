/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;

public class CD4CodeTrafo4Defaults {
  protected CD4CodeTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;
  protected final CD4CodeDelegatorVisitor symbolTableCreator;

  public CD4CodeTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CD4CodeMill.cD4CodeSymbolTableCreatorDelegator());
  }

  public CD4CodeTrafo4Defaults(CD4CodeDelegatorVisitor symbolTableCreator) {
    this(new CDAfterParseHelper(), symbolTableCreator);
  }

  public CD4CodeTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4CodeDelegatorVisitor symbolTableCreator) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.symbolTableCreator = symbolTableCreator;
    this.traverser = CD4CodeMill.traverser();

    init(cdAfterParseHelper, symbolTableCreator, traverser);
  }

  public static void init(CDAfterParseHelper cdAfterParseHelper, CD4CodeDelegatorVisitor symbolTableCreator, CD4CodeTraverser traverser) {
    final CDAssociationRoleNameTrafo cdAssociation = new CDAssociationRoleNameTrafo(cdAfterParseHelper, symbolTableCreator.getCDAssociationVisitor().get());
    traverser.add4CDAssociation(cdAssociation);
    traverser.setCDAssociationHandler(cdAssociation);
  }

  public CD4CodeTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeTraverser traverser) {
    this.traverser = traverser;
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    if (!compilationUnit.getCDDefinition().isPresentSymbol()) {
      final String msg = "0xCD0B3: can't start the transformation, the symbol table is missing";
      Log.error(msg);
      throw new RuntimeException(msg);
    }

    compilationUnit.accept(getTraverser());
    symbolTableCreator.endVisit(compilationUnit);
  }
}
