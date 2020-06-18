/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.modifier.ModifierHandler;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinterDelegator;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDBasisSymbolTableCreator extends CDBasisSymbolTableCreatorTOP {
  protected CDBasisPrettyPrinterDelegator prettyPrinter;
  protected DeriveSymTypeOfCDBasis typeChecker;
  protected ModifierHandler modifierHandler;

  public CDBasisSymbolTableCreator(ICDBasisScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
    init();
  }

  public CDBasisSymbolTableCreator(Deque<? extends ICDBasisScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
    init();
  }

  private void init() {
    prettyPrinter = new CDBasisPrettyPrinterDelegator();
    typeChecker = new DeriveSymTypeOfCDBasis();
    modifierHandler = new ModifierHandler();
  }

  public CDBasisPrettyPrinterDelegator getPrettyPrinter() {
    return prettyPrinter;
  }

  public DeriveSymTypeOfCDBasis getTypeChecker() {
    return typeChecker;
  }

  public ModifierHandler getModifierHandler() {
    return this.modifierHandler;
  }

  public void setPrettyPrinter(CDBasisPrettyPrinterDelegator prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
  }

  public void setTypeChecker(DeriveSymTypeOfCDBasis typeChecker) {
    this.typeChecker = typeChecker;
  }

  public void setModifierHandler(ModifierHandler modifierHandler) {
    this.modifierHandler = modifierHandler;
  }

  @Override
  public CDBasisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    final CDBasisArtifactScope artifactScope = super.createFromAST(rootNode);
    artifactScope.setPackageName(Names.getQualifiedName(rootNode.getCDPackageStatement().getPackageList()));

    return artifactScope;
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    Log.debug("Building Symboltable for CD: " + node.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());

    super.visit(node);
  }

  @Override
  protected void initialize_CDClass(CDTypeSymbol symbol, ASTCDClass ast) {
    super.initialize_CDClass(symbol, ast);
    symbol.setIsClass(true);

    getModifierHandler().handle(ast.getModifier(), symbol);

    symbol.setSuperTypeList(ast.getCDExtendUsage().getSuperclassList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeChecker().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the extended classes (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

    symbol.setInterfaceList(ast.getCDInterfaceUsage().getInterfaceList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeChecker().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the interface (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
  }

  @Override
  protected void initialize_CDAttribute(FieldSymbol symbol, ASTCDAttribute ast) {
    super.initialize_CDAttribute(symbol, ast);
    symbol.setIsVariable(true);

    getModifierHandler().handle(ast.getModifier(), symbol);

    final Optional<SymTypeExpression> typeResult = getTypeChecker().calculateType(ast.getMCType());
    if (!typeResult.isPresent()) {
      Log.error(String.format("0xA0000: The type (%s) of the attribute (%s) could not be calculated", getPrettyPrinter().prettyprint(ast.getMCType()), ast.getName()));
    }
    else {
      symbol.setType(typeResult.get());
    }

    // don't store the initial value in the ST
  }
}
