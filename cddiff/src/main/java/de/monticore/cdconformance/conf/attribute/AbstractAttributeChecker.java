package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;

public abstract class AbstractAttributeChecker implements CDAttributeChecker {

  protected final String mapping;
  protected final String underspecifiedTypeName;
  protected final MatchingStrategy<ASTCDType> typeMatcher;
  protected ASTCDType concreteType;
  protected ASTCDType referenceType;

  protected AbstractAttributeChecker(
      String mapping, String underspecifiedTypeName, MatchingStrategy<ASTCDType> typeMatcher) {
    this.mapping = mapping;
    this.underspecifiedTypeName = underspecifiedTypeName;
    this.typeMatcher = typeMatcher;
  }

  @Override
  public boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref) {
    /*
     * An attribute conforms to the reference attribute if one of the following holds:
     * - the concrete attribute has the exact same type as the reference attribute
     * - the concrete attribute type is an incarnation of the reference attribute type
     * - the reference attribute type is underspecified
     */
    ASTMCType conType = concrete.getMCType();
    ASTMCType refType = ref.getMCType();
    if (refType.printType().equals(underspecifiedTypeName)) {
      if (conType.printType().equals(underspecifiedTypeName)) {
        Log.error("The underspecified placeholder type is not allowed as a concrete type.");
        return false;
      }
      // every type is allowed if the reference type is underspecified
      return true;
    }
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
                .anyMatch(r -> r.getSymbol().getFullName().equals(refCDType.getFullName())));
      }
    }
    return Optional.empty();
  }

  @Override
  public ASTCDType getConcreteType() {
    return concreteType;
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.concreteType = conType;
  }

  @Override
  public ASTCDType getReferenceType() {
    return referenceType;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.referenceType = refType;
  }
}
