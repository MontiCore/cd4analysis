/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp.language_access;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.AstPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeAstPrettyPrinter implements AstPrettyPrinter<ASTCDCompilationUnit> {
  
  private final CD4CodeFullPrettyPrinter prettyPrinter = new CD4CodeFullPrettyPrinter(
      new IndentPrinter());
  
  @Override
  public String prettyPrint(ASTCDCompilationUnit node) {
    return prettyPrinter.prettyprint(node);
  }
  
}
