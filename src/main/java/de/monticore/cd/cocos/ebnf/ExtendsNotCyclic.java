/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolReference;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Checks that there are no inheritance cycles.
 *
 * @author Robert Heim
 */
public class ExtendsNotCyclic implements CD4AnalysisASTCDDefinitionCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDDefinitionCoCo#check(de.monticore.cd._ast.ASTCDDefinition)
   */
  @Override
  public void check(ASTCDDefinition node) {
    for (ASTCDClass c : node.getCDClassList()) {
      checkClass(c);
    }
    for (ASTCDInterface i : node.getCDInterfaceList()) {
      checkInterfacePath((CDTypeSymbol) i.getSymbol2(), new HashSet<>());
    }
  }
  
  /**
   * Recursive method checking that a path in the inheritance (up-side-down)
   * tree does not include any name twice.
   * 
   * @param interf the current interface symbol on the inheritance path
   * @param currentPath the current inheritance path to i (not including i).
   * This set will be adjusted for each step, but it is ensured that
   * currentPath@Pre == currentPath@Post.
   */
  private void checkInterfacePath(CDTypeSymbol interf, Set<CDTypeSymbol> currentPath) {
    Optional<CDTypeSymbol> extendingInterfaceWithSameName = currentPath.stream()
        .filter(i -> i.getName().equals(interf.getName()))
        .findFirst();
    if (extendingInterfaceWithSameName.isPresent()) {
      error("interface", extendingInterfaceWithSameName.get());
    }
    else {
      currentPath.add(interf);
      for (CDTypeSymbol superInterf : interf.getCdInterfaces()) {
        checkInterfacePath(superInterf, currentPath);
      }
      currentPath.remove(interf);
    }
  }
  
  /**
   * Checks that there are no cycles in the the class hierarchy.
   * 
   * @param node class to check.
   */
  private void checkClass(ASTCDClass node) {
    CDTypeSymbol symbol = (CDTypeSymbol) node.getSymbol2();
    Set<CDTypeSymbol> path = new HashSet<>();
    Optional<CDTypeSymbolReference> optSuperSymb = symbol.getSuperClass();
    while (optSuperSymb.isPresent()) {
      CDTypeSymbol superSymb = optSuperSymb.get();
      Optional<CDTypeSymbol> existingClassWithSameName = path.stream()
          .filter(c -> c.getName().equals(superSymb.getName())).findAny();
      if (existingClassWithSameName.isPresent()) {
        error("class", existingClassWithSameName.get());
        optSuperSymb = Optional.empty();
      }
      else {
        path.add(superSymb);
        optSuperSymb = superSymb.getSuperClass();
      }
    }
  }
  
  /**
   * Issues the coco error.
   * 
   * @param type "interface" or "class"
   * @param symbol the symbol that produced the error
   */
  private void error(String type, CDTypeSymbol symbol) {
    Log.error(String.format(
        "0xC4A07 The %s %s introduces an inheritance cycle. Inheritance may not be cyclic.", type,
        symbol.getName()));
  }
}
