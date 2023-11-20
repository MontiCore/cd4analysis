/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;

/** Interface for algorithms which merge two types */
public interface TypeMergeStrategy {

  /**
   * Merges the two classes into one
   *
   * @param class1 - input class 1
   * @param class2 - input class 2
   * @return - the merged class
   */
  ASTCDClass merge(
      ASTCDClass class1, ASTCDClass class2, ASTMatchGraph<ASTCDAttribute, ASTCDClass> matchResult);

  /**
   * Merges the two enums into one
   *
   * @param enum1 - input enum 1
   * @param enum2 - input enum 2
   * @return - the merged enum
   */
  ASTCDEnum merge(ASTCDEnum enum1, ASTCDEnum enum2);

  /**
   * Merges the two interfaces into one
   *
   * @param interfaces1 - input interfaces 1
   * @param interfaces2 - input interfaces 2
   * @return - the merged interfaces
   */
  ASTCDInterface merge(ASTCDInterface left, ASTCDInterface right);

  /**
   * Returns true if this strategy offers support to merge heterogenous types, i.e. classes with
   * interfaces Thus returns true if the operation {@link mergeClassWithInterface} is implemented
   */
  default boolean canMergeHeterogenousTypes() {
    return false;
  }

  /**
   * Executes a heterogeneous merge of class and interface
   *
   * @param astClass - input class
   * @param astInterface - input interface
   * @return - the merged class
   */
  default ASTCDClass merge(ASTCDClass astClass, ASTCDInterface astInterface) {
    throw new UnsupportedOperationException(
        "This Strategy does not support heterogenous type merges");
  }

  /**
   * Executes a heterogeneous merge of class and interface
   *
   * @param astClass - input class
   * @param astEnum - input enum
   * @return - the merged class
   */
  default ASTCDClass merge(ASTCDClass astClass, ASTCDEnum astEnum) {
    throw new UnsupportedOperationException(
        "This Strategy does not support heterogenous type merges");
  }

  /**
   * Executes a heterogeneous merge of class and enum
   *
   * @param astClass - input class
   * @param astEnum - input enum
   * @return - the merged enum
   */
  default ASTCDEnum merge(ASTCDInterface element, ASTCDEnum enm) {
    throw new UnsupportedOperationException(
        "This Strategy does not support heterogenous type merges");
  }
}
