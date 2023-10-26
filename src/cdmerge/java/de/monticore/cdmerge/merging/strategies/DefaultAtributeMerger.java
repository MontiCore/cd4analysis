/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.config.PrecedenceConfig;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.matching.matchresult.MatchNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.CDMergeUtils;
import de.monticore.cdmerge.util.JPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Merges the attribute lists of two classes by uniting them and/or considering precedences */
public class DefaultAtributeMerger extends AttributeMerger {

  public DefaultAtributeMerger(MergeBlackBoard mergeBlackBoard) {
    super(mergeBlackBoard);
  }

  private void addExlicitAttributePrecedences(
      ASTCDClass sourceClass, ASTCDDefinition sourceCD, ASTCDClass mergedClass) {
    PrecedenceConfig precedences = getConfig().getPrecedences();

    List<String> additionalAttrNames =
        precedences.getPrecedenceAttributesForClass(sourceClass, sourceCD);
    for (String attrName : additionalAttrNames) {
      Optional<ASTCDAttribute> attr = CDMergeUtils.getAttributeFromClass(attrName, sourceClass);
      if (attr.isPresent()
          && !CDMergeUtils.getAttributeFromClass(attrName, mergedClass).isPresent()) {
        mergedClass.addCDMember(attr.get());
      }
    }
  }

  @Override
  public void mergeAttributes(
      ASTCDClass input1,
      ASTCDClass input2,
      ASTMatchGraph<ASTCDAttribute, ASTCDClass> matchResult,
      ASTCDClass mergedClazz) {

    PrecedenceConfig precedences = getConfig().getPrecedences();

    if (precedences.hasPrecedence(
        input1,
        input2,
        getBlackBoard().getCurrentInputCd1().getCDDefinition(),
        getBlackBoard().getCurrentInputCd2().getCDDefinition())) {
      // No union: take only the attributes of the left class
      log(
          ErrorLevel.INFO,
          "Left Class has preference over right class, ommiting attributes from right, if not "
              + "explicitily specified via precedence");
      mergedClazz.getCDAttributeList().clear();
      mergedClazz.getCDAttributeList().addAll(input1.getCDAttributeList());
      log(ErrorLevel.INFO, "Adding explicitly defined Attributes from right class");
      addExlicitAttributePrecedences(
          input2, getBlackBoard().getCurrentInputCd2().getCDDefinition(), mergedClazz);
    } else if (precedences.hasPrecedence(
        input2,
        input1,
        getBlackBoard().getCurrentInputCd2().getCDDefinition(),
        getBlackBoard().getCurrentInputCd1().getCDDefinition())) {
      // No union: take only the attributes of the right class
      log(
          ErrorLevel.INFO,
          "Right Class has preference over left class, ommiting attributes from left, if not "
              + "exlicitily specified via precedence");
      mergedClazz.getCDAttributeList().clear();
      mergedClazz.getCDAttributeList().addAll(input2.getCDAttributeList());
      log(ErrorLevel.INFO, "Adding explicitly defined Attributes from left class");
      addExlicitAttributePrecedences(
          input1, getBlackBoard().getCurrentInputCd1().getCDDefinition(), mergedClazz);
    } else { // union of both attribute lists
      try {
        defaultMergeAttributes(input1, input2, matchResult, mergedClazz);
      } catch (MergingException e) {
        logError(e);
      }
    }
  }

