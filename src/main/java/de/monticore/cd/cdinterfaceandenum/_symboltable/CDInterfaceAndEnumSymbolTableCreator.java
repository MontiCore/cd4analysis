/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdinterfaceandenum._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd.cdbasis.CDBasisMill;
import de.monticore.cd.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cd.cdbasis._symboltable.CDTypeSymbolLoader;
import de.monticore.cd.cdbasis._visitor.SymModifierVisitor;
import de.monticore.cd.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.cd.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cd.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cd.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumSymbolTableCreator
    extends CDInterfaceAndEnumSymbolTableCreatorTOP {
  protected DeriveSymTypeOfCDBasis typeCheck;
  protected SymModifierVisitor symModifierVisitor;

  public CDInterfaceAndEnumSymbolTableCreator(ICDInterfaceAndEnumScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
    init();
  }

  public CDInterfaceAndEnumSymbolTableCreator(Deque<? extends ICDInterfaceAndEnumScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
    init();
  }

  private void init() {
    typeCheck = new DeriveSymTypeOfCDBasis();
    symModifierVisitor = CDBasisMill.symModifierVisitor();
  }

  public DeriveSymTypeOfCDBasis getTypeCheck() {
    return typeCheck;
  }

  public SymModifierVisitor getSymModifierVisitor() {
    return symModifierVisitor;
  }

  @Override
  protected void initialize_CDInterface(CDTypeSymbol symbol, ASTCDInterface ast) {
    super.initialize_CDInterface(symbol, ast);
    symbol.setIsInterface(true);

    symbol.setModifier(getSymModifierVisitor().visitAll(ast.getCDModifierList()).build());

    symbol.setSuperTypeList(ast.getCDExtendUsage().getSuperclassList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeCheck().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the extended interfaces (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
  }

  @Override
  protected void initialize_CDEnum(CDTypeSymbol symbol, ASTCDEnum ast) {
    super.initialize_CDEnum(symbol, ast);
    symbol.setIsEnum(true);

    symbol.setModifier(getSymModifierVisitor().visitAll(ast.getCDModifierList()).build());

    symbol.setInterfaceList(ast.getCDInterfaceUsage().getInterfaceList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeCheck().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the interface (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
  }

  @Override
  protected void initialize_CDEnumConstant(FieldSymbol symbol, ASTCDEnumConstant ast) {
    super.initialize_CDEnumConstant(symbol, ast);
    symbol.setIsVariable(true);

    // read type of "parent"
    final LinkedListMultimap<String, CDTypeSymbol> cdTypeSymbols = ast.getEnclosingScope().getCDTypeSymbols();
    if (cdTypeSymbols.size() == 1) {
      final CDTypeSymbol cdTypeSymbol = cdTypeSymbols.values().get(0);
      symbol.setType(new SymTypeOfObject(new CDTypeSymbolLoader(cdTypeSymbol.getName(), ast.getEnclosingScope())));
    }
    else {
      // handle error
    }
  }
}
