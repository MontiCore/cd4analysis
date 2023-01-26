package de.monticore.conformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;

public class BasicAssocConfStrategy implements ConformanceStrategy<ASTCDAssociation> {
  @Override
  public boolean checkConformance(ASTCDAssociation concrete) {
    // todo: check types, role names, navigation and cardinalities
    return false;
  }
}
