/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._prettyprint.CDBasisFullPrettyPrinter;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsVisitor2;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.FullSynthesizeFromMCBasicTypes;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Collectors;

public class CDBasisSymbolTableCompleter implements CDBasisVisitor2, OOSymbolsVisitor2 {
  
  protected CDBasisTraverser traverser;
  //TODO remove ISynthesize from the constructor if possible
  protected ISynthesize typeSynthesizer;
  protected CDBasisFullPrettyPrinter prettyPrinter;
  
  public CDBasisSymbolTableCompleter(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
    prettyPrinter = new CDBasisFullPrettyPrinter(new IndentPrinter());
  }
  
  public CDBasisSymbolTableCompleter() {
    this(new FullSynthesizeFromMCBasicTypes());
  }
  
  @Override
  public void visit(ASTCDCompilationUnit node) {
    final ICDBasisScope artifactScope = node.getCDDefinition().getEnclosingScope();
    if (artifactScope instanceof ICD4AnalysisArtifactScope) {
      ((ICD4AnalysisArtifactScope) artifactScope).addAllImports(node.getMCImportStatementList()
          .stream().map(i -> new ImportStatement(i.getQName(), i.isStar())).collect(Collectors
              .toList()));
    }
  }
  
  @Override
  public void visit(ASTCDClass node) {
    
    final CDTypeSymbol symbol = node.getSymbol();
    
    if (node.isPresentCDExtendUsage()) {
      symbol.addAllSuperTypes(node.getCDExtendUsage().streamSuperclass().map(s -> {
        final SymTypeExpression result = TypeCheck3.symTypeFromAST(s);
        if (result == null) {
          Log.error(String.format(
              "0xCDA00: The type of the extended classes (%s) could not be calculated", CDBasisMill
                  .prettyPrint(s, false)), s.get_SourcePositionStart());
        }
        return result;
      }).filter(res -> res != null && !res.isObscureType()).collect(Collectors.toList()));
    }
    
    if (node.isPresentCDInterfaceUsage()) {
      symbol.addAllSuperTypes(node.getCDInterfaceUsage().streamInterface().map(s -> {
        final SymTypeExpression result = TypeCheck3.symTypeFromAST(s);
        if (result == null) {
          Log.error(String.format("0xCDA01: The type of the interface (%s) could not be calculated",
              s.getClass().getSimpleName()), s.get_SourcePositionStart());
        }
        return result;
      }).filter(res -> res != null && !res.isObscureType()) // Filtert ungültige Typen direkt aus
          .collect(Collectors.toList()));
    }
  }
  
  @Override
  public void endVisit(ASTCDClass node) {
    assert node.getSymbol() != null;
    initialize_CDClass(node);
    CDBasisVisitor2.super.endVisit(node);
  }
  
  protected void initialize_CDClass(ASTCDClass ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsClass(true);
    setupModifiers(ast.getModifier(), symbol);
  }
  
  @Override
  public void visit(ASTCDAttribute node) {
    final FieldSymbol symbol = node.getSymbol();
    
    // Compute the !final! SymTypeExpression for the type of the field
    final SymTypeExpression typeResult = TypeCheck3.symTypeFromAST(node.getMCType());
    if (typeResult == null) {
      Log.error(String.format(
          "0xCDA02: The type (%s) of the attribute (%s) could not be calculated", CDBasisMill
              .prettyPrint(node.getMCType(), false), node.getName()), node.getMCType()
                  .get_SourcePositionStart());
    }
    else {
      symbol.setType(typeResult);
    }
  }
  
  @Override
  public void endVisit(ASTCDAttribute node) {
    assert node.getSymbol() != null;
    initialize_CDAttribute(node);
    CDBasisVisitor2.super.endVisit(node);
  }
  
  protected void initialize_CDAttribute(ASTCDAttribute ast) {
    FieldSymbol symbol = ast.getSymbol();
    setupModifiers(ast.getModifier(), symbol);
  }
  
  public void setupModifiers(ASTModifier modifier, CDTypeSymbol typeSymbol) {
    typeSymbol.setIsPublic(modifier.isPublic());
    typeSymbol.setIsPrivate(modifier.isPrivate());
    typeSymbol.setIsProtected(modifier.isProtected());
    typeSymbol.setIsStatic(modifier.isStatic());
    typeSymbol.setIsAbstract(modifier.isAbstract());
    typeSymbol.setIsDerived(modifier.isDerived());
  }
  
  public void setupModifiers(ASTModifier modifier, FieldSymbol fieldSymbol) {
    fieldSymbol.setIsPublic(modifier.isPublic());
    fieldSymbol.setIsPrivate(modifier.isPrivate());
    fieldSymbol.setIsProtected(modifier.isProtected());
    fieldSymbol.setIsStatic(modifier.isStatic());
    fieldSymbol.setIsFinal(modifier.isFinal());
    fieldSymbol.setIsDerived(modifier.isDerived());
  }
  
  public CDBasisTraverser getTraverser() { return traverser; }
  
  public void setTraverser(CDBasisTraverser traverser) { this.traverser = traverser; }
  
}
