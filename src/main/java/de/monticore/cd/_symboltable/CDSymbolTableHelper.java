/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.cd.CDMill;
import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;
import de.monticore.cdassociation._visitor.CDAssocTypeForSymAssociationVisitor;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinterDelegator;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.se_rwth.commons.Names.getQualifier;
import static de.se_rwth.commons.Names.getSimpleName;
import static de.se_rwth.commons.logging.Log.trace;

public class CDSymbolTableHelper {
  protected CDBasisPrettyPrinterDelegator prettyPrinter;
  protected CDTypesCalculator typeChecker;

  protected ModifierHandler modifierHandler;
  protected CDAssociationNavigableVisitor navigableVisitor;
  protected CDAssocTypeForSymAssociationVisitor assocTypeVisitor;

  protected Stack<String> cdTypeStack;
  protected Set<SymAssociation> handledAssociations;

  protected List<ASTMCImportStatement> imports;

  public CDSymbolTableHelper() {
    this(new DeriveSymTypeOfCDBasis());
  }

  public CDSymbolTableHelper(CDTypesCalculator typeChecker) {
    this(new CDBasisPrettyPrinterDelegator(), typeChecker,
        CDMill.modifierHandler(), CDAssociationMill.associationNavigableVisitor(), CDAssociationMill.cDAssocTypeForSymAssociationVisitor(),
        new Stack<>(), new HashSet<>(), new ArrayList<>());
  }

  public CDSymbolTableHelper(CDBasisPrettyPrinterDelegator prettyPrinter, CDTypesCalculator typeChecker, ModifierHandler modifierHandler, CDAssociationNavigableVisitor navigableVisitor, CDAssocTypeForSymAssociationVisitor assocTypeVisitor, Stack<String> cdTypeStack, Set<SymAssociation> handledAssociations, List<ASTMCImportStatement> imports) {
    this.prettyPrinter = prettyPrinter;
    this.typeChecker = typeChecker;
    this.modifierHandler = modifierHandler;
    this.navigableVisitor = navigableVisitor;
    this.assocTypeVisitor = assocTypeVisitor;
    this.cdTypeStack = cdTypeStack;
    this.handledAssociations = handledAssociations;
    this.imports = imports;
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

  public Stack<String> getCdTypeStack() {
    return cdTypeStack;
  }

  public void setCdTypeStack(Stack<String> cdTypeStack) {
    this.cdTypeStack = cdTypeStack;
  }

  public void addToCDTypeStack(String className) {
    this.cdTypeStack.push(className);
  }

  public String getCurrentCDTypeOnStack() {
    return this.cdTypeStack.peek();
  }

  public String removeFromCDTypeStack() {
    return this.cdTypeStack.pop();
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

  public List<ASTMCImportStatement> getImports() {
    return imports;
  }

  public void setImports(List<ASTMCImportStatement> imports) {
    this.imports = imports;
  }

  public boolean addImport(ASTMCImportStatement importStatement) {
    return this.imports.add(importStatement);
  }

  public boolean removeImport(ASTMCImportStatement importStatement) {
    return this.imports.remove(importStatement);
  }

  /**
   * copy from {@link de.monticore.symboltable.IArtifactScope#calculateQualifiedNames(String, String, List)}
   */
  public Set<String> calculateQualifiedNames(String name, String packageName) {
    final Set<String> potentialSymbolNames = new LinkedHashSet<>();

    // the simple name (in default package)
    potentialSymbolNames.add(name);

    // if name is already qualified, no further (potential) names exist.
    if (getQualifier(name).isEmpty()) {
      // maybe the model belongs to the same package
      if (!packageName.isEmpty()) {
        potentialSymbolNames.add(packageName + "." + name);
      }

      for (ASTMCImportStatement importStatement : imports) {
        if (importStatement.isStar()) {
          potentialSymbolNames.add(importStatement.getQName() + "." + name);
        }
        else if (getSimpleName(importStatement.getQName()).equals(name)) {
          potentialSymbolNames.add(importStatement.getQName());
        }
      }
    }
    trace("Potential qualified names for \"" + name + "\": " + potentialSymbolNames.toString(),
        "IArtifactScope");

    return potentialSymbolNames;
  }

  /**
   * Splits a qualified name in a potential model name part
   * (cutting the potential name of the symbol)
   * <pre>
   * name has to have at least 2 parts,
   * where at least the first one is the model name
   * and at least the last one is the symbol name
   * Example:
   * input {@code "de.monticore.cdbasis.parser.Simple.A"} would return
   * {@code
   *        [
   *          "de",
   *          "de.monticore",
   *          "de.monticore.cdbasis",
   *          "de.monticore.cdbasis.parser",
   *          "de.monticore.cdbasis.parser.Simple"
   *        ]}
   * at least the last element is the name of the symbol
   * </pre>
   *
   * @param qName a qualified name, to be splitted for the model name
   * @return a List of potential model names
   */
  public static Set<String> calculateModelNamesSimple(String qName, CDSymbolTableHelper symbolTableHelper) {
    final List<String> potentialModelNames = new ArrayList<>();

    if (!Names.getQualifier(qName).isEmpty()) {
      potentialModelNames.add(qName);
    }
    symbolTableHelper.getImports().forEach(i -> potentialModelNames.add(i + qName));

    return potentialModelNames.stream().map(p -> {
      final List<String> nameParts = Splitters.DOT.splitToList(qName);
      return IntStream.range(1, nameParts.size()) // always begin with the first element, and stop at the second to last
          .mapToObj(i -> nameParts.stream().limit(i).collect(Collectors.joining(".")))
          .collect(Collectors.toSet());
    }).flatMap(Collection::stream).collect(Collectors.toSet());
  }
}