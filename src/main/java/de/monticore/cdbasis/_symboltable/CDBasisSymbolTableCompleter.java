/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.SourcePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd._symboltable.CDSymbolTableHelper.resolveUniqueTypeSymbol;

public class CDBasisSymbolTableCompleter implements CDBasisVisitor2, OOSymbolsVisitor2 {

  private final List<ASTMCImportStatement> imports;
  private final ASTMCQualifiedName packageDeclaration;

  public CDBasisSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.imports = imports;
    this.packageDeclaration = packageDeclaration;
  }

  @Override
  public void visit(FieldSymbol field) {
    // Compute the !final! SymTypeExpression for the type of the field

    // Fetch the preliminary type
    String typeName = field.getType().getTypeInfo().getName();

    SourcePosition sourcePositionStart = field.getAstNode().get_SourcePositionStart();
    SourcePosition sourcePositionEnd = field.getAstNode().get_SourcePositionEnd();

    // store all found type symbols here
    Optional<TypeSymbol> typeSymbol = resolveUniqueTypeSymbol(imports, packageDeclaration, typeName, (ICDBasisScope) field.getEnclosingScope(), sourcePositionStart, sourcePositionEnd);

    // replace the !preliminary! SymTypeExpression stored in the field with the !final! one
    typeSymbol.ifPresent(symbol -> field.setType(SymTypeExpressionFactory.createTypeExpression(symbol)));
  }

  @Override
  public void visit(CDTypeSymbol cdType) {
    // Compute the !final! SymTypeExpression for the extended and implemented types

    List<SymTypeExpression> correctedExtendsExpressions = new ArrayList<>();
    for (SymTypeExpression superType : cdType.getSuperTypesList()) {
      // Fetch the preliminary type
      String typeName = superType.getTypeInfo().getName();

      SourcePosition sourcePositionStart = cdType.getAstNode().get_SourcePositionStart();
      SourcePosition sourcePositionEnd = cdType.getAstNode().get_SourcePositionEnd();

      // store all found type symbols here
      Optional<TypeSymbol> typeSymbol = resolveUniqueTypeSymbol(imports, packageDeclaration, typeName, (ICDBasisScope) cdType.getEnclosingScope(), sourcePositionStart, sourcePositionEnd);
      typeSymbol.ifPresent(symbol -> correctedExtendsExpressions.add(SymTypeExpressionFactory.createTypeExpression(symbol)));
    }
    cdType.setSuperTypesList(correctedExtendsExpressions);
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
