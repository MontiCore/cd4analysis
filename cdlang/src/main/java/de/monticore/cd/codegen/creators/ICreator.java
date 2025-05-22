package de.monticore.cd.codegen.creators;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.Map;

public interface ICreator<T extends ICreator.ICreatedData> extends IDecorator<T> {

  public interface ICreatedData {

    ASTCDCompilationUnit getOriginal();

    ASTCDCompilationUnit getDecorated();

    Map<ASTNode, ASTNode> getOriginalToDecoratedMap();
  }
}
