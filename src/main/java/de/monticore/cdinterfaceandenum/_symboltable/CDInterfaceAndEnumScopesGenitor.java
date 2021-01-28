/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._symboltable.CDBasisScopesGenitorTOP;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumScopesGenitor extends CDInterfaceAndEnumScopesGenitorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDInterfaceAndEnumScopesGenitor(ICDInterfaceAndEnumScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CDInterfaceAndEnumScopesGenitor(Deque<? extends ICDInterfaceAndEnumScope> scopeStack) {
    super(scopeStack);
    init();
  }

  public CDInterfaceAndEnumScopesGenitor() {
    super();
    init();
  }

  protected void init() {
    symbolTableHelper = new CDSymbolTableHelper();
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public void visit(ASTCDInterface node) {
    super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
    initialize_CDInterface(node);
  }

  @Override
  public void visit(ASTCDEnum node) {
    super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    super.endVisit(node);
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
    assert scopeStack.peekLast() != null;
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, scopeStack.peekLast().getEnclosingScope());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }
}
