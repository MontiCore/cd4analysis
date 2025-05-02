package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Matches methods by name and parameter signature. In order to support overloading, the
 * strategy treats the signature (name & parameter types) as a whole as it is done in Java and other
 * languages to identify a method.
 * A parameter matches the reference parameter if the type of the parameter is one of the
 * following:
 * - exactly the same type as the reference parameter type
 * - an incarnation of the reference parameter type
 * - the reference type is underspecified
 */
public class EqNameMethodChecker extends AbstractMethodChecker {
  public EqNameMethodChecker(String mapping, TypeIncarnationHelper typeHelper) {
    super(mapping, typeHelper);
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
      for (int i = 0; i < ref.getCDParameterList().size(); i++) {
        ASTCDParameter conParam = concrete.getCDParameter(i);
        ASTCDParameter refParam = ref.getCDParameter(i);
        if (!typeHelper.isMCTypeMatched(conParam.getMCType(), refParam.getMCType())) {
          return false;
        }
      }
      return true; // same signature (name & parameter types)
    }
    return false;
  }
}
