/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.matching.matchresult.MatchNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.CDMergeUtils;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/** Merges types with the same name and adds types which weren't merged (union) */
public class DefaultTypeMerger extends TypeMerger {

  public DefaultTypeMerger(MergeBlackBoard mergeBlackBoard, TypeMergeStrategy typeMergeStrategy) {
    super(mergeBlackBoard, typeMergeStrategy);
  }

  @Override
  public void mergeTypes(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchresult) {
    try {
      if (getConfig().allowHeterogeneousMerge()
          && !this.typeMergeStrategy.canMergeHeterogenousTypes()) {
        logWarning(
            "CD Merge was configured to allow merging of heterogenous types, but the provided "
                + "TypeMergingStrategy does not support this. Merge will therefore continue only "
                + "merging homogenous types!");
      }
      if (getConfig().allowHeterogeneousMerge()
          && this.typeMergeStrategy.canMergeHeterogenousTypes()) {
        mergeAllTypes(cd1, cd2, matchresult);
        interfaceToClassInheritance();
      } else {
        mergeInterfaces(cd1, cd2, matchresult.getMatchedInterfaces());
        mergeEnums(cd1, cd2, matchresult.getMatchedEnums());
        mergeClasses(cd1, cd2, matchresult.getMatchedClasses(), matchresult.getMatchedAttributes());
      }
    } catch (MergingException e) {
      logError(e);
    }
  }

