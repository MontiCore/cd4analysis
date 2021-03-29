/*
 * (c) https://github.com/MontiCore/monticore
 */

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
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

  public CDSymbolTableHelper setImports(List<ASTMCImportStatement> imports) {
    this.imports = imports;
    return this;
  }

  public boolean addImport(ASTMCImportStatement importStatement) {
    return this.imports.add(importStatement);
  }

  public boolean removeImport(ASTMCImportStatement importStatement) {
    return this.imports.remove(importStatement);
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
    symbolTableHelper.getImports().forEach(i -> potentialModelNames.add(i.getQName() + "." + qName));

    return potentialModelNames.stream().map(p -> {
      @SuppressWarnings("UnstableApiUsage") final List<String> nameParts = Splitters.DOT.splitToList(p);
      return IntStream.range(1, nameParts.size()) // always begin with the first element, and stop at the second to last
        .mapToObj(i -> nameParts.stream().limit(i).collect(Collectors.joining("."))).collect(Collectors.toSet());
    }).flatMap(Collection::stream).collect(Collectors.toSet());
  }

  /*
   * computes possible full-qualified name candidates for the symbol named simpleName.
   * The symbol may be imported,
   * be located in the same package,
   * or be defined inside the model itself.
   */
  public static List<String> calcFQNameCandidates(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration, String simpleName) {
    List<String> fqNameCandidates = new ArrayList<>();
    for (ASTMCImportStatement anImport : imports) {
      if (de.monticore.utils.Names.getSimpleName(anImport.getQName()).equals(simpleName)) {
        // top level symbol that has the same name as the node, e.g. diagram symbol
        fqNameCandidates.add(anImport.getQName());
      }
      fqNameCandidates.add(anImport.getQName() + "." + simpleName);
    }
    // The searched symbol might be located in the same package as the artifact
    if (!packageDeclaration.getQName().isEmpty()) {
      fqNameCandidates.add(packageDeclaration + "." + simpleName);
    }

    // Symbol might be defined in the model itself
    fqNameCandidates.add(simpleName);

    return fqNameCandidates;
  }
}
