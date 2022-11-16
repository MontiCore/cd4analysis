/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.matching.matchresult.MatchNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import java.util.*;

/** Base Class for Attribute Matching */
public abstract class AttributeMatcherBase extends MatcherBase implements AttributeMatcher {

  public AttributeMatcherBase(MergeBlackBoard mergeBlackBoard) {
    super(mergeBlackBoard);
  }

  @Override
  public abstract boolean matchAttribute(ASTCDAttribute attribute1, ASTCDAttribute attribute2);

  @Override
  public Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> findMatchingAttributes(
      ASTMatchGraph<ASTCDClass, ASTCDDefinition> matchedClasses) {

    Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> matchingAttributes =
        new HashMap<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>>();

    String parentClassName;
    MatchNode<ASTCDAttribute, ASTCDClass> node1, node2;
    Iterator<List<MatchNode<ASTCDClass, ASTCDDefinition>>> iterator =
        matchedClasses.getMatchNodeIterator();
    // Iterate over all known classes in all classdiagrams
    List<ASTCDClass> parents = new ArrayList<>();
    while (iterator.hasNext()) {

      // Loops over each class in a parent classdiagram
      for (MatchNode<ASTCDClass, ASTCDDefinition> clazzNode : iterator.next()) {
        parentClassName = clazzNode.getElement().getName();
        parents = new ArrayList<>();
        parents.add(clazzNode.getElement());
        parents.addAll(clazzNode.getMatchedElements());
        if (!matchingAttributes.containsKey(parentClassName)) {
          matchingAttributes.put(
              parentClassName, new ASTMatchGraph<ASTCDAttribute, ASTCDClass>(parents));
        }
        // add each attribute of the current class to the
        // MatchResult
        for (ASTCDAttribute attribute1 : clazzNode.getElement().getCDAttributeList()) {
          // Did we already consider this attribute?
          if (matchingAttributes
              .get(parentClassName)
              .getNode(attribute1, clazzNode.getElement())
              .isPresent()) {
            continue;
          }
          node1 =
              matchingAttributes
                  .get(parentClassName)
                  .addElement(attribute1, clazzNode.getElement(), Optional.empty());
          // Check for attribute matches in matching classes (i.e.
          // classes with same name but other ClassDiagrams)
          for (MatchNode<ASTCDClass, ASTCDDefinition> matchingClazz : clazzNode.getMatchedNodes()) {
            for (ASTCDAttribute attribute2 : matchingClazz.getElement().getCDAttributeList()) {
              if (matchAttribute(attribute1, attribute2)) {
                // We found a matching attribute, add it to
                // result and establish bidirectional
                // matching
                // (edge)
                node2 =
                    matchingAttributes
                        .get(parentClassName)
                        .addElement(attribute2, matchingClazz.getElement(), Optional.empty());
                node2.addMatch(node1);
              }
            }
          }
        }
      }
    }
    return matchingAttributes;
  }
}
