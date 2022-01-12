/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._symboltable;

import de.monticore.cd.CDMill;
import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;
import de.monticore.cdassociation._visitor.CDAssocTypeForSymAssociationVisitor;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdbasis.prettyprint.CDBasisFullPrettyPrinter;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.*;

// TODO SVa: check if all attributes needed, or split for STCompleteTypes
public class CDSymbolTableHelper {
  protected CDBasisFullPrettyPrinter prettyPrinter;
  protected CDTypesCalculator typeChecker;

  protected ModifierHandler modifierHandler;
  protected CDAssociationNavigableVisitor navigableVisitor;
  protected CDAssocTypeForSymAssociationVisitor assocTypeVisitor;

  protected Stack<String> cdTypeStack;
  protected Map<CDRoleSymbol, TypeSymbol> handledRoles;

  protected List<ASTMCImportStatement> imports;
  protected ASTMCQualifiedName packageDeclaration;

  public CDSymbolTableHelper() {
    this(new DeriveSymTypeOfCDBasis());
  }

  public CDSymbolTableHelper(CDTypesCalculator typeChecker) {
    this(new CDBasisFullPrettyPrinter(), typeChecker, CDMill.modifierHandler(), new CDAssociationNavigableVisitor(), new CDAssocTypeForSymAssociationVisitor(), new Stack<>(), new HashMap<>(), new ArrayList<>());
  }

  public CDSymbolTableHelper(CDBasisFullPrettyPrinter prettyPrinter, CDTypesCalculator typeChecker, ModifierHandler modifierHandler, CDAssociationNavigableVisitor navigableVisitor, CDAssocTypeForSymAssociationVisitor assocTypeVisitor, Stack<String> cdTypeStack, Map<CDRoleSymbol, TypeSymbol> handledRoles, List<ASTMCImportStatement> imports) {
    this.prettyPrinter = prettyPrinter;
    this.typeChecker = typeChecker;
    this.modifierHandler = modifierHandler;
    this.navigableVisitor = navigableVisitor;
    this.assocTypeVisitor = assocTypeVisitor;
    this.cdTypeStack = cdTypeStack;
    this.handledRoles = handledRoles;
    this.imports = imports;
  }

  public CDBasisFullPrettyPrinter getPrettyPrinter() {
    return prettyPrinter;
  }

  public CDSymbolTableHelper setPrettyPrinter(CDBasisFullPrettyPrinter prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
    return this;
  }

  public CDTypesCalculator getTypeChecker() {
    return typeChecker;
  }

  public void setTypeChecker(CDTypesCalculator typeChecker) {
    this.typeChecker = typeChecker;
  }

  public ModifierHandler getModifierHandler() {
    return modifierHandler;
  }

  public void setModifierHandler(ModifierHandler modifierHandler) {
    this.modifierHandler = modifierHandler;
  }

  public CDAssociationNavigableVisitor getNavigableVisitor() {
    return navigableVisitor;
  }

  public CDSymbolTableHelper setNavigableVisitor(CDAssociationNavigableVisitor navigableVisitor) {
    this.navigableVisitor = navigableVisitor;
    return this;
  }

  public CDAssocTypeForSymAssociationVisitor getAssocTypeVisitor(SymAssociationBuilder symAssociation) {
    assocTypeVisitor.setSymAssociation(symAssociation);
    return assocTypeVisitor;
  }

  public CDSymbolTableHelper setAssocTypeVisitor(CDAssocTypeForSymAssociationVisitor assocTypeVisitor) {
    this.assocTypeVisitor = assocTypeVisitor;
    return this;
  }

  public Stack<String> getCdTypeStack() {
    return cdTypeStack;
  }

  public CDSymbolTableHelper setCdTypeStack(Stack<String> cdTypeStack) {
    this.cdTypeStack = cdTypeStack;
    return this;
  }

  public CDSymbolTableHelper addToCDTypeStack(String className) {
    this.cdTypeStack.push(className);
    return this;
  }

  public String getCurrentCDTypeOnStack() {
    return this.cdTypeStack.peek();
  }

  public String removeFromCDTypeStack() {
    return this.cdTypeStack.pop();
  }

  public ASTMCQualifiedName getPackageDeclaration() {
    return packageDeclaration;
  }

  public CDSymbolTableHelper setPackageDeclaration(ASTMCQualifiedName packageDeclaration) {
    this.packageDeclaration = packageDeclaration;
    return this;
  }

  public Map<CDRoleSymbol, TypeSymbol> getHandledRoles() {
    return handledRoles;
  }

  public CDSymbolTableHelper setHandledRoles(Map<CDRoleSymbol, TypeSymbol> handledRoles) {
    this.handledRoles = handledRoles;
    return this;
  }

  public TypeSymbol addToHandledRoles(CDRoleSymbol symbol, TypeSymbol type) {
    return this.handledRoles.put(symbol, type);
  }

  public TypeSymbol removeFromHandledAssociations(CDRoleSymbol symbol) {
    return this.handledRoles.remove(symbol);
  }

  public List<ASTMCImportStatement> getImports() {
    return imports;
  }


}
