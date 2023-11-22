/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CDMergeInheritanceHelper {

  public static ASTCDCompilationUnit mergeRedundantAttributes(
      ASTCDCompilationUnit cd, boolean allowTypeConversion) throws MergingException {
    CDMergeUtils.refreshSymbolTable(cd);
    for (ASTCDClass cdClass : cd.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : cdClass.getCDAttributeList()) {
        Optional<ASTCDAttribute> optAttribute =
            getAttributeFromSuperClass(cdClass, attribute.getName());
        if (optAttribute.isPresent()) {
          if (allowTypeConversion) {
            Optional<ASTMCType> type =
                JPrimitiveType.getCommonSuperType(
                    attribute.getMCType(), optAttribute.get().getMCType());
            if (type.isEmpty()) {
              throw new MergingException(
                  "Could not merge "
                      + attribute.getSymbol().getFullName()
                      + " with "
                      + optAttribute.get().getSymbol().getFullName());
            }
            optAttribute.get().setMCType(type.get());
          } else if (!attribute.getMCType().deepEquals(optAttribute.get().getMCType())) {
            throw new MergingException(
                "Could not merge "
                    + attribute.getSymbol().getFullName()
                    + " with "
                    + optAttribute.get().getSymbol().getFullName());
          }
          cdClass.removeCDMember(attribute);
        }
      }
    }
    return cd;
  }

  public static Optional<ASTCDAttribute> getAttributeFromSuperClass(
      ASTCDClass srcClass, String attributeName) {
    Set<ASTCDAttribute> attributeList = new HashSet<>();
    getStrictSuperClasses(srcClass)
        .forEach(superClass -> attributeList.addAll(superClass.getCDAttributeList()));
    return attributeList.stream()
        .filter(attribute -> attribute.getName().equals(attributeName))
        .findAny();
  }

  public static boolean isStrictSuperClassOf(ASTCDClass srcClass, ASTCDClass targetClass) {
    return getStrictSuperClasses(srcClass).stream()
        .anyMatch(
            superClass ->
                superClass.getSymbol().getFullName().equals(targetClass.getSymbol().getFullName()));
  }

  public static Set<ASTCDClass> getStrictSuperClasses(ASTCDClass cdClass) {
    HashSet<ASTCDClass> superClasses = new HashSet<>();
    if (cdClass.getSymbol().getSuperClassesOnly().isEmpty()) {
      return superClasses;
    } else {
      superClasses.addAll(
          cdClass.getSymbol().getSuperClassesOnly().stream()
              .map(exp -> (ASTCDClass) exp.getTypeInfo().getAstNode())
              .collect(Collectors.toSet()));
      HashSet<ASTCDClass> superSuperClasses = new HashSet<>();
      superClasses.forEach(
          superClass -> superSuperClasses.addAll(getStrictSuperClasses(superClass)));
      superClasses.addAll(superSuperClasses);
    }
    return superClasses;
  }
}
