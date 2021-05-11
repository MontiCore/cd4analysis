package de.monticore.cdinterfaceandenum._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumSymbolTableCompleter
    implements CDInterfaceAndEnumVisitor2 {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDInterfaceAndEnumSymbolTableCompleter(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
  }

  public CDInterfaceAndEnumSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = new CDSymbolTableHelper()
        .setImports(imports)
        .setPackageDeclaration(packageDeclaration);
  }

  @Override
  public void visit(ASTCDInterface node) {
    CDInterfaceAndEnumVisitor2.super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    CDInterfaceAndEnumVisitor2.super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
    initialize_CDInterface(node);
  }

  @Override
  public void visit(ASTCDEnum node) {
    CDInterfaceAndEnumVisitor2.super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    CDInterfaceAndEnumVisitor2.super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
    initialize_CDEnum(node);
  }

  @Override
  public void endVisit(ASTCDEnumConstant node) {
    initialize_CDEnumConstant(node);
  }

  protected void initialize_CDInterface(ASTCDInterface ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsInterface(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    if (ast.isPresentCDExtendUsage()) {
      symbol.addAllSuperTypes(ast.getCDExtendUsage().streamSuperclass().map(s -> {
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format("0xCDA30: The type of the extended interfaces (%s) could not be calculated", symbolTableHelper.getPrettyPrinter().prettyprint(s)), s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  protected void initialize_CDEnum(ASTCDEnum ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsEnum(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    if (ast.isPresentCDInterfaceUsage()) {
      symbol.addAllSuperTypes(ast.getCDInterfaceUsage().streamInterface().map(s -> {
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format("0xCDA31: The type of the interface (%s) could not be calculated", s.getClass().getSimpleName()), s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  protected void initialize_CDEnumConstant(ASTCDEnumConstant ast) {
    FieldSymbol symbol = ast.getSymbol();

    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);

    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = symbolTableHelper.getCurrentCDTypeOnStack();
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, ast.getEnclosingScope());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }
}
