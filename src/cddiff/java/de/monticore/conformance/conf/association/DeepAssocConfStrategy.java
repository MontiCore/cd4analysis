package de.monticore.conformance.conf.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.matcher.MatchingStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;

public class DeepAssocConfStrategy extends BasicAssocConfStrategy {

  public DeepAssocConfStrategy(
      ASTCDCompilationUnit refCD,
      ASTCDCompilationUnit conCD,
      MatchingStrategy<ASTCDType> typeInc,
      MatchingStrategy<ASTCDAssociation> assocInc,
      boolean allowCardRefinement) {
    super(refCD, conCD, typeInc, assocInc, allowCardRefinement);
  }

  @Override
  protected boolean checkReference(String concrete, String ref) {
    Optional<CDTypeSymbol> conTypeSymbol = conCD.getEnclosingScope().resolveCDTypeDown(concrete);
    Optional<CDTypeSymbol> refTypeSymbol = refCD.getEnclosingScope().resolveCDTypeDown(ref);

    if (conTypeSymbol.isPresent() && refTypeSymbol.isPresent()) {
      ASTCDType conType = conTypeSymbol.get().getAstNode();
      ASTCDType refType = refTypeSymbol.get().getAstNode();
      return typeInc.isMatched(conType, refType)
          || CDDiffUtil.getAllStrictSubTypes(conType, conCD.getCDDefinition()).stream()
              .anyMatch(conSub -> typeInc.isMatched(conSub, refType));
    }
    Log.error("0xCDD17: Could not resolve association reference!");
    return false;
  }
}
