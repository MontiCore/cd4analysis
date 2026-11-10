/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.mctype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.generics.bounds.Bound;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * A matching strategy for {@link ASTMCType}s that used the MontiCore type check mechanism to
 * check if a concrete type is an incarnation of a reference type.<br>
 * This enabled us to use the full generics support of MontiCore and still respect the incarnation
 * mapping for (nested) type parameters.<br>
 * <br>
 * See {@link IncMappingAwareSymTypeCompatibilityCalculator} for how we extend the original type
 * relations to support incarnations.
 */
public class TypeCheckMCTypeMatchingStrategy implements MCTypeMatchingStrategy {
  
  protected String underspecifiedTypeName;
  protected IncMappingAwareSymTypeCompatibilityCalculator compatibilityCalculator;
  
  public TypeCheckMCTypeMatchingStrategy(String underspecifiedTypeName) {
    this.underspecifiedTypeName = underspecifiedTypeName;
    this.compatibilityCalculator = new IncMappingAwareSymTypeCompatibilityCalculator();
  }
  
  /**
   * Returns if the given concrete type is an incarnation of the given reference type.
   *
   * @param conType the concrete type
   * @param refType the reference type
   * @return true if the types are matched, false otherwise
   */
  @Override
  public boolean isMatched(ASTMCType conType, ASTMCType refType,
      BooleanMatchingStrategy<ASTCDType> cdTypeMatcher) {
    if (MCTypeUtil.isUnderspecified(underspecifiedTypeName, refType)) {
      if (MCTypeUtil.isUnderspecified(underspecifiedTypeName, conType)) {
        Log.error("The underspecified placeholder type is not allowed as a concrete type.");
        return false;
      }
      // every type is allowed if the reference type is underspecified
      return true;
    }
    SymTypeExpression concreteType = TypeCheck3.symTypeFromAST(conType);
    SymTypeExpression referenceType = TypeCheck3.symTypeFromAST(refType);
    /*
     * Compatibility calculator knows the incarnation mapping and checks if the concrete
     * type is an incarnation of the reference type.if both are CDTypeSymbols.
     * As a result, we get full generics support and respect the incarnation mapping even
     * in nested generic typ parameters.
     */
    // TODO check if all incarnations used as type parameters in the concrete type are conflict
    //  free. i.e.
    //  1. not two different incarnations of the same reference type in the same type
    //  2. no conflicting bindings attached to one of the type parameters
    //  (can be solved with a simple visitor once we have the "Binding" infrastructure from OCL
    //   available here)
    /*
     * IMPORTANT: Set the CD type matcher before calling compatibilityCalculator so it uses the
     * desired incarnation mapping for the CD types.
     * This makes the method NOT THREAD SAVE! (like a lot of other methods in the conformance check
     * and concretization as well, so not a huge issue).
     */
    compatibilityCalculator.setCDTypeMatcher(cdTypeMatcher);
    List<Bound> result = compatibilityCalculator.constrainSameType(concreteType, referenceType);
    /*
     * IMPORTANT: Do NOT log an error here if it is no match! Conformance checking code might call
     * this for multiple candidate where only one is expected to match. If we log an error
     * here this results in a failure in the end. Even info/debug logs will result in confusion
     * for users/developers, as they will see invalid matches for the other candidates.
     * It is the responsibility of the conformance checking code to report errors if no match is
     * found for any candidate.
     */
    return result.isEmpty();
  }
  
}
