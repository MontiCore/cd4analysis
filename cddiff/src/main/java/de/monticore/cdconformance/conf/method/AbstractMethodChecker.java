package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.ICDMethodChecker;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

public abstract class AbstractMethodChecker implements ICDMethodChecker {

  protected final String mapping;
  protected final TypeIncarnationHelper typeHelper;
  protected ASTCDType conType;
  protected ASTCDType refType;

  protected AbstractMethodChecker(String mapping, TypeIncarnationHelper typeHelper) {
    this.mapping = mapping;
    this.typeHelper = typeHelper;
  }

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
      return typeHelper.isMCTypeMatched(conPar.getMCType(), refPar.getMCType());
    }
    return false;
  }

  protected boolean checkReturnTypeConformance(
      ASTMCReturnType conReturn, ASTMCReturnType refReturn) {
    if (typeHelper.isVoidType(refReturn)) {
      /*
       * For methods, we treat 'void' as underspecification of the return type. Therefore, any
       * concrete return type is allowed.
       */
      return true;
    }
    if (typeHelper.isVoidType(conReturn)) {
      // a void return type is only allowed if the reference type is either void or underspecified
      return typeHelper.isUnderspecified(refReturn);
    }
    return typeHelper.isMCTypeMatched(conReturn.getMCType(), refReturn.getMCType());
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
