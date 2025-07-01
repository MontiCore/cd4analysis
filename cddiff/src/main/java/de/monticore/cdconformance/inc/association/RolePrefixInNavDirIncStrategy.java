/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.booleanMatching.MatchCDAssocsBySrcTypeAndTgtRole;
import de.monticore.cdmatcher.caching.StructureCache;

public class RolePrefixInNavDirIncStrategy extends MatchCDAssocsBySrcTypeAndTgtRole {

  public RolePrefixInNavDirIncStrategy(BooleanMatchingStrategy<ASTCDType> typeMatcher, StructureCache structureCache) {
    super(typeMatcher, structureCache);
  }

  @Override
  protected boolean checkRole(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {
    if (srcElem.isPresentCDRole() && tgtElem.isPresentCDRole()) {
      return srcElem.getCDRole().getName().startsWith(tgtElem.getCDRole().getName());
    }
    return false;
  }

}
