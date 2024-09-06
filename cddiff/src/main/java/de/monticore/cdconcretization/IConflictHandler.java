package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public interface IConflictHandler {
  void handleConflicts(ASTCDCompilationUnit cccd);
}
