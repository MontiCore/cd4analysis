/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.JPrimitiveType;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

/** Matches only Attributes with same name and compatible type */
public class TypeCompatibleAttributeMatcher extends AttributeMatcherBase {

  public TypeCompatibleAttributeMatcher(MergeBlackBoard blackBoard) {
    super(blackBoard);
  }

  /**
   * Matches only Attributes with same name and compatible type Does only check built in java types
   * - all other types are considered to be incompatible
   */
  @Override
  public boolean matchAttribute(ASTCDAttribute attribute1, ASTCDAttribute attribute2) {
    if (!attribute1.getName().equalsIgnoreCase(attribute2.getName())) {
      return false;
    }

    String type1 =
        attribute1.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
    String type2 =
        attribute2.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
    if (type1.equals(type2)) {
      return true;
    }
    if (JPrimitiveType.isPrimitiveType(type1) || JPrimitiveType.isPrimitiveType(type2)) {
      if (JPrimitiveType.isPrimitiveType(type1) && JPrimitiveType.isPrimitiveType(type2)) {
        boolean match =
            JPrimitiveType.getCommonSuperType(
                    JPrimitiveType.getType(type1), JPrimitiveType.getType(type2))
                .isPresent();
        if (match) {
          log(
              ErrorLevel.FINE,
              "Identified attributes with matching (super)types",
              attribute1,
              attribute2);
        }
        return match;
      } else return false;
    }

    // We don't know as external type references in the class diagram
    // are typically modeled as associations. So we have now information about
    // these,
    // most possibly imported types. In future Versions we could use the
    // SymbolTable with JavaDSL Adapter to find a possible common super
    // Type. For now we say the attributes are not compatible
    return false;
  }
}
