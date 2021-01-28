/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import com.google.common.collect.Iterables;
import de.monticore.cd.CDMill;
import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;
import de.monticore.cdassociation._visitor.CDAssocTypeForSymAssociationVisitor;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis.prettyprint.CDBasisFullPrettyPrinter;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

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
  protected Map<CDRoleSymbol, SymTypeExpression> handledRoles;

  protected List<ASTMCImportStatement> imports;

  private static final String USED_BUT_UNDEFINED = "0xCDA80: Type '%s' is used but not defined.";
  private static final String DEFINED_MUTLIPLE_TIMES = "0xCDA81: Type '%s' is defined more than once.";

  public CDSymbolTableHelper() {
    this(new DeriveSymTypeOfCDBasis());
  }

  public CDSymbolTableHelper(CDTypesCalculator typeChecker) {
    this(new CDBasisFullPrettyPrinter(), typeChecker, CDMill.modifierHandler(), CDAssociationMill.associationNavigableVisitor(), CDAssociationMill.cDAssocTypeForSymAssociationVisitor(), new Stack<>(), new HashMap<>(), new ArrayList<>());
  }

  public CDSymbolTableHelper(CDBasisFullPrettyPrinter prettyPrinter, CDTypesCalculator typeChecker, ModifierHandler modifierHandler, CDAssociationNavigableVisitor navigableVisitor, CDAssocTypeForSymAssociationVisitor assocTypeVisitor, Stack<String> cdTypeStack, Map<CDRoleSymbol, SymTypeExpression> handledRoles, List<ASTMCImportStatement> imports) {
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

  public void setPrettyPrinter(CDBasisFullPrettyPrinter prettyPrinter) {
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

  public Map<CDRoleSymbol, SymTypeExpression> getHandledRoles() {
    return handledRoles;
  }

  public void setHandledRoles(Map<CDRoleSymbol, SymTypeExpression> handledRoles) {
    this.handledRoles = handledRoles;
  }

  public SymTypeExpression addToHandledRoles(CDRoleSymbol symbol, SymTypeExpression type) {
    return this.handledRoles.put(symbol, type);
  }

  public SymTypeExpression removeFromHandledAssociations(CDRoleSymbol symbol) {
    return this.handledRoles.remove(symbol);
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
   * Computes the unique type symbol with the simple name simpleTypeName that can be resolved in the CDBasisScope scope
   * via qualifying the simple name with the imports or the packageDeclaration.
   * If no symbol or multiple symbols can be resolved, then this methods logs an error and returns an empty Optional.
   */
  public static Optional<TypeSymbol> resolveUniqueTypeSymbol(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration, String simpleTypeName, ICDBasisScope scope, SourcePosition sourcePositionStart, SourcePosition sourcePositionEnd) {
    // store all found type symbols here
    Set<TypeSymbol> typeSymbols = new HashSet<>();
    // for each potential full< qualified name defining the type..
    for (String fqNameCandidate : calcFQNameCandidates(imports, packageDeclaration, simpleTypeName)) {
      // ========================================================================
      // THE FOLLOWING CODE MUST BE CHANGED AFTER THE DOUBLE RESOLVE BUG IS FIXED
      // Currently, the code removes duplicated findings.
      // As soon as bug is fixed, there cannot be duplicates anymore.
      // ========================================================================

      List<TypeSymbol> curTypeSyms = scope.resolveTypeMany(fqNameCandidate);
      List<OOTypeSymbol> curOOTypeSyms = scope.resolveOOTypeMany(fqNameCandidate);

      List<TypeSymbol> symbolsToAdd = new ArrayList<>();

      // if OOTypeSymbol with same name as TypeSymbol already exists, then do not add the TypeSymbol
      for (int i = 0; i < curTypeSyms.size(); i++) {
        TypeSymbol curTypeSyms_i = curTypeSyms.get(i);
        boolean foundDuplicate = false;
        for(TypeSymbol sym : curOOTypeSyms) {
          if (curTypeSyms_i.getName().equals(sym.getName())) {
            foundDuplicate = true;
          }
        }
        if(!foundDuplicate) {
          symbolsToAdd.add(curTypeSyms_i);
        }
      }
      symbolsToAdd.addAll(curOOTypeSyms);

      // try to resolve the type with the fully qualified name and add it to the list
      typeSymbols.addAll(symbolsToAdd);

      // ========================================================================
      // END OF CODE THAT MUST BE CHANGED AFTER THE DOUBLE RESOLVE BUG IS FIXED
      // ========================================================================
    }

    if (typeSymbols.isEmpty()) {
      // no symbol found => Error, type does not exist
      Log.error(String.format(USED_BUT_UNDEFINED, simpleTypeName), sourcePositionStart, sourcePositionEnd);
      return Optional.empty();
    }
    else if (typeSymbols.size() > 1) {
      // symbol found multiple times => Error, type name ambiguous
      Log.error(String.format(DEFINED_MUTLIPLE_TIMES, simpleTypeName), sourcePositionStart, sourcePositionEnd);
      return Optional.empty();
    }
    else {
      // nice, we found exactly one type
      return Optional.ofNullable(Iterables.getFirst(typeSymbols, null));
    }
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
