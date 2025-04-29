package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.conf.ICDMethodChecker;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import java.util.Optional;

public abstract class AbstractMethodChecker implements ICDMethodChecker {
  protected String mapping;
  protected ASTCDType conType;
  protected ASTCDType refType;
  protected MatchingStrategy<ASTCDType> typeMatcher;

  @Override
  public boolean checkConformance(ASTCDMethod concrete, ASTCDMethod ref) {
    return checkReturnTypeConformance(concrete.getMCReturnType(), ref.getMCReturnType())
        && ref.getCDParameterList().stream()
            .allMatch(
                refPar ->
                    concrete.getCDParameterList().stream()
                        .anyMatch(conPar -> checkParameterConformance(conPar, refPar)));
  }

  protected boolean checkParameterConformance(ASTCDParameter conPar, ASTCDParameter refPar) {
    if (conPar.getName().equals(refPar.getName())) {
      Optional<Boolean> conParType = checkTypeIncarnation(refPar.getMCType(), conPar.getMCType());
      if (conParType.isPresent()) {
        return conParType.get();
      }
      return conPar.getMCType().deepEquals(refPar.getMCType());
    }
    return false;
  }

  protected boolean checkReturnTypeConformance(
      ASTMCReturnType conReturn, ASTMCReturnType refReturn) {
    if (refReturn.printType().equals("void")) {
      return true;
    }
    Optional<Boolean> conReturnType =
        checkTypeIncarnation(refReturn.getMCType(), conReturn.getMCType());
    if (conReturnType.isPresent()) {
      return conReturnType.get();
    }
    return conReturn.getMCType().deepEquals(refReturn.getMCType());
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
  public ASTCDType getReferenceType() {
    return refType;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
  }

  @Override
  public ASTCDType getConcreteType() {
    return conType;
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.conType = conType;
  }
}
