/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.matching.strategies.AssociationMatcher;
import de.monticore.cdmerge.matching.strategies.AttributeMatcher;
import de.monticore.cdmerge.matching.strategies.MatcherBase;
import de.monticore.cdmerge.matching.strategies.TypeMatcher;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

import java.util.List;
import java.util.Map;

/**
 * Returns a list of matching type definitions from the provided class diagrams.
 */
public abstract class CDMatcher extends MatcherBase {

  private AssociationMatcher associationMatcher;

  private AttributeMatcher attributeMatcher;

  private TypeMatcher typeMatcher;

  public CDMatcher(MergeBlackBoard blackBoard, TypeMatcher typeMatcher,
      AttributeMatcher attributeMatcher, AssociationMatcher associationMatcher) {
    super(blackBoard);
    this.typeMatcher = typeMatcher;
    this.attributeMatcher = attributeMatcher;
    this.associationMatcher = associationMatcher;
  }

  protected AssociationMatcher getAssociationMatcher() {
    return this.associationMatcher;
  }

  protected AttributeMatcher getAttributeMatcher() {
    return this.attributeMatcher;
  }

  protected TypeMatcher getTypeMatcher() {
    return this.typeMatcher;
  }

  public Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> findMatchingAttributes() {
    return getAttributeMatcher().findMatchingAttributes(findMatchingClasses());
  }

  public ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> findMatchingAssociations() {
    return getAssociationMatcher().findMatchingAssociations();

  }

  public ASTMatchGraph<ASTCDAttribute, ASTCDClass> findMatchingAttributes(String className) {
    Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> matchingAttributes =
        getAttributeMatcher().findMatchingAttributes(
        findMatchingClasses());
    if (matchingAttributes.containsKey(className)) {
      return matchingAttributes.get(className);
    }
    else {
      throw new IllegalArgumentException(
          "No class with name " + className + " found in any of the classdiagrams");
    }
  }

  public ASTMatchGraph<ASTCDType, ASTCDDefinition> findMatchingTypes() {
    return getTypeMatcher().findMatchingTypes();
  }

  public ASTMatchGraph<ASTCDClass, ASTCDDefinition> findMatchingClasses() {
    return getTypeMatcher().findMatchingClasses();
  }

  public ASTMatchGraph<ASTCDInterface, ASTCDDefinition> findMatchingInterfaces() {
    return getTypeMatcher().findMatchingInterfaces();
  }

  public ASTMatchGraph<ASTCDEnum, ASTCDDefinition> findMatchingEnums() {
    return getTypeMatcher().findMatchingEnums();

  }

  public abstract CDMatch createCDMatch(List<ASTCDDefinition> cds);

}
