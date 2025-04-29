package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public abstract class AbstractAttributeChecker implements CDAttributeChecker {

  protected final String mapping;
  protected final MatchingStrategy<ASTCDType> typeMatcher;
  protected ASTCDType conType;
  protected ASTCDType refType;

  protected AbstractAttributeChecker(String mapping, MatchingStrategy<ASTCDType> typeMatcher) {
    this.mapping = mapping;
    this.typeMatcher = typeMatcher;
  }

  @Override
  public boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref) {
    /*
     * An attribute conforms to the reference attribute if one of the following holds:
     * - the concrete attribute has the exact same type as the reference attribute
     * - the concrete attribute type is an incarnation of the reference attribute type
     * - TODO the reference attribute type is underspecified
     */
    ASTMCType conType = concrete.getMCType();
    ASTMCType refType = ref.getMCType();
    Optional<Boolean> conReturnType = checkTypeIncarnation(refType, conType);
    if (conReturnType.isPresent()) {
      return conReturnType.get();
    }
    return conType.deepEquals(refType);
  }

  protected Optional<Boolean> checkTypeIncarnation(ASTMCType refType, ASTMCType conType) {
    if (conType.getDefiningSymbol().isPresent()
            && conType.getDefiningSymbol().get() instanceof CDTypeSymbol
            && refType.getDefiningSymbol().isPresent()
            && refType.getDefiningSymbol().get() instanceof CDTypeSymbol) {
      CDTypeSymbol conCDType = (CDTypeSymbol) conType.getDefiningSymbol().get();
      CDTypeSymbol refCDType = (CDTypeSymbol) refType.getDefiningSymbol().get();
      if (conCDType.isPresentAstNode()) {
        return Optional.of(
                typeMatcher.getMatchedElements(conCDType.getAstNode()).stream()
                        .anyMatch(
                                r -> r.getSymbol().getFullName().equals(refCDType.getFullName())));
      }
    }
    return Optional.empty();
  }

  @Override
  public ASTCDType getConcreteType() {
    return conType;
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.conType = conType;
  }

  @Override
  public ASTCDType getReferenceType() {
    return refType;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
  }
}
