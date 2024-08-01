package de.monticore.cdcoconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.odrulegeneration._ast.ASTAssociation;

public class DefaultAssocIncCompleter implements IncarnationCompleter<ASTAssociation> {

  protected ASTCDCompilationUnit rcd;
  protected ASTCDCompilationUnit ccd;

  protected String mapping;

  public DefaultAssocIncCompleter(
      ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD, String mapping) {
    this.rcd = refCD;
    this.ccd = conCD;
    this.mapping = mapping;
  }

  public void identifyAndCopyMissingAssociations(
      ASTCDCompilationUnit cccd, ASTCDCompilationUnit rcd) {
    //  not yet implemented, first doing types and attributes
  }

  @Override
  public void completeIncarnations() {}
}
