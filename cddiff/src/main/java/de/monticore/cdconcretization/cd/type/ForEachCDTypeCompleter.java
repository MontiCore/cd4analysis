package de.monticore.cdconcretization.cd.type;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.StereotypeUtil;
import de.monticore.cdconcretization.cd.CDCompletionContext;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ForEachCDTypeCompleter extends AbstractCDTypeCompleter {

  @Override
  public void completeCDForType(
      ASTCDDefinition concreteCD, ASTCDType referenceType, CDCompletionContext context)
      throws CompletionException {
    Optional<String> stereotypeValue =
        StereotypeUtil.getForEachStereotypeValue(
            referenceType.getModifier(),
            "Stereotype value must not be empty for stereotype 'forEach' @ "
                + referenceType.get_SourcePositionStart());
    if (stereotypeValue.isPresent()) {
      boolean processed = processAsTypeReference(referenceType, context, stereotypeValue.get());
      // TODO Support other references than types
      if (!processed) {
        throw new CompletionException(
            "Unsupported forEach reference expression" + stereotypeValue.get());
      }
    } else {
      super.completeCDForType(concreteCD, referenceType, context);
    }
  }

  private boolean processAsTypeReference(
      ASTCDType referenceType, CDCompletionContext context, String referenceExpr)
      throws CompletionException {
    Optional<TypeSymbol> typeSymbol =
        context.getReferenceCD().getEnclosingScope().resolveType(referenceExpr);
    if (typeSymbol.isPresent()) {
      ASTType type = typeSymbol.get().getAstNode();
      // is field an attribute?
      if (type instanceof ASTCDType) {
        ASTCDType rTargetType = (ASTCDType) type;
        completeTypeParameterizedByType(referenceType, rTargetType, context);
        return true;
      } else {
        throw new CompletionException(
            "Referenced type symbol "
                + referenceExpr
                + " is not a CDType! (type: "
                + type.getClass().getName()
                + ")");
      }
    }
    return false;
  }

  private void completeTypeParameterizedByType(
      ASTCDType referenceType,
      ASTCDType rTargetType, // TODO maybe name paramType instead of 'target' ?
      CDCompletionContext context)
      throws CompletionException {

    Set<ASTCDType> targetTypeIncarnations =
        ConcretizationHelper.getCDTypes(context.getConcreteCD()).stream()
            .filter(type -> context.getTypeIncStrategy().isMatched(type, rTargetType))
            .collect(Collectors.toSet());
    System.out.println(
        "Found type incarnations for " + rTargetType.getName() + ": " + targetTypeIncarnations);

    for (ASTCDType cTargetTypeInc : targetTypeIncarnations) {
      // if we have more than one type incarnation, we need to add a suffix to the new type
      String typeSuffix = targetTypeIncarnations.size() > 1 ? "_" + cTargetTypeInc.getName() : "";

      ASTCDType newType = referenceType.deepClone();

      // TODO maybe introduce convention for type names
      // e.g., if the target name is a substring of the reference type name, we replace it
      // otherwise we append it with a suffix
      newType.setName(referenceType.getName() + typeSuffix);

      // remove forEach stereotype from concrete attribute but add a reference to the
      // original attribute
      StereotypeUtil.removeForEachStereotype(newType.getModifier());
      StereotypeUtil.addStereotype(
          newType.getModifier(),
          context.getMappingName(),
          referenceType.getSymbol().getFullName());
      StereotypeUtil.addIncarnationBindingStereotype(
          newType.getModifier(),
          rTargetType.getSymbol().getFullName(),
          cTargetTypeInc.getSymbol().getFullName());

      // 4. pass the new attribute to the next completer
      super.completeCDForType(
          context.getConcreteCD().getCDDefinition(), newType, context);
    }
  }
}
