/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
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
  private final ICDBasisScope conScope;
  private final ICDBasisScope refScope;
  private ASTCDType refType;

  public AdaptedNameMethodIncStrategy(BooleanMatchingStrategy<ASTCDType> typeMatcher,
      MCTypeMatchingStrategy mcTypeMatcher,
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD) {
    this.typeMatcher = typeMatcher;
    this.mcTypeMatcher = mcTypeMatcher;
    this.conScope = concreteCD.getEnclosingScope();
    this.refScope = referenceCD.getEnclosingScope();
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
      Optional<ASTCDType> refRetType = resolveCDType(ref.getMCReturnType().getMCType(), refScope);
      Optional<ASTCDType> conRetType = resolveCDType(concrete.getMCReturnType().getMCType(), conScope);
      if (refRetType.isPresent() && conRetType.isPresent()
          && typeMatcher.isMatched(conRetType.get(), refRetType.get())) {
        adaptedName = NameUtil.adaptTemplatedName(adaptedName, refRetType.get().getName(),
            conRetType.get().getName()).orElse(adaptedName);
      }
    }

    for (int i = 0; i < ref.getCDParameterList().size(); i++) {
      Optional<ASTCDType> refParamType = resolveCDType(
          ref.getCDParameterList().get(i).getMCType(), refScope);
      Optional<ASTCDType> conParamType = resolveCDType(
          concrete.getCDParameterList().get(i).getMCType(), conScope);
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

  private Optional<ASTCDType> resolveCDType(ASTMCType mcType, ICDBasisScope scope) {
    if (!(mcType instanceof ASTMCQualifiedType)) {
      return Optional.empty();
    }
    String typeName = ((ASTMCQualifiedType) mcType).getMCQualifiedName().getQName();
    return scope.resolveCDTypeDown(typeName)
        .filter(CDTypeSymbol::isPresentAstNode)
        .map(CDTypeSymbol::getAstNode);
  }

}
