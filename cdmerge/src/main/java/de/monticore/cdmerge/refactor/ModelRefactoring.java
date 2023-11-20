/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.refactor;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

/** Interface for operations that refactor a class diagram in place */
public interface ModelRefactoring {

  void apply(ASTCDCompilationUnit cd);
}
