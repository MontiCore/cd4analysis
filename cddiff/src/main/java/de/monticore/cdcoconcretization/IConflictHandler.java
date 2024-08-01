package de.monticore.cdcoconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public interface IConflictHandler {
  void handleConflicts(ASTCDCompilationUnit cccd);
}
