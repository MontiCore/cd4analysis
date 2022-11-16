/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.matching.matchresult.MatchNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import java.util.*;
import java.util.function.BiFunction;

/** Default Type matcher implementation */
public class DefaultTypeMatcher extends MatcherBase implements TypeMatcher {

  public DefaultTypeMatcher(MergeBlackBoard blackBoard) {
    super(blackBoard);
  }

  @Override
  public ASTMatchGraph<ASTCDType, ASTCDDefinition> findMatchingTypes() {
    Set<String> names = new HashSet<>();
    getCurrentCDHelper().forEach(h -> names.addAll(h.getTypeNames()));
    BiFunction<Integer, String, Optional<ASTCDType>> getTypeFunc =
        ((i, name) -> getCurrentCDHelper().get(i).getType(name));
    return findMatching(names, getTypeFunc);
  }

  @Override
  public ASTMatchGraph<ASTCDClass, ASTCDDefinition> findMatchingClasses() {
    Set<String> names = new HashSet<>();
    getCurrentCDHelper().forEach(h -> names.addAll(h.getClassNames()));
    BiFunction<Integer, String, Optional<ASTCDClass>> getClassFunc =
        ((i, name) -> getCurrentCDHelper().get(i).getClass(name));
    return findMatching(names, getClassFunc);
  }

  @Override
  public ASTMatchGraph<ASTCDInterface, ASTCDDefinition> findMatchingInterfaces() {
    Set<String> names = new HashSet<>();
    getCurrentCDHelper().forEach(h -> names.addAll(h.getInterfaceNames()));
    BiFunction<Integer, String, Optional<ASTCDInterface>> getInterfaceFunc =
        ((i, name) -> getCurrentCDHelper().get(i).getInterface(name));
    return findMatching(names, getInterfaceFunc);
  }

  @Override
  public ASTMatchGraph<ASTCDEnum, ASTCDDefinition> findMatchingEnums() {
    Set<String> names = new HashSet<>();
    getCurrentCDHelper().forEach(h -> names.addAll(h.getEnumNames()));
    BiFunction<Integer, String, Optional<ASTCDEnum>> getEnumFunc =
        ((i, name) -> getCurrentCDHelper().get(i).getEnum(name));
    return findMatching(names, getEnumFunc);
  }

  /**
   * This generic lambda parameterized Method retrieves Matchings for ASTCDTypes
   *
   * @param cds
   * @param typeNames - A Set of all Typenames
   * @param getSpecificType - A Function with two Params: The first integer addresses an
   *     ASTCDHelper, the second the concrete Type
   * @return
   */
  private <T extends ASTCDElement> ASTMatchGraph<T, ASTCDDefinition> findMatching(
      Set<String> typeNames, BiFunction<Integer, String, Optional<T>> getSpecificType) {

    ASTMatchGraph<T, ASTCDDefinition> matches =
        new ASTMatchGraph<T, ASTCDDefinition>(getCurrentCDs());
    Optional<T> astType;
    List<MatchNode<T, ASTCDDefinition>> previousMatches = new ArrayList<>();
    MatchNode<T, ASTCDDefinition> node = null;
    for (String type : typeNames) {
      previousMatches.clear();
      for (int i = 0; i < getCurrentCDs().size(); i++) {
        astType = getSpecificType.apply(i, type);
        if (astType.isPresent()) {
          node =
              matches.addElement(
                  astType.get(),
                  getCurrentCDs().get(i),
                  getCurrentCDHelper().get(i).getCDPackageName((astType.get())));
          for (MatchNode<T, ASTCDDefinition> match : previousMatches) {
            // addMatch is bidirectional so reverse match will be
            // automatically created
            log(
                ErrorLevel.FINE,
                "Identified Type Match",
                (ASTNode) node.getElement(),
                (ASTNode) match.getElement());
            node.addMatch(match);
          }
          previousMatches.add(node);
        }
      }
    }
    return matches;
  }
}
