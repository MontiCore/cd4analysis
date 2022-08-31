package de.monticore.cdmerge;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import net.sourceforge.plantuml.Log;

import java.util.Optional;
import java.util.Set;

public class CDMerge {
  public static ASTCDCompilationUnit merge(Set<ASTCDCompilationUnit> inputs) {
    Optional<ASTCDCompilationUnit> optAST = inputs.stream().findAny();
    if (optAST.isPresent()) {
      return optAST.get();

    }
    Log.error("No input!");
    return null;
  }

}
