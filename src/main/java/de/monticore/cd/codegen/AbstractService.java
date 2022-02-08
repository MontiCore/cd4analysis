/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDPackageSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AbstractService<T extends AbstractService> {

  protected final MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  public List<DiagramSymbol> getSuperCDsDirect(DiagramSymbol cdSymbol) {
    // get direct parent CDSymbols
    List<DiagramSymbol> superCDs = ((ICDBasisArtifactScope) cdSymbol.getEnclosingScope()).getImportsList().stream()
        .map(i -> i.getStatement())
        .map(x -> resolveCD(cdSymbol, x))
        .collect(Collectors.toList());
    return superCDs;
  }

  protected final LoadingCache<DiagramSymbol, List<DiagramSymbol>> superCDsTransitiveCache = CacheBuilder.newBuilder()
          .maximumSize(10000)
          .build(new CacheLoader<DiagramSymbol, List<DiagramSymbol>>() {
            @Override
            public List<DiagramSymbol> load(@Nonnull DiagramSymbol cdSymbol) {
              return getSuperCDsTransitiveUncached(cdSymbol);
            }
          });

  // Cache this methods return value
  protected List<DiagramSymbol> getSuperCDsTransitiveUncached(DiagramSymbol cdSymbol) {
    // get direct parent CDSymbols
    List<DiagramSymbol> directSuperCdSymbols = ((ICDBasisArtifactScope) cdSymbol.getEnclosingScope()).getImportsList().stream()
            .map(i -> i.getStatement())
            .map(x -> resolveCD(cdSymbol, x))
            .collect(Collectors.toList());
    // search for super Cds in super Cds
    List<DiagramSymbol> resolvedCds = new ArrayList<>(directSuperCdSymbols);
    for (DiagramSymbol superSymbol : directSuperCdSymbols) {
      List<DiagramSymbol> superCDs = getSuperCDsTransitive(superSymbol);
      for (DiagramSymbol superCD : superCDs) {
        if (resolvedCds
                .stream()
                .noneMatch(c -> c.getFullName().equals(superCD.getFullName()))) {
          resolvedCds.add(superCD);
        }
      }
    }
    return resolvedCds;
  }

  protected DiagramSymbol resolveCD(DiagramSymbol cdSymbol, String qualifiedName) {
    Set<DiagramSymbol> symbols = Sets.newHashSet(cdSymbol.getEnclosingScope().resolveDiagramMany(qualifiedName));
    if (symbols.size() == 1) {
      return symbols.iterator().next();
    } else {
      Log.error("0x110C10 Cannot resolve classdiagram " + qualifiedName);
      return null;
    }
  }

  public List<DiagramSymbol> getSuperCDsTransitive(DiagramSymbol cdSymbol) {
    return this.superCDsTransitiveCache.getUnchecked(cdSymbol);
  }

  public List<CDTypeSymbol> getAllCDTypes(DiagramSymbol cdSymbol) {
    List<CDPackageSymbol> directPackages = ((ICDBasisArtifactScope) cdSymbol.getEnclosingScope()).getLocalCDPackageSymbols().stream().collect(Collectors.toList());
    List<CDTypeSymbol> types = Lists.newArrayList();
    directPackages.forEach(p -> types.addAll(p.getSpannedScope().getLocalCDTypeSymbols()));
    return types;
  }

  protected List<CDTypeSymbol> getAllSuperClassesTransitive(CDTypeSymbol cdTypeSymbol) {
    List<CDTypeSymbol> superSymbolList = new ArrayList<>();
    if (cdTypeSymbol.isPresentSuperClass()) {
      String superName = cdTypeSymbol.getSuperClass().getTypeInfo().getFullName();
      Optional<CDTypeSymbol> superSymbol = CD4CodeMill.globalScope().resolveCDType(superName);
      if (superSymbol.isPresent()) {
        superSymbolList.add(superSymbol.get());
        superSymbolList.addAll(getAllSuperClassesTransitive(superSymbol.get()));
      }
    }
    return superSymbolList;
  }

  public String getPackage(DiagramSymbol cdSymbol) {
    if (cdSymbol.getPackageName().isEmpty()) {
      return String.join(".", cdSymbol.getName(), getSubPackage()).toLowerCase();
    }
    return String.join(".", cdSymbol.getPackageName(), cdSymbol.getName(), getSubPackage()).toLowerCase();
  }

  public String getSubPackage() {
    return "";
  }


  /**
   * method should be overwritten in SubClasses of the AbstractService to return the correct type
   */
  protected T createService() {
    return (T) new AbstractService();
  }


  /**
   * checking for duplicate classes and methods
   */
  public boolean isClassOverwritten(ASTCDType astcdClass, List<ASTCDClass> classList) {
    //if there is a Class with the same name in the current CompilationUnit, then the methods are only generated once
    return classList.stream().anyMatch(x -> x.getName().endsWith(astcdClass.getName()));
  }

  public boolean isClassOverwritten(String className, List<ASTCDClass> classList) {
    //if there is a Class with the same name in the current CompilationUnit, then the methods are only generated once
    return classList.stream().anyMatch(x -> x.getName().equals(className));
  }

  public boolean isMethodAlreadyDefined(String methodname, List<ASTCDMethod> definedMethods) {
    return definedMethods
        .stream()
        .anyMatch(x -> x.getName().equals(methodname));
  }

  public boolean isMethodAlreadyDefined(ASTCDMethod method, List<ASTCDMethod> definedMethods) {
    return definedMethods
        .stream()
        .anyMatch(x -> isSameMethodSignature(method, x));
  }

  public List<ASTCDMethod> getMethodListWithoutDuplicates(List<ASTCDMethod> astRuleMethods, List<ASTCDMethod> attributeMethods) {
    List<ASTCDMethod> methodList = new ArrayList<>(attributeMethods);
    for (int i = 0; i < astRuleMethods.size(); i++) {
      ASTCDMethod cdMethod = astRuleMethods.get(i);
      for (int j = 0; j < attributeMethods.size(); j++) {
        if (isSameMethodSignature(cdMethod, attributeMethods.get(j))) {
          methodList.remove(attributeMethods.get(j));
        }
      }
    }
    return methodList;
  }

  protected boolean isSameMethodSignature(ASTCDMethod method1, ASTCDMethod method2) {
    if (!method1.getName().equals(method2.getName()) || method1.sizeCDParameters() != method2.sizeCDParameters()) {
      return false;
    }
    for (int i = 0; i < method1.getCDParameterList().size(); i++) {
      if (!method1.getCDParameter(i).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())
          .equals(method2.getCDParameter(i).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()))) {
        return false;
      }
    }
    return true;
  }

  protected int count = 0;

  public String getGeneratedErrorCode(String name) {
    // Use the string representation
    // also use a count to make sure no double codes can appear
    // because sometimes there is not enough information for a unique string
    String codeString = name + count;
    count++;
    //calculate hashCode, but limit the values to have at most 5 digits
    int hashCode = Math.abs(codeString.hashCode() % 100000);
    //use String formatting to add leading zeros to always have 5 digits
    String errorCodeSuffix = String.format("%05d", hashCode);
    return "x" + errorCodeSuffix;
  }

  /**
   * It's possible to overwrite this method if the attribute has prefixes
   */
  public String getNativeAttributeName(String attributeName) {
    return attributeName;
  }

  public ASTMCType getFirstTypeArgument(ASTMCType type) {
    if (type instanceof ASTMCGenericType) {
      ASTMCGenericType genericType = (ASTMCGenericType) type;
      if (!genericType.isEmptyMCTypeArguments()) {
        return genericType.getMCTypeArgument(0).getMCTypeOpt().get();
      }
    }
    Log.error("0x110C11 InternalError: type is not optional");
    return null; // May not happen
  }

  public boolean hasDerivedAttributeName(ASTCDAttribute astcdAttribute) {
    return false;
  }

}
