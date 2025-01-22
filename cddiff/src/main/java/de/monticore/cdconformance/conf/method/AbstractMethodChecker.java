package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.conf.ICDMethodChecker;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

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
      if (conPar.getMCType().getDefiningSymbol().isPresent()
          && conPar.getMCType().getDefiningSymbol().get() instanceof CDTypeSymbol) {
        CDTypeSymbol conParType = (CDTypeSymbol) conPar.getMCType().getDefiningSymbol().get();
        if (conParType.isPresentAstNode()) {
          return typeMatcher.getMatchedElements(conParType.getAstNode()).stream()
              .anyMatch(
                  refType ->
                      refType
                          .getSymbol()
                          .getInternalQualifiedName()
                          .contains(refPar.getMCType().printType()));
        }
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
    if (conReturn.getMCType().getDefiningSymbol().isPresent()
        && conReturn.getMCType().getDefiningSymbol().get() instanceof CDTypeSymbol) {
      CDTypeSymbol conParType = (CDTypeSymbol) conReturn.getMCType().getDefiningSymbol().get();
      if (conParType.isPresentAstNode()) {
        return typeMatcher.getMatchedElements(conParType.getAstNode()).stream()
            .anyMatch(
                refType ->
                    refType
                        .getSymbol()
                        .getInternalQualifiedName()
                        .contains(refReturn.getMCType().printType()));
      }
    }
    return conReturn.deepEquals(refReturn);
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
