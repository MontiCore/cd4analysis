/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that there are no inheritance cycles.
 *
 * @author Robert Heim
 */
public class ExtendsNotCyclic implements CD4AnalysisASTCDDefinitionCoCo {
  
  public static final String ERROR_CODE = "0xC4A07";
  
  public static final String ERROR_MSG_FORMAT = "The %s %s introduces an inheritance cycle. Inheritance may not be cyclic.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo#check(de.monticore.umlcd4a._ast.ASTCDDefinition)
   */
  @Override
  public void check(ASTCDDefinition node) {
    for (ASTCDClass c : node.getCDClasses()) {
      checkClass(c);
    }
    for (ASTCDInterface i : node.getCDInterfaces()) {
      checkInterfacePath((CDTypeSymbol) i.getSymbol().get(), new HashSet<>());
    }
  }
  
  /**
   * Recursive method checking that a path in the inheritance (up-side-down)
   * tree does not include any name twice.
   * 
   * @param i the current interface symbol on the inheritance path
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
      for (CDTypeSymbol superInterf : interf.getInterfaces()) {
        checkInterfacePath(superInterf, currentPath);
      }
      currentPath.remove(interf);
    }
  }
  
  /**
   * Checks that there are no cycles in the the class hierarchy.
   * 
   * @param The class to check.
   */
  private void checkClass(ASTCDClass node) {
    CDTypeSymbol symbol = (CDTypeSymbol) node.getSymbol().get();
    Set<CDTypeSymbol> path = new HashSet<>();
    Optional<CDTypeSymbol> optSuperSymb = symbol.getSuperClass();
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
    CoCoLog.error(ERROR_CODE,
        String.format(ERROR_MSG_FORMAT, type, symbol.getName()));
  }
}
