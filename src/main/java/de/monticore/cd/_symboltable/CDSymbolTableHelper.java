/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;
import de.monticore.cdassociation._visitor.CDAssocTypeForSymAssociationVisitor;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdbasis.modifier.ModifierHandler;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinterDelegator;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class CDSymbolTableHelper {
  protected CDBasisPrettyPrinterDelegator prettyPrinter;
  protected CDTypesCalculator typeChecker;

  protected ModifierHandler modifierHandler;
  protected CDAssociationNavigableVisitor navigableVisitor;
  protected CDAssocTypeForSymAssociationVisitor assocTypeVisitor;

  protected Stack<String> classStack;
  protected Stack<String> enumStack;
  protected Set<SymAssociation> handledAssociations;

  public CDSymbolTableHelper() {
    this(new CDBasisPrettyPrinterDelegator(), new DeriveSymTypeOfCDBasis(),
        new ModifierHandler(), CDAssociationMill.associationNavigableVisitor(), new CDAssocTypeForSymAssociationVisitor(),
        new Stack<>(), new Stack<>(), new HashSet<>());
  }

  public CDSymbolTableHelper(CDTypesCalculator typeChecker) {
    this(new CDBasisPrettyPrinterDelegator(), typeChecker,
        new ModifierHandler(), CDAssociationMill.associationNavigableVisitor(), new CDAssocTypeForSymAssociationVisitor(),
        new Stack<>(), new Stack<>(), new HashSet<>());
  }

  public CDSymbolTableHelper(CDBasisPrettyPrinterDelegator prettyPrinter, CDTypesCalculator typeChecker, ModifierHandler modifierHandler, CDAssociationNavigableVisitor navigableVisitor, CDAssocTypeForSymAssociationVisitor assocTypeVisitor, Stack<String> classStack, Stack<String> enumStack, Set<SymAssociation> handledAssociations) {
    this.prettyPrinter = prettyPrinter;
    this.typeChecker = typeChecker;
    this.modifierHandler = modifierHandler;
    this.navigableVisitor = navigableVisitor;
    this.assocTypeVisitor = assocTypeVisitor;
    this.classStack = classStack;
    this.enumStack = enumStack;
    this.handledAssociations = handledAssociations;
  }

  public CDBasisPrettyPrinterDelegator getPrettyPrinter() {
    return prettyPrinter;
  }

  public void setPrettyPrinter(CDBasisPrettyPrinterDelegator prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
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

  public void setNavigableVisitor(CDAssociationNavigableVisitor navigableVisitor) {
    this.navigableVisitor = navigableVisitor;
  }

  public CDAssocTypeForSymAssociationVisitor getAssocTypeVisitor(SymAssociationBuilder symAssociation) {
    assocTypeVisitor.setSymAssociation(symAssociation);
    return assocTypeVisitor;
  }

  public void setAssocTypeVisitor(CDAssocTypeForSymAssociationVisitor assocTypeVisitor) {
    this.assocTypeVisitor = assocTypeVisitor;
  }

  public Stack<String> getClassStack() {
    return classStack;
  }

  public void setClassStack(Stack<String> classStack) {
    this.classStack = classStack;
  }

  public void addToClassStack(String className) {
    this.classStack.push(className);
  }

  public String getCurrentClassOnStack() {
    return this.classStack.peek();
  }

  public String removeFromClassStack() {
    return this.classStack.pop();
  }

  public Stack<String> getEnumStack() {
    return enumStack;
  }

  public void setEnumStack(Stack<String> enumStack) {
    this.enumStack = enumStack;
  }

  public void addToEnumStack(String enumName) {
    this.enumStack.push(enumName);
  }

  public String getCurrentEnumOnStack() {
    return this.enumStack.peek();
  }

  public String removeFromEnumStack() {
    return this.enumStack.pop();
  }

  public Set<SymAssociation> getHandledAssociations() {
    return handledAssociations;
  }

  public void setHandledAssociations(Set<SymAssociation> handledAssociations) {
    this.handledAssociations = handledAssociations;
  }

  public boolean addToHandledAssociations(SymAssociation handledAssociation) {
    return this.handledAssociations.add(handledAssociation);
  }

  public boolean removeFromHandledAssociations(SymAssociation handledAssociation) {
    return this.handledAssociations.remove(handledAssociation);
  }
}
