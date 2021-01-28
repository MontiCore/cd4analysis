/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdbasis._symboltable.CDBasisScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd._symboltable.CDSymbolTableHelper.resolveUniqueTypeSymbol;

public class CD4CodeBasisSymbolTableCompleter implements CD4CodeBasisVisitor2 {

  private final List<ASTMCImportStatement> imports;
  private final ASTMCQualifiedName packageDeclaration;

  public CD4CodeBasisSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.imports = imports;
    this.packageDeclaration = packageDeclaration;
  }

  @Override
  public void visit(CDMethodSignatureSymbol methodSignature) {
    // Compute the !final! SymTypeExpression for the exceptions and the return type

    List<SymTypeExpression> correctedExceptionExpressions = new ArrayList<>();
    for (SymTypeExpression exceptionType : methodSignature.getExceptionsList()) {
      // Fetch the preliminary type
      String typeName = exceptionType.getTypeInfo().getName();
      // store all found type symbols here
      Optional<TypeSymbol> typeSymbol = resolveUniqueTypeSymbol(imports, packageDeclaration, typeName, (CDBasisScope) methodSignature.getAstNode());
      typeSymbol.ifPresent(symbol -> correctedExceptionExpressions.add(SymTypeExpressionFactory.createTypeExpression(symbol)));
    }
    methodSignature.setExceptionsList(correctedExceptionExpressions);

    SymTypeExpression returnType = methodSignature.getReturnType();
    if (!returnType.isVoidType()) {
      // Fetch the preliminary type
      String typeName = returnType.getTypeInfo().getName();
      Optional<TypeSymbol> typeSymbol = resolveUniqueTypeSymbol(imports, packageDeclaration, typeName, (CDBasisScope) methodSignature.getAstNode());
      typeSymbol.ifPresent(symbol -> methodSignature.setReturnType(SymTypeExpressionFactory.createTypeExpression(symbol)));
    }
  }

  /*
  The following visit methods must be overriden because both implemented interface
  provide default methods for the visit methods.
 */
  public void visit(de.monticore.symboltable.ISymbol node) {
  }

  public void endVisit(de.monticore.symboltable.ISymbol node) {
  }

  public void endVisit(de.monticore.ast.ASTNode node) {
  }

  public void visit(de.monticore.ast.ASTNode node) {
  }

  public void visit(de.monticore.symboltable.IScope node) {
  }

  public void endVisit(de.monticore.symboltable.IScope node) {
  }

}
