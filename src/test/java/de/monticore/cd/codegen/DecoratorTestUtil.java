/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public final class DecoratorTestUtil {

  private DecoratorTestUtil() {}

  public static ASTCDClass getClassBy(String name, ASTCDCompilationUnit ast) {
    List<ASTCDClass> filtered = ast.getCDDefinition().getCDClassesList().stream()
        .filter(c -> name.equals(c.getName()))
        .collect(Collectors.toList());
    assertEquals(String.format("Expected to find 1 class, but found '%s'", filtered.size()), 1, filtered.size());
    return filtered.get(0);
  }

  public static ASTCDInterface getInterfaceBy(String name, ASTCDCompilationUnit ast) {
    List<ASTCDInterface> filtered = ast.getCDDefinition().getCDInterfacesList().stream()
        .filter(c -> name.equals(c.getName()))
        .collect(Collectors.toList());
    assertEquals(String.format("Expected to find 1 interface, but found '%s'", filtered.size()), 1, filtered.size());
    return filtered.get(0);
  }

  public static ASTCDEnum getEnumBy(String name, ASTCDCompilationUnit ast) {
    List<ASTCDEnum> filtered = ast.getCDDefinition().getCDEnumsList().stream()
        .filter(c -> name.equals(c.getName()))
        .collect(Collectors.toList());
    assertEquals(String.format("Expected to find 1 enum, but found '%s'", filtered.size()), 1, filtered.size());
    return filtered.get(0);
  }

  public static List<ASTCDMethod> getMethodsBy(String name, ASTCDClass clazz) {
    return getMethodsBy(name, clazz.getCDMethodList());
  }

  public static List<ASTCDMethod> getMethodsBy(String name, ASTCDInterface interfazz) {
    return getMethodsBy(name, interfazz.getCDMethodList());
  }

  public static List<ASTCDMethod> getMethodsBy(String name, int parameterSize, ASTCDClass clazz) {
    return getMethodsBy(name, parameterSize, clazz.getCDMethodList());
  }

  public static ASTCDMethod getMethodBy(String name, ASTCDClass clazz) {
    return getMethodBy(name, clazz.getCDMethodList());
  }

  public static ASTCDMethod getMethodBy(String name, int parameterSize, ASTCDClass clazz) {
    return getMethodBy(name, parameterSize, clazz.getCDMethodList());
  }

  public static List<ASTCDMethod> getMethodsBy(String name, int parameterSize, ASTCDInterface clazz) {
    return getMethodsBy(name, parameterSize, clazz.getCDMethodList());
  }

  public static ASTCDMethod getMethodBy(String name, ASTCDInterface clazz) {
    return getMethodBy(name, clazz.getCDMethodList());
  }

  public static ASTCDMethod getMethodBy(String name, int parameterSize, ASTCDInterface clazz) {
    return getMethodBy(name, parameterSize, clazz.getCDMethodList());
  }

  public static List<ASTCDMethod> getMethodsBy(String name, List<ASTCDMethod> methods) {
    return filterMethods(methods, Collections.singletonList(
        m -> name.equals(m.getName())));
  }

  public static List<ASTCDMethod> getMethodsBy(String name, int parameterSize, List<ASTCDMethod> methods) {
    return filterMethods(methods, Arrays.asList(
        m -> name.equals(m.getName()),
        m -> parameterSize == m.getCDParameterList().size()));
  }

  public static ASTCDMethod getMethodBy(String name, List<ASTCDMethod> methods) {
    return filterMethodsOrFail(methods, Collections.singletonList(
        m -> name.equals(m.getName())));
  }

  public static ASTCDMethod getMethodBy(String name, int parameterSize, List<ASTCDMethod> methods) {
    return filterMethodsOrFail(methods, Arrays.asList(
        m -> name.equals(m.getName()),
        m -> parameterSize == m.getCDParameterList().size()));
  }

  private static ASTCDMethod filterMethodsOrFail(List<ASTCDMethod> methods, List<Predicate<ASTCDMethod>> predicates) {
    List<ASTCDMethod> filtered = filterMethods(methods, predicates);
    assertEquals(String.format("Expected find 1 method, but found '%s'", filtered.size()), 1, filtered.size());
    return filtered.get(0);
  }

  private static List<ASTCDMethod> filterMethods(List<ASTCDMethod> methods, List<Predicate<ASTCDMethod>> predicates) {
    Predicate<ASTCDMethod> composedPredicate = predicates.stream()
        .reduce(m -> true, Predicate::and);
    return methods.stream()
        .filter(composedPredicate)
        .collect(Collectors.toList());
  }

  public static ASTCDAttribute getAttributeBy(String name, ASTCDClass clazz) {
    List<ASTCDAttribute> filtered = clazz.getCDAttributeList().stream()
        .filter(attribute -> name.equals(attribute.getName()))
        .collect(Collectors.toList());
    assertEquals(String.format("Expected find 1 attribute, but found '%s'", filtered.size()), 1, filtered.size());
    return filtered.get(0);
  }


  public static ASTCDAttribute getAttributeBy(String name, ASTCDInterface clazz) {
    List<ASTCDAttribute> filtered = clazz.getCDAttributeList().stream()
        .filter(attribute -> name.equals(attribute.getName()))
        .collect(Collectors.toList());
    assertEquals(String.format("Expected find 1 attribute, but found '%s'", filtered.size()), 1, filtered.size());
    return filtered.get(0);
  }
}
