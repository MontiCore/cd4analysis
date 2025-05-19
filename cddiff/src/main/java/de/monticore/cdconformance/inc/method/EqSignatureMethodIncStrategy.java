package de.monticore.cdconformance.inc.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;
import de.monticore.cdmatcher.MatchingStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Matches methods by their signature (name & parameter types). This can be used to support
 * overloading (multiple methods with same name but different parameter types), as it is possible,
 * e.g., in Java.
 * It is still configurable whether the order of the parameters matters or not.
 * A parameter matches the reference parameter if the type of the parameter is one of the
 * following:
 * - exactly the same type as the reference parameter type
 * - an incarnation of the reference parameter type
 * - the reference type is underspecified
 */
public class EqSignatureMethodIncStrategy implements CDMethodMatchingStrategy {
  private final MCTypeMatcher typeMatcher;
  private final boolean strictParameterOrder;

  private ASTCDType refType;

  public EqSignatureMethodIncStrategy(MCTypeMatcher typeMatcher, boolean strictParameterOrder) {
    this.typeMatcher = typeMatcher;
    this.strictParameterOrder = strictParameterOrder;
  }

  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return refType.getCDMethodList().stream()
            .filter(method -> isMatched(concrete, method))
            .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    if (ref.getName().equals(concrete.getName())
            && ref.getCDParameterList().size() == concrete.getCDParameterList().size()) {
      return strictParameterOrder
              ? isStrictParameterMatch(concrete.getCDParameterList(), ref.getCDParameterList())
              : isParameterMatchWithoutOrder(concrete.getCDParameterList(), ref.getCDParameterList());
    }
    return false;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
  }

  private boolean isStrictParameterMatch(
          List<ASTCDParameter> conParams, List<ASTCDParameter> refParams) {
    for (int i = 0; i < conParams.size(); i++) {
      ASTCDParameter conParam = conParams.get(i);
      ASTCDParameter refParam = refParams.get(i);
      if (!typeMatcher.isMCTypeMatched(conParam.getMCType(), refParam.getMCType())) {
        return false;
      }
    }
    return true;
  }

  private boolean isParameterMatchWithoutOrder(
          List<ASTCDParameter> conParams, List<ASTCDParameter> refParams) {
    for (ASTCDParameter refParam : refParams) {
      if (conParams.stream().noneMatch(conParam
              -> typeMatcher.isMCTypeMatched(conParam.getMCType(), refParam.getMCType()))) {
        return false;
      }
    }
    return true;
  }
}
