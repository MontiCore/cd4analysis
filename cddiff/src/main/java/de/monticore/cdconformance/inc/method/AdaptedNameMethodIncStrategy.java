/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.method;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdconformance.inc.mctype.MCTypeMatchingStrategy;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Matches a concrete method to a reference method when the concrete method name is the
 * implicit-name-adapted form of the reference method name, derived by applying the type
 * incarnation pairs of the return type and parameter types sequentially.<br>
 * <br>
 * For example, {@code findTicket(String id)} is matched to {@code findTask(String id)} when no
 * CD-type parameter exists but the return type adapts the name, and
 * {@code compareFooAndBar(Foo foo, Bar bar)} is matched to
 * {@code compareInputAndOutput(Input input, Output output)} when {@code Foo} incarnates
 * {@code Input} and {@code Bar} incarnates {@code Output}.
 */
public class AdaptedNameMethodIncStrategy implements CDMethodMatchingStrategy {

  private final BooleanMatchingStrategy<ASTCDType> typeMatcher;
  private final MCTypeMatchingStrategy mcTypeMatcher;
  private ASTCDType refType;

  public AdaptedNameMethodIncStrategy(BooleanMatchingStrategy<ASTCDType> typeMatcher,
      MCTypeMatchingStrategy mcTypeMatcher) {
    this.typeMatcher = typeMatcher;
    this.mcTypeMatcher = mcTypeMatcher;
  }

  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return refType.getCDMethodList().stream().filter(method -> isMatched(concrete, method))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    if (ref.getCDParameterList().size() != concrete.getCDParameterList().size()) {
      return false;
    }

    // Apply all relevant type pairs sequentially to compute the adapted reference method name
    String adaptedName = ref.getName();

    if (ref.getMCReturnType().isPresentMCType() && concrete.getMCReturnType().isPresentMCType()) {
      Optional<ASTCDType> refRetType = resolveCDType(ref.getMCReturnType().getMCType());
      Optional<ASTCDType> conRetType = resolveCDType(concrete.getMCReturnType().getMCType());
      if (refRetType.isPresent() && conRetType.isPresent()
          && typeMatcher.isMatched(conRetType.get(), refRetType.get())) {
        adaptedName = NameUtil.adaptTemplatedName(adaptedName, refRetType.get().getName(),
            conRetType.get().getName()).orElse(adaptedName);
      }
    }

    for (int i = 0; i < ref.getCDParameterList().size(); i++) {
      Optional<ASTCDType> refParamType = resolveCDType(
          ref.getCDParameterList().get(i).getMCType());
      Optional<ASTCDType> conParamType = resolveCDType(
          concrete.getCDParameterList().get(i).getMCType());
      if (refParamType.isPresent() && conParamType.isPresent()
          && typeMatcher.isMatched(conParamType.get(), refParamType.get())) {
        adaptedName = NameUtil.adaptTemplatedName(adaptedName, refParamType.get().getName(),
            conParamType.get().getName()).orElse(adaptedName);
      }
    }

    if (!adaptedName.equals(concrete.getName())) {
      return false;
    }

    // Verify parameter types are incarnation-compatible (in strict order)
    for (int i = 0; i < ref.getCDParameterList().size(); i++) {
      if (!mcTypeMatcher.isMatched(concrete.getCDParameterList().get(i).getMCType(),
          ref.getCDParameterList().get(i).getMCType(), typeMatcher)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
  }

  private Optional<ASTCDType> resolveCDType(ASTMCType mcType) {
    // Only qualified types can be CD types; primitives and collection types cannot
    if (!(mcType instanceof ASTMCQualifiedType)) {
      return Optional.empty();
    }
    // Use global scope lookup to avoid NPE on types without enclosing scope (e.g., deep-cloned
    // elements added during concretization that have not been through symbol table construction)
    String typeName = ((ASTMCQualifiedType) mcType).getMCQualifiedName().getQName();
    return CD4CodeMill.globalScope().resolveCDTypeDown(typeName)
        .filter(sym -> sym.isPresentAstNode())
        .map(CDTypeSymbol::getAstNode);
  }

}