  /**
   * Merges all Types in the input CDs, supports heterogeneous type merging (e.g. merging of
   * Interface with Classes)
   *
   * @throws MergingException
   */
  public void mergeAllTypes(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchresult)
      throws MergingException {

    if (getConfig().allowHeterogeneousMerge()
        && !this.typeMergeStrategy.canMergeHeterogenousTypes()) {
      logWarning(
          "CD Merge was configured to allow merging of heterogenous type, though the provided "
              + "TypeMergingStrategy does not support this. Merge will therefore continue only "
              + "merging homogenous types!");
    }
    if (!matchresult.getCdDefinitions().contains(cd1)) {
      throw new MergingException("There was unexpectedly no match result for CD1 ", PHASE, cd1);
    }
    if (!matchresult.getCdDefinitions().contains(cd2)) {
      throw new MergingException("There was unexpectedly no match result for CD2 ", PHASE, cd2);
    }

    List<MatchNode<ASTCDType, ASTCDDefinition>> cd1Types =
        matchresult.getMatchedTypes().getAllNodesForParent(cd1);
    for (MatchNode<ASTCDType, ASTCDDefinition> cd1Type : cd1Types) {
      // A lonely type in cd1 - add it to result cd and done
      if (!cd1Type.hasMatch(cd2)) {
        addTypeToMergedCD(
            cd1Type.getElement(),
            getBlackBoard().getASTCDHelperInputCD1().getCDPackageName(cd1Type.getElement()));
      } else {

        if (cd1Type.getMatchedNodes(cd2).size() > 1) {
          logError(
              "Ambiguous matching of type "
                  + cd1Type.getElement().getName()
                  + " from CD "
                  + cd1.getName()
                  + " in CD "
                  + cd2.getName()
                  + "  . Will not process this type further!",
              cd1Type.getElement());
        }
        List<ASTCDType> matches = cd1Type.getMatchedElements();
        ASTCDType mergedType = null;
        // Check Precedences first
        if (getConfig()
            .getPrecedences()
            .hasPrecedence(cd1Type.getElement(), matches.get(0), cd1, cd2)) {
          mergedType = cd1Type.getElement();
          log(
              ErrorLevel.FINE,
              "Type "
                  + cd1.getName()
                  + "."
                  + cd1Type.getElement().getName()
                  + " has precedence and will not be merged with "
                  + cd2.getName()
                  + "."
                  + matches.get(0).getName());
        } else if (getConfig()
            .getPrecedences()
            .hasPrecedence(matches.get(0), cd1Type.getElement(), cd2, cd1)) {
          mergedType = matches.get(0);
          log(
              ErrorLevel.FINE,
              "Type "
                  + cd2.getName()
                  + "."
                  + matches.get(0).getName()
                  + " has precedence and will not be merged with "
                  + cd1.getName()
                  + "."
                  + cd1Type.getElement().getName());
        } else {

          if (cd1Type.getElement() instanceof ASTCDClass && matches.get(0) instanceof ASTCDClass) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDClass) cd1Type.getElement(),
                    (ASTCDClass) matches.get(0),
                    matchresult.getMatchedAttributes(cd1Type.getElement().getName()));
          } else if (cd1Type.getElement() instanceof ASTCDInterface
              && matches.get(0) instanceof ASTCDInterface) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDInterface) cd1Type.getElement(), (ASTCDInterface) matches.get(0));
          } else if (cd1Type.getElement() instanceof ASTCDEnum
              && matches.get(0) instanceof ASTCDEnum) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDEnum) cd1Type.getElement(), (ASTCDEnum) matches.get(0));
          } else if (cd1Type.getElement() instanceof ASTCDClass
              && matches.get(0) instanceof ASTCDInterface) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDClass) cd1Type.getElement(), (ASTCDInterface) matches.get(0));
          } else if (cd1Type.getElement() instanceof ASTCDClass
              && matches.get(0) instanceof ASTCDEnum) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDClass) cd1Type.getElement(), (ASTCDEnum) matches.get(0));
          } else if (cd1Type.getElement() instanceof ASTCDInterface
              && matches.get(0) instanceof ASTCDClass) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDClass) matches.get(0), (ASTCDInterface) cd1Type.getElement());
          } else if (cd1Type.getElement() instanceof ASTCDEnum
              && matches.get(0) instanceof ASTCDClass) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDClass) matches.get(0), (ASTCDEnum) cd1Type.getElement());
          } else if (cd1Type.getElement() instanceof ASTCDInterface
              && matches.get(0) instanceof ASTCDEnum) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDInterface) cd1Type.getElement(), (ASTCDEnum) matches.get(0));
          } else if (cd1Type.getElement() instanceof ASTCDEnum
              && matches.get(0) instanceof ASTCDInterface) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDInterface) matches.get(0), (ASTCDEnum) cd1Type.getElement());
          } else if (cd1Type.getElement() instanceof ASTCDInterface
              && matches.get(0) instanceof ASTCDEnum) {
            mergedType =
                typeMergeStrategy.merge(
                    (ASTCDInterface) cd1Type.getElement(), (ASTCDEnum) matches.get(0));
          }
        }
        if (mergedType == null) {
          logError("There is no merge Result!", cd1Type.getElement(), matches.get(0));
        } else {
          mergeComments(mergedType, cd1Type.getElement(), matches.get(0));
          addTypeToMergedCD(mergedType, getMergedPackage(cd1Type.getElement(), matches.get(0)));
        }
      }
    }
    // We added all types and matched types from cd1, now add the
    // remaining, non-matching types from cd2
    List<MatchNode<ASTCDType, ASTCDDefinition>> cd2Types =
        matchresult.getMatchedTypes().getAllNodesForParent(cd2);
    Iterator<MatchNode<ASTCDType, ASTCDDefinition>> cd2TypesIterator = cd2Types.iterator();
    while (cd2TypesIterator.hasNext()) {
      MatchNode<ASTCDType, ASTCDDefinition> cd2Type = cd2TypesIterator.next();
      if (!cd2Type.hasMatch(cd1)) {
        addTypeToMergedCD(
            cd2Type.getElement(),
            getBlackBoard().getASTCDHelperInputCD2().getCDPackageName(cd2Type.getElement()));
      }
    }
  }

  private void addTypeToMergedCD(ASTCDType type, Optional<String> packageName) {
    if (type instanceof ASTCDClass) {
      getBlackBoard().addMergedClass(Optional.of((ASTCDClass) type), packageName);
    } else if (type instanceof ASTCDInterface) {
      getBlackBoard().addMergedInterface(Optional.of((ASTCDInterface) type), packageName);
    } else if (type instanceof ASTCDEnum) {
      getBlackBoard().addMergedEnum(Optional.of((ASTCDEnum) type), packageName);
    }
  }

  /**
   * Merges all Classes from the input CDs
   *
   * @throws MergingException
   */
  public void mergeClasses(
      ASTCDDefinition cd1,
      ASTCDDefinition cd2,
      ASTMatchGraph<ASTCDClass, ASTCDDefinition> classMatchresult,
      Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> attributeMatchresult)
      throws MergingException {
    doMerge(
        cd1,
        cd2,
        classMatchresult,
        getBlackBoard()::addMergedClass,
        (c1, c2) -> this.typeMergeStrategy.merge(c1, c2, attributeMatchresult.get(c1.getName())));
  }

  /**
   * Merges all Enums from the input CDs
   *
   * @throws MergingException
   */
  public void mergeEnums(
      ASTCDDefinition cd1,
      ASTCDDefinition cd2,
      ASTMatchGraph<ASTCDEnum, ASTCDDefinition> matchedEnums)
      throws MergingException {
    doMerge(cd1, cd2, matchedEnums, getBlackBoard()::addMergedEnum, this.typeMergeStrategy::merge);
  }

  /**
   * Merges all Interfaces from the input CDs
   *
   * @throws MergingException
   */
  public void mergeInterfaces(
      ASTCDDefinition cd1,
      ASTCDDefinition cd2,
      ASTMatchGraph<ASTCDInterface, ASTCDDefinition> matchedInterfaces)
      throws MergingException {
    doMerge(
        cd1,
        cd2,
        matchedInterfaces,
        getBlackBoard()::addMergedInterface,
        this.typeMergeStrategy::merge);
  }

  /**
   * Merges all Types from the input CDs
   *
   * @throws MergingException
   */
  private <T extends ASTCDType> void doMerge(
      ASTCDDefinition cd1,
      ASTCDDefinition cd2,
      ASTMatchGraph<T, ASTCDDefinition> matchresult,
      BiConsumer<Optional<T>, Optional<String>> addToBlackBoard,
      BiFunction<T, T, T> merge)
      throws MergingException {

    if (!matchresult.hasParent(cd1)) {
      throw new MergingException("There was unexpectedly no match result for CD1 ", PHASE, cd1);
    }
    if (!matchresult.hasParent(cd2)) {
      throw new MergingException("There was unexpectedly no match result for CD2 ", PHASE, cd2);
    }

    List<MatchNode<T, ASTCDDefinition>> cd1Types = matchresult.getAllNodesForParent(cd1);
    for (MatchNode<T, ASTCDDefinition> cd1Type : cd1Types) {
      if (!cd1Type.hasMatch(cd2)) {
        addToBlackBoard.accept(Optional.of(cd1Type.getElement()), cd1Type.getPackage());
      } else {
        List<T> matches = cd1Type.getMatchedElements();
        if (matches.size() > 1) {
          logError(
              "Ambiguous matching of type "
                  + cd1Type.getElement().getName()
                  + "in CD "
                  + cd1.getName()
                  + " in CD "
                  + cd2.getName()
                  + "  . Will not process this type further!",
              cd1Type.getElement());
        } else {
          T merged = merge.apply(cd1Type.getElement(), matches.get(0));
          mergeComments(merged, cd1Type.getElement(), matches.get(0));
          addToBlackBoard.accept(
              Optional.of(merged), getMergedPackage(cd1Type.getElement(), matches.get(0)));
        }
      }
    }
    // We added all types and matched types from cd1, now add the
    // remaining, non-matching types from cd2
    List<MatchNode<T, ASTCDDefinition>> cd2Classes = matchresult.getAllNodesForParent(cd2);
    for (MatchNode<T, ASTCDDefinition> cd2Type : cd2Classes) {
      if (!cd2Type.hasMatch(cd1)) {
        addToBlackBoard.accept(Optional.of(cd2Type.getElement()), cd2Type.getPackage());
      }
    }
  }

  private Optional<String> getMergedPackage(ASTCDType t1, ASTCDType t2) {
    // We stick with default package if none specified
    if (getBlackBoard().getASTCDHelperInputCD1().isInDefaultPackage(t1)
        && getBlackBoard().getASTCDHelperInputCD1().isInDefaultPackage(t2)) {
      return Optional.empty();
    }
    Optional<String> package1 = getBlackBoard().getASTCDHelperInputCD1().getCDPackageName(t1);
    Optional<String> package2 = getBlackBoard().getASTCDHelperInputCD2().getCDPackageName(t2);
    if (package1.isPresent() && package2.isPresent()) {
      if (package1.get().equals(package2.get())) {
        return package1;
      } else {
        log(
            ErrorLevel.ERROR,
            "Ambigous package declaration "
                + package1.get()
                + t1.getName()
                + " vs. "
                + package2.get()
                + t2.getName()
                + "  for the types to merge !",
            t1,
            t2);
      }
    } else {
      if (package1.isEmpty()) {
        log(ErrorLevel.ERROR, "Unable to retrieve pacakge information!", t1);
      }
      if (package2.isEmpty()) {
        log(ErrorLevel.ERROR, "Unable to retrieve pacakge information!", t2);
      }
    }
    return Optional.empty();
  }

  /**
   * If heterogeneous merge was allowed, than an interface could have been merged with a class. So
   * if in one of the input CDs one class implemented the former interfaces, this has now to be
   * changed to a superclass with "extends".
   */
  private void interfaceToClassInheritance() {

    ASTMCObjectType iface;
    for (ASTCDClass clazz :
        getBlackBoard().getIntermediateMergedCD().getCDDefinition().getCDClassesList()) {
      for (int i = clazz.getInterfaceList().size() - 1; i >= 0; i--) {
        iface = clazz.getInterfaceList().get(i);
        if (getBlackBoard().getASTCDHelperMergedCD().cdContainsClass(CDMergeUtils.getName(iface))) {
          // Interface became a class
          if (clazz.getSuperclassList().size() > 0) {
            getBlackBoard()
                .addLog(
                    ErrorLevel.ERROR,
                    "Interface "
                        + CDMergeUtils.getName(iface)
                        + " was merged to a class. Class "
                        + clazz.getName()
                        + "  implemented that interface, but cannot be converted to subclass because it already has at least another superclass!",
                    MergePhase.MODEL_REFACTORING,
                    iface,
                    clazz);
          } else {
            // Switch Interface to superclass
            CDMergeUtils.setSuperClass(clazz, iface);
            clazz.getInterfaceList().remove(i);
          }
        }
      }
      if (clazz.getInterfaceList().size() == 0) {
        clazz.setCDInterfaceUsageAbsent();
      }
    }
  }
}
