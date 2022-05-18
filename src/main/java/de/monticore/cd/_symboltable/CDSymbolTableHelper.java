/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._symboltable;

import de.monticore.cd.CDMill;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;
import de.monticore.cdassociation._visitor.CDAssocTypeForSymAssociationVisitor;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdbasis.prettyprint.CDBasisFullPrettyPrinter;
import de.monticore.cdbasis.typescalculator.FullDeriveFromCDBasis;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.AbstractSynthesize;
import de.monticore.types.check.FullSynthesizeFromMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

// TODO SVa: check if all attributes needed, or split for STCompleteTypes
public class CDSymbolTableHelper {
  protected CDBasisFullPrettyPrinter prettyPrinter;
  protected AbstractDerive typeDeriver;
  protected AbstractSynthesize typeSynthesizer;

  protected ModifierHandler modifierHandler;
  protected CDAssociationNavigableVisitor navigableVisitor;
  protected CDAssocTypeForSymAssociationVisitor assocTypeVisitor;

  protected Stack<String> cdTypeStack;
  protected Map<CDRoleSymbol, TypeSymbol> handledRoles;

  protected ASTMCQualifiedName packageDeclaration;

  public CDSymbolTableHelper() {
    this(new FullDeriveFromCDBasis(), new FullSynthesizeFromMCBasicTypes());
  }

  public CDSymbolTableHelper(AbstractDerive typeDeriver, AbstractSynthesize typeSynthesizer) {
    this(new CDBasisFullPrettyPrinter(), typeDeriver, typeSynthesizer, CDMill.modifierHandler(), new CDAssociationNavigableVisitor(), new CDAssocTypeForSymAssociationVisitor(), new Stack<>(), new HashMap<>());
  }

  public CDSymbolTableHelper(CDBasisFullPrettyPrinter prettyPrinter, AbstractDerive typeDeriver, AbstractSynthesize typeSynthesizer, ModifierHandler modifierHandler, CDAssociationNavigableVisitor navigableVisitor, CDAssocTypeForSymAssociationVisitor assocTypeVisitor, Stack<String> cdTypeStack, Map<CDRoleSymbol, TypeSymbol> handledRoles) {
    this.prettyPrinter = prettyPrinter;
    this.typeDeriver = typeDeriver;
    this.typeSynthesizer = typeSynthesizer;
    this.modifierHandler = modifierHandler;
    this.navigableVisitor = navigableVisitor;
    this.assocTypeVisitor = assocTypeVisitor;
    this.cdTypeStack = cdTypeStack;
    this.handledRoles = handledRoles;
  }

  public CDBasisFullPrettyPrinter getPrettyPrinter() {
    return prettyPrinter;
  }

  public CDSymbolTableHelper setPrettyPrinter(CDBasisFullPrettyPrinter prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
    return this;
  }

  public AbstractSynthesize getTypeSynthesizer() {
    return typeSynthesizer;
  }

  public AbstractDerive getTypeDeriver() {
    return typeDeriver;
  }

  public void setTypeDeriver(AbstractDerive typeDeriver) {
    this.typeDeriver = typeDeriver;
  }

  public void setTypeSynthesizer(AbstractSynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
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

}
