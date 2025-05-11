package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.conf.ICDMethodChecker;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public abstract class AbstractMethodChecker implements ICDMethodChecker {

  protected final String mapping;
  protected final Set<CDConfParameter> params;
  protected final TypeIncarnationHelper typeHelper;
  protected ASTCDType conType;
  protected ASTCDType refType;

  protected AbstractMethodChecker(
          String mapping,
          Set<CDConfParameter> params,
          TypeIncarnationHelper typeHelper) {
    this.mapping = mapping;
    this.params = params;
    this.typeHelper = typeHelper;
  }

  @Override
  public boolean checkConformance(ASTCDMethod concrete, ASTCDMethod ref) {
    return checkReturnTypeConformance(concrete.getMCReturnType(), ref.getMCReturnType())
        && checkParameterListConformance(concrete.getCDParameterList(), ref.getCDParameterList());

  }

  protected boolean checkParameterListConformance(
          List<ASTCDParameter> conParams, List<ASTCDParameter> refParams) {
    if (params.contains(CDConfParameter.IGNORE_PARAMETER_ORDER)) {
      return refParams.stream()
              .allMatch(refPar ->
                  conParams.stream()
                          .anyMatch(conPar -> checkParameterConformance(conPar, refPar)));
    } else {
      return IntStream.range(0, conParams.size())
              .allMatch(i -> checkParameterConformance(conParams.get(i), refParams.get(i)));
    }
  }

  protected boolean checkParameterConformance(ASTCDParameter conPar, ASTCDParameter refPar) {
    if (params.contains(CDConfParameter.IGNORE_PARAMETER_ORDER)
            && !conPar.getName().equals(refPar.getName())) {
      return false;
    }
    return typeHelper.isMCTypeMatched(conPar.getMCType(), refPar.getMCType());
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
