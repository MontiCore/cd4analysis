/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cd4codebasis._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd.cd4codebasis._visitor.CD4CodeSymModifierVisitor;
import de.monticore.cd.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cd.cdbasis._symboltable.CDTypeSymbolLoader;
import de.monticore.cd.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeVoid;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD4CodeBasisSymbolTableCreator
    extends CD4CodeBasisSymbolTableCreatorTOP {
  protected DeriveSymTypeOfCDBasis typeChecker;
  protected CD4CodeSymModifierVisitor symModifierVisitor;

  public CD4CodeBasisSymbolTableCreator(ICD4CodeBasisScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CD4CodeBasisSymbolTableCreator(Deque<? extends ICD4CodeBasisScope> scopeStack) {
    super(scopeStack);
    init();
  }

  protected void init() {
    typeChecker = new DeriveSymTypeOfCDBasis();
    symModifierVisitor = CD4CodeBasisMill.symModifierVisitor();
  }

  public DeriveSymTypeOfCDBasis getTypeChecker() {
    return typeChecker;
  }

  public CD4CodeSymModifierVisitor getSymModifierVisitor() {
    return symModifierVisitor;
  }

  @Override
  protected void initialize_CDMethod(CDMethodSignatureSymbol symbol, ASTCDMethod ast) {
    super.initialize_CDMethod(symbol, ast);

    symbol.setIsMethod(true);

    final Optional<SymTypeExpression> typeResult = getTypeChecker().calculateType(ast.getMCReturnType());
    if (!typeResult.isPresent()) {
      Log.error(String.format("0xA0000: The type of the return type (%s) could not be calculated", ast.getMCReturnType().getClass().getSimpleName()));
    }
    else {
      symbol.setReturnType(typeResult.get());
    }

    symbol.setHasEllipsis(ast.getCDParameterList().stream().anyMatch(ASTCDParameter::isEllipsis));

    symbol.setExceptionList(ast.getCDThrowsDeclaration().getExceptionList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeChecker().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the exception classes (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

    symbol.setModifier(getSymModifierVisitor().visitAll(ast.getCDModifierList()).build());
  }

  @Override
  protected void initialize_CDConstructor(CDMethodSignatureSymbol symbol, ASTCDConstructor ast) {
    super.initialize_CDConstructor(symbol, ast);

    symbol.setIsConstructor(true);
    symbol.setHasEllipsis(ast.getCDParameterList().stream().anyMatch(ASTCDParameter::isEllipsis));

    // TODO SVa: should this be the class type?
    symbol.setReturnType(new SymTypeVoid());

    symbol.setExceptionList(ast.getCDThrowsDeclaration().getExceptionList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeChecker().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the exception classes (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

    symbol.setModifier(symModifierVisitor.visitAll(ast.getCDModifierList()).build());
  }

  @Override
  protected void initialize_CDParameter(FieldSymbol symbol, ASTCDParameter ast) {
    super.initialize_CDParameter(symbol, ast);

    symbol.setIsParameter(true);

    final Optional<SymTypeExpression> typeResult = getTypeChecker().calculateType(ast.getMCType());
    if (!typeResult.isPresent()) {
      Log.error(String.format("0xA0000: The type (%s) of the attribute (%s) could not be calculated", ast.getMCType().getClass().getSimpleName(), ast.getName()));
    }
    else {
      symbol.setType(typeResult.get());
    }
  }

  @Override
  protected void initialize_CD4CodeEnumConstant(FieldSymbol symbol, ASTCD4CodeEnumConstant ast) {
    super.initialize_CD4CodeEnumConstant(symbol, ast);

    symbol.setIsVariable(true);

    // TODO SVa: how to read the parent? build a stack?
    // read type of "parent"
    final LinkedListMultimap<String, CDTypeSymbol> cdTypeSymbols = ast.getEnclosingScope().getCDTypeSymbols();
    if (cdTypeSymbols.size() == 1) {
      final CDTypeSymbol cdTypeSymbol = cdTypeSymbols.values().get(0);
      symbol.setType(new SymTypeOfObject(new CDTypeSymbolLoader(cdTypeSymbol.getName(), ast.getEnclosingScope())));
    }
    else {
      // handle error
    }

    // TODO SVa: where to store the arguments?
  }
}
