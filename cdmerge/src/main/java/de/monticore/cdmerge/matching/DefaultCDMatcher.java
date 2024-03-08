/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.matching.strategies.AssociationMatcher;
import de.monticore.cdmerge.matching.strategies.AttributeMatcher;
import de.monticore.cdmerge.matching.strategies.TypeMatcher;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import java.util.List;

/**
 * Implements default matching strategies: Types with the same Name, Attributes with the same name
 * and compatible types, Associations with the same type references and compatible specification
 */
public class DefaultCDMatcher extends CDMatcher {

  public DefaultCDMatcher(
      MergeBlackBoard blackboard,
      TypeMatcher typeMatcher,
      AttributeMatcher attributeMatcher,
      AssociationMatcher associationMatcher) {
    super(blackboard, typeMatcher, attributeMatcher, associationMatcher);
  }

  @Override
  public CDMatch createCDMatch(List<ASTCDDefinition> cds) {
    CDMatch matchResult = new CDMatch(cds);
    matchResult.setMatchedTypes(findMatchingTypes());
    matchResult.setMatchedClasses(findMatchingClasses());
    matchResult.setMatchedAttributes(findMatchingAttributes());
    matchResult.setMatchedEnums(findMatchingEnums());
    matchResult.setMatcheInterfaces(findMatchingInterfaces());
    matchResult.setMatchedAssociations(findMatchingAssociations());
    log(ErrorLevel.INFO, "Match Graph completed");
    log(ErrorLevel.FINE, matchResult.toString());
    return matchResult;
  }
}
