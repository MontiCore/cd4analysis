/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/**
 * Matches Two attributes if the have identical names and their type is equal (using deepEqual AST
 * comparison of ASTType)
 */
public class DefaultAttributeMatcher extends AttributeMatcherBase {

  public DefaultAttributeMatcher(MergeBlackBoard blackBoard) {
    super(blackBoard);
  }

  /**
   * Matches only Attributes with same name and same type
   */
  @Override
  public boolean matchAttribute(ASTCDAttribute attribute1, ASTCDAttribute attribute2) {
    boolean match =
        attribute1.getName().equalsIgnoreCase(attribute2.getName()) && attribute1.getMCType()
            .deepEquals(attribute2.getMCType());
    log(ErrorLevel.FINE, "Identified matching attribute ", attribute1, attribute2);
    return match;
  }

}
