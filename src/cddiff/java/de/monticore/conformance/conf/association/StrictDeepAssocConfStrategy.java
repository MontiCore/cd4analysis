package de.monticore.conformance.conf.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.matcher.MatchingStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import java.util.Set;

public class StrictDeepAssocConfStrategy extends BasicAssocConfStrategy {

  public StrictDeepAssocConfStrategy(
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

      Set<ASTCDType> concreteTypes =
          CDDiffUtil.getAllStrictSubTypes(conType, conCD.getCDDefinition());
      concreteTypes.add(conType);

      return concreteTypes.stream()
          .allMatch(
              conType1 ->
                  checkRule1(conType1, refType)
                      || checkRule2(conType1, refType)
                      || checkRule3(conType1, refType));
    }
    Log.error("0xCDD17: Could not resolve association reference!");
    return false;
  }

  /***
   * the concrete type incarnate the reference type.
   */
  protected boolean checkRule1(ASTCDType concrete, ASTCDType ref) {
    return typeInc.isMatched(concrete, ref);
  }

  /***
   * the concrete type is abstract or an interface and has a subtype that incarnate
   * the reference type.
   */

  protected boolean checkRule2(ASTCDType concrete, ASTCDType ref) {
    return (concrete.getModifier().isAbstract() || ref.getSymbol().isIsInterface())
        && CDDiffUtil.getAllStrictSubTypes(concrete, conCD.getCDDefinition()).stream()
            .anyMatch(subtype -> typeInc.isMatched(subtype, ref));
  }

  /***
   *the concrete type inherit form an Incarnation of the reference type.
   */
  protected boolean checkRule3(ASTCDType concrete, ASTCDType ref) {
    return CDDiffUtil.getAllSuperTypes(concrete, conCD.getCDDefinition()).stream()
        .anyMatch(superType -> typeInc.isMatched(concrete, ref));
  }
}
