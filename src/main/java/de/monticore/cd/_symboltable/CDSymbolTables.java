/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd._symboltable;

import com.google.common.collect.Lists;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;
import java.util.stream.Collectors;

public class CDSymbolTables {

  public static List<ASTCDAttribute> getAttributesInHierarchy(ASTCDType ast) {
    List<ASTCDAttribute> attributes = Lists.newArrayList();
    for (ASTCDType sc : getTransitiveSuperTypes(ast)) {
      attributes.addAll(sc.getCDAttributeList());
    }
    return attributes;
  }

  public static List<ASTCDAttribute> getInheritedAttributesInHierarchy(ASTCDType ast) {
    List<ASTCDAttribute> attributes = getAttributesInHierarchy(ast);
    attributes.removeAll(ast.getCDAttributeList());
    return attributes;
  }

  public static List<ASTCDAssocSide> getAssociations(ASTCDType ast) {
    return ast.getSymbol().getCDRoleList().stream().filter(r -> r.isIsDefinitiveNavigable())
      .map(a -> a.getAssocSide()).collect(Collectors.toList());
  }

   public static List<ASTCDAssocSide> getAssociationsInHierarchy(ASTCDType ast) {
    List<ASTCDAssocSide> assocs = Lists.newArrayList();
    for (ASTCDType sc : getTransitiveSuperTypes(ast)) {
      assocs.addAll(getAssociations(sc));
    }
    return assocs;
  }

  public static List<ASTCDClass> getTransitiveSuperClasses(ASTCDClass ast) {
    List<ASTCDClass> classes = Lists.newArrayList(ast);
    ASTType currentClass = ast;
    while (currentClass.getSymbol().isPresentSuperClass()) {
      ASTType node = currentClass.getSymbol().getSuperClass().getTypeInfo().getAstNode();
      if (node instanceof ASTCDClass) {
        classes.add((ASTCDClass) node);
      }
      currentClass = node;
    }
    return classes;
  }
  public static List<ASTCDInterface> getTransitiveSuperInterfaces(ASTCDType ast) {
    List<ASTCDInterface> interfaces = Lists.newArrayList();
    for (SymTypeExpression s : ast.getSymbol().getInterfaceList()) {
      ASTType node = s.getTypeInfo().getAstNode();
      if (node instanceof ASTCDInterface) {
        ASTCDInterface interf = (ASTCDInterface) node;
        interfaces.add(interf);
        interfaces.addAll(getTransitiveSuperInterfaces(interf));
      }
    }
    return interfaces;
  }

  public static List<ASTCDType> getTransitiveSuperTypes(ASTCDType ast) {
    List<ASTCDType> types = Lists.newArrayList();
    if (ast instanceof ASTCDClass) {
      types.addAll(getTransitiveSuperClasses((ASTCDClass) ast));
    }
    types.addAll(getTransitiveSuperInterfaces(ast));
    return types;
  }

}
