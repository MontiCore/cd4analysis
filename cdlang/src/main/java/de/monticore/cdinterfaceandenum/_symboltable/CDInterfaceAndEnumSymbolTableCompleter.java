/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum._symboltable;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.*;
import de.monticore.types3.TypeCheck3;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumSymbolTableCompleter implements CDInterfaceAndEnumVisitor2 {
  
  //TODO remove ISynthesize from the constructor if possible
  protected ISynthesize typeSynthesizer;
  
  public CDInterfaceAndEnumSymbolTableCompleter(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
  }
  
  public CDInterfaceAndEnumSymbolTableCompleter() {
    this(new FullSynthesizeFromMCBasicTypes());
  }
  
  @Override
  public void endVisit(ASTCDInterface node) {
    initialize_CDInterface(node);
  }
  
  @Override
  public void endVisit(ASTCDEnum node) {
    initialize_CDEnum(node);
  }
  
  @Override
  public void endVisit(ASTCDEnumConstant node) {
    initialize_CDEnumConstant(node);
  }
  
  protected void initialize_CDInterface(ASTCDInterface ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsInterface(true);
    
    setupModifiers(ast.getModifier(), symbol);
    
    if (ast.isPresentCDExtendUsage()) {
      symbol.addAllSuperTypes(ast.getCDExtendUsage().streamSuperclass().map(s -> {
        final SymTypeExpression result = TypeCheck3.symTypeFromAST(s);
        if (result == null) {
          Log.error(String.format(
              "0xCDA30: The type of the extended interfaces (%s) could not be calculated",
              CDInterfaceAndEnumMill.prettyPrint(s, false)), s.get_SourcePositionStart());
        }
        return result;
      }).filter(res -> res != null && !res.isObscureType()).collect(Collectors.toList()));
    }
  }
  
  protected void initialize_CDEnum(ASTCDEnum ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsEnum(true);
    
    setupModifiers(ast.getModifier(), symbol);
    
    if (ast.isPresentCDInterfaceUsage()) {
      symbol.addAllSuperTypes(ast.getCDInterfaceUsage().streamInterface().map(s -> {
        final SymTypeExpression result = TypeCheck3.symTypeFromAST(s);
        if (result == null) {
          Log.error(String.format("0xCDA31: The type of the interface (%s) could not be calculated",
              s.getClass().getSimpleName()), s.get_SourcePositionStart());
        }
        return result;
      }).filter(res -> res != null && !res.isObscureType()).collect(Collectors.toList()));
    }
  }
  
  protected void initialize_CDEnumConstant(ASTCDEnumConstant ast) {
    // this is probably dead code, since it is never executed
    FieldSymbol symbol = ast.getSymbol();
    
    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);
    
    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = ast.getEnclosingScope().getName();
    // call getEnclosingScope() twice, to achieve the correct package name
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, ast
        .getEnclosingScope().getEnclosingScope());
    symbol.setType(typeObject);
    
    // Don't store the arguments in the ST
  }
  
  public void setupModifiers(ASTModifier modifier, CDTypeSymbol typeSymbol) {
    typeSymbol.setIsPublic(modifier.isPublic());
    typeSymbol.setIsPrivate(modifier.isPrivate());
    typeSymbol.setIsProtected(modifier.isProtected());
    typeSymbol.setIsStatic(modifier.isStatic());
    typeSymbol.setIsAbstract(modifier.isAbstract());
    typeSymbol.setIsDerived(modifier.isDerived());
  }
  
}
