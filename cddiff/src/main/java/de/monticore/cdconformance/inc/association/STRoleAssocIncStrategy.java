/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.booleanMatching.MatchCDAssocsBySrcTypeAndTgtRole;
import de.monticore.cdmatcher.caching.StructureCache;

public class STRoleAssocIncStrategy extends MatchCDAssocsBySrcTypeAndTgtRole {

  protected String mapping;

  public STRoleAssocIncStrategy(BooleanMatchingStrategy<ASTCDType> typeMatcher, StructureCache structureCache, String mapping) {
    super(typeMatcher, structureCache);
    this.mapping = mapping;
  }

  @Override
  protected boolean checkRole(ASTCDAssocSide concrete, ASTCDAssocSide reference) {
    if (concrete.getModifier().isPresentStereotype() && concrete.getModifier().getStereotype()
        .contains(mapping) && reference.isPresentCDRole()) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return reference.getCDRole().getName().equals(refName);
    }
    return false;
  }

}
