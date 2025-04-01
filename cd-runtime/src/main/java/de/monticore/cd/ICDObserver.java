/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

import de.monticore.ast.ASTNode;

public interface ICDObserver {

  default void update(ASTNode subject) {
  }

}
