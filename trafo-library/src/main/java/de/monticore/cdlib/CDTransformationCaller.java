package de.monticore.cdlib;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.io.IOException;
import java.util.Map;

public interface CDTransformationCaller {
  boolean apply(ASTCDCompilationUnit ast, Map<String, CDTransformationParameter<?>> params) throws IOException;
}