  private void defaultMergeAttributes(
      ASTCDClass input1,
      ASTCDClass input2,
      ASTMatchGraph<ASTCDAttribute, ASTCDClass> matchResult,
      ASTCDClass mergedClass)
      throws MergingException {

    if (!input1.getName().equalsIgnoreCase(input2.getName())) {
      logError("Default Attribute merger expects two classes with same name !", input1, input2);
    }
    // We don't want to manipulate the original input
    List<ASTCDAttribute> remainingFromInput2 = new ArrayList<>(input2.getCDAttributeList());

    if (!matchResult.hasParent(input1) && !matchResult.hasParent(input2)) {
      throw new MergingException(
          "Unexpected error while merging Attribtues, no matches found for the given classes - Do"
              + " the classes Match?",
          PHASE,
          input1,
          input2);
    }

    List<MatchNode<ASTCDAttribute, ASTCDClass>> matches;
    Optional<ASTCDAttribute> mergedAttr;
    for (MatchNode<ASTCDAttribute, ASTCDClass> attr1 : matchResult.getAllNodesForParent(input1)) {
      matches = attr1.getMatchedNodes(input2);
      switch (matches.size()) {
        case 0:
          // add unique attribute
          mergedClass.addCDMember(attr1.getElement());
          log(
              ErrorLevel.FINE,
              "Added declared attribute "
                  + getBlackBoard().getCurrentInputCd1().getCDDefinition().getName()
                  + "."
                  + input1.getName()
                  + "."
                  + attr1.getElement().getName()
                  + "["
                  + CDMergeUtils.getTypeName(attr1.getElement().getMCType())
                  + "]"
                  + " not present in other Class");
          break;
        case 1:
          mergedAttr = Optional.empty();
          // determine common supertype if necessary
          if (attr1.getElement().getMCType().deepEquals(matches.get(0).getElement().getMCType())) {
            // add matched attribute
            mergedAttr = Optional.of(attr1.getElement().deepClone());
            log(
                ErrorLevel.INFO,
                "Merged attribute "
                    + getBlackBoard().getCurrentInputCd1().getCDDefinition().getName()
                    + "."
                    + input1.getName()
                    + "."
                    + attr1.getElement().getName()
                    + "["
                    + CDMergeUtils.getTypeName(attr1.getElement().getMCType())
                    + "]"
                    + " with "
                    + getBlackBoard().getCurrentInputCd2().getCDDefinition().getName()
                    + "."
                    + input2.getName()
                    + "."
                    + matches.get(0).getElement().getName()
                    + "["
                    + CDMergeUtils.getTypeName(matches.get(0).getElement().getMCType())
                    + "]");

          } else {
            if (getConfig().allowPrimitiveTypeConversion()) {
              Optional<ASTMCType> commonSuperType =
                  JPrimitiveType.getCommonSuperType(
                      attr1.getElement().getMCType(), matches.get(0).getElement().getMCType());

              if (commonSuperType.isPresent()) {
                attr1.getElement().setMCType(commonSuperType.get());
                mergedAttr = Optional.of(attr1.getElement());
                log(
                    ErrorLevel.INFO,
                    "Merged attribute "
                        + getBlackBoard().getCurrentInputCd1().getCDDefinition().getName()
                        + "."
                        + input1.getName()
                        + "."
                        + attr1.getElement().getName()
                        + "["
                        + CDMergeUtils.getTypeName(attr1.getElement().getMCType())
                        + "]"
                        + " with "
                        + getBlackBoard().getCurrentInputCd2().getCDDefinition().getName()
                        + "."
                        + input2.getName()
                        + "."
                        + matches.get(0).getElement().getName()
                        + "["
                        + CDMergeUtils.getTypeName(matches.get(0).getElement().getMCType())
                        + "] with result type "
                        + CDMergeUtils.getTypeName(commonSuperType.get()));
              } else {
                logError(
                    "Could not detect common super type for attributes! Merged Class will only "
                        + "contain attribute with type from first attribute",
                    attr1.getElement(),
                    matches.get(0).getElement());
              }
            } else {
              log(
                  ErrorLevel.INFO,
                  "Unable to merge attributes "
                      + input1.getName()
                      + "."
                      + attr1.getElement().getName()
                      + "["
                      + CDMergeUtils.getTypeName(attr1.getElement().getMCType())
                      + "]"
                      + " with "
                      + input2.getName()
                      + "."
                      + matches.get(0).getElement().getName()
                      + "["
                      + CDMergeUtils.getTypeName(matches.get(0).getElement().getMCType())
                      + "] as result types differ and type conversion is not enabled");
            }
          }
          if (mergedAttr.isPresent()) {
            mergeComments(mergedAttr.get(), attr1.getElement(), matches.get(0).getElement());
            mergedClass.addCDMember(mergedAttr.get());
            // Remove match partner
            remainingFromInput2.remove(matches.get(0).getElement());
          }
          break;
        default:
          logError(
              "There was more than one match for attribute "
                  + attr1.getElement().getName()
                  + " in class "
                  + input1.getName(),
              input1,
              input2);
          break;
      }
    }
    // Add remaining, non matching attributes from input2
    for (ASTCDAttribute attr2 : remainingFromInput2) {
      mergedClass.addCDMember(attr2);
      log(
          ErrorLevel.FINE,
          "Added declared attribute "
              + getBlackBoard().getCurrentInputCd2().getCDDefinition().getName()
              + "."
              + input2.getName()
              + "."
              + attr2.getName()
              + "["
              + CDMergeUtils.getTypeName(attr2.getMCType())
              + "]"
              + " not present in other Class");
    }
  }
}
