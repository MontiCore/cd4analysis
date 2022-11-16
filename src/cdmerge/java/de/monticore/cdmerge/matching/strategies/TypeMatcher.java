/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;

public interface TypeMatcher {

  /** Rather simple strategy: two types match if the have the same name */
  default boolean matchType(ASTCDType type1, ASTCDType type2) {
    return (type1.getName().equalsIgnoreCase(type2.getName()));
  }

  /** Rather simple strategy: two classes match if the have the same name */
  default boolean matchType(ASTCDClass class1, ASTCDClass class2) {
    return (class1.getName().equalsIgnoreCase(class2.getName()));
  }

  /** Rather simple strategy: two enums match if the have the same name */
  default boolean matchType(ASTCDEnum enum1, ASTCDEnum enum2) {
    return (enum1.getName().equalsIgnoreCase(enum2.getName()));
  }

  /** Rather simple strategy: two interfaces match if the have the same name */
  default boolean matchType(ASTCDInterface interface1, ASTCDInterface interface2) {
    return (interface1.getName().equalsIgnoreCase(interface2.getName()));
  }

  /** Heterogeneous types dont't match by default */
  default boolean matchType(ASTCDClass clazz, ASTCDInterface iface) {
    return false;
  }

  /** Heterogeneous types dont't match by default */
  default boolean matchType(ASTCDEnum en, ASTCDInterface iface) {
    return false;
  }

  /** Heterogeneous types dont't match by default */
  default boolean matchType(ASTCDClass clazz, ASTCDEnum en) {
    return false;
  }

  ASTMatchGraph<ASTCDType, ASTCDDefinition> findMatchingTypes();

  ASTMatchGraph<ASTCDClass, ASTCDDefinition> findMatchingClasses();

  ASTMatchGraph<ASTCDInterface, ASTCDDefinition> findMatchingInterfaces();

  ASTMatchGraph<ASTCDEnum, ASTCDDefinition> findMatchingEnums();
}
