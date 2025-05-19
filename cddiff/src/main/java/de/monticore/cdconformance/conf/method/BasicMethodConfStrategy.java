package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.inc.method.CDMethodMatchingStrategy;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static de.monticore.cdconformance.CDConfParameter.*;

public class BasicMethodConfStrategy extends CDMethodChecker {

  private final MCTypeMatcher typeMatcher;
  private final Set<CDConfParameter> params;

  public BasicMethodConfStrategy(
          CDMethodMatchingStrategy methodIncStrategy,
          MCTypeMatcher typeMatcher,
          Set<CDConfParameter> params) {
    super(methodIncStrategy);
    this.typeMatcher = typeMatcher;
    this.params = params;
  }

  @Override
  public boolean checkConformance(ASTCDMethod concrete, ASTCDMethod ref) {
    return checkReturnTypeConformance(concrete.getMCReturnType(), ref.getMCReturnType())
        && checkParameterListConformance(concrete.getCDParameterList(), ref.getCDParameterList(),
            concrete.get_SourcePositionStart());
  }

  /**
   * Checks whether the concrete parameter list conforms to the reference parameter list.
   * The conformance depends on the {@link CDConfParameter} settings.
   * <ul>
   *   <li>If {@link CDConfParameter#STRICT_PARAMETER_ORDER} is present, the each concrete parameter
   *   type has to conform to the reference parameter at the same position. The name is ignored.</li>
   *   <li>If {@link CDConfParameter#STRICT_PARAMETER_ORDER} is not present, the must be one concrete
   *   parameter for each reference parameter that has the same name and a conforming type.</li>
   *   <li>Additionally, if {@link CDConfParameter#ALLOW_ADDITIONAL_PARAMETERS} is present, the concrete
   *   parameter list may contain additional parameters that are not present in the reference model.
   * </ul>
   *
   * @param conParams The concrete parameter list
   * @param refParams The reference parameter list
   * @param sourcePos The source position of the concrete method for error reporting
   * @return true if the concrete parameter list conforms to the reference parameter list, false otherwise
   */
  protected boolean checkParameterListConformance(
          List<ASTCDParameter> conParams, List<ASTCDParameter> refParams, SourcePosition sourcePos) {
    if (params.contains(ALLOW_ADDITIONAL_PARAMETERS)) {
      if (conParams.size() < refParams.size()) {
        Log.error("The concrete method has less parameters than the reference method.",
                sourcePos);
        return false;
      }
    } else {
      if (conParams.size() != refParams.size()) {
        Log.error("The concrete method has a different number of parameters than the " +
                        "reference method. If you want to allow additional parameters, set the " +
                        "CDConf parameter " + ALLOW_ADDITIONAL_PARAMETERS,
                        sourcePos);
        return false;
      }
    }
    /* From here on we know that the number of concrete parameters is higher than or equal to
     * the number of reference parameters. The following conditions only check if each reference
     * parameter has a matching concrete parameter and ignores possible additional parameters.
     */
    if (params.contains(STRICT_PARAMETER_ORDER)) {
      return IntStream.range(0, refParams.size())
              .allMatch(i -> checkParameterConformance(conParams.get(i), refParams.get(i)));

    } else {
      return refParams.stream().allMatch(refPar -> {
            if (conParams.stream().noneMatch(conPar -> checkParameterConformance(conPar, refPar))) {
              Log.error("No concrete parameter matches the reference parameter name '"
                      + refPar.getName()
                      + "'. If you want to match parameters by their type only, set the parameter "
                      + STRICT_PARAMETER_ORDER, refPar.get_SourcePositionStart());
              return false;
            } else {
              return true;
            }
        });
    }
  }

  /**
   * Checks whether the concrete parameter conforms to the reference parameter.
   * The concrete parameter conforms to the reference parameter iff:
   * <ol>
   *   <li>the type conforms to the reference type (see {@link MCTypeMatcher#isMCTypeMatched})</li>
   *   <li>(only if NOT {@link CDConfParameter#STRICT_PARAMETER_ORDER}) the parameter name is the same</li>
   * </ol>
   *
   * @param conPar the concrete parameter
   * @param refPar the reference parameter
   * @return true if the concrete parameter conforms to the reference parameter, false otherwise
   */
  protected boolean checkParameterConformance(ASTCDParameter conPar, ASTCDParameter refPar) {
    if (!params.contains(STRICT_PARAMETER_ORDER) && !conPar.getName().equals(refPar.getName())) {
      return false;
    }
    return typeMatcher.isMCTypeMatched(conPar.getMCType(), refPar.getMCType());
  }

  /**
   * Checks whether the concrete return type conforms to the reference return type.
   * The concrete return type conforms to the reference return type if one of the following holds:
   * <ol>
   *   <li>the reference type is underspecified</li>
   *   <li>both return types are void</li>
   *   <li>the type conforms to the reference type (see {@link MCTypeMatcher#isMCTypeMatched})</li>
   * </ol>
   *
   * @param conReturn
   * @param refReturn
   * @return
   */
  protected boolean checkReturnTypeConformance(
          ASTMCReturnType conReturn, ASTMCReturnType refReturn) {
    if (typeMatcher.isVoidType(refReturn)) {
      /*
       * For methods, we treat 'void' as underspecification of the return type. Therefore, any
       * concrete return type is allowed.
       */
      return true;
    }
    if (typeMatcher.isVoidType(conReturn)) {
      // a void return type is only allowed if the reference type is either void or underspecified
      return typeMatcher.isUnderspecified(refReturn);
    }
    return typeMatcher.isMCTypeMatched(conReturn.getMCType(), refReturn.getMCType());
  }
}
