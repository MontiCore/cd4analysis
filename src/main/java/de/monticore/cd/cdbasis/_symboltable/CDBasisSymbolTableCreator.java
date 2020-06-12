/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdbasis._symboltable;

import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.cd.cdbasis.CDBasisMill;
import de.monticore.cd.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd.cdbasis._ast.ASTCDClass;
import de.monticore.cd.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cdbasis._ast.ASTCDDefinition;
import de.monticore.cd.cdbasis._visitor.SymModifierVisitor;
import de.monticore.cd.cdbasis.prettyprint.CDBasisPrettyPrinterDelegator;
import de.monticore.cd.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDBasisSymbolTableCreator extends CDBasisSymbolTableCreatorTOP {
  protected CDBasisPrettyPrinterDelegator prettyPrinter;
  protected DeriveSymTypeOfCDBasis typeChecker;
  protected SymModifierVisitor symModifierVisitor;

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
    symModifierVisitor = CDBasisMill.symModifierVisitor();
  }

  public CDBasisPrettyPrinterDelegator getPrettyPrinter() {
    return prettyPrinter;
  }

  public DeriveSymTypeOfCDBasis getTypeChecker() {
    return typeChecker;
  }

  public SymModifierVisitor getSymModifierVisitor() {
    return symModifierVisitor;
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
  protected void initialize_CDDefinition(CDDefinitionSymbol symbol, ASTCDDefinition ast) {
    super.initialize_CDDefinition(symbol, ast);

    symbol.setModifier(getSymModifierVisitor().visitAll(ast.getCDModifierList()).build());
  }

  @Override
  protected void initialize_CDClass(CDTypeSymbol symbol, ASTCDClass ast) {
    super.initialize_CDClass(symbol, ast);
    symbol.setIsClass(true);

    symbol.setModifier(getSymModifierVisitor().visitAll(ast.getCDModifierList()).build());

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
  protected void initialize_CDAttribute(CDAttributeSymbol symbol, ASTCDAttribute ast) {
    super.initialize_CDAttribute(symbol, ast);
    symbol.setIsVariable(true);

    symbol.setModifier(getSymModifierVisitor().visitAll(ast.getCDModifierList()).build());

    final Optional<SymTypeExpression> typeResult = getTypeChecker().calculateType(ast.getMCType());
    if (!typeResult.isPresent()) {
      Log.error(String.format("0xA0000: The type (%s) of the attribute (%s) could not be calculated", getPrettyPrinter().prettyprint(ast.getMCType()), ast.getName()));
    }
    else {
      symbol.setType(typeResult.get());
    }

    if (ast.isPresentInitial()) {
      final Optional<SymTypeExpression> initialResult = getTypeChecker().calculateType(ast.getMCType());
      if (!initialResult.isPresent()) {
        Log.error(String.format("0xA0000: The type of the value (%s) of the attribute (%s) could not be calculated", getPrettyPrinter().prettyprint(ast.getInitial()), ast.getName()));
      }
      else {
        symbol.setInitial(initialResult.get());
      }
    }
  }
}
