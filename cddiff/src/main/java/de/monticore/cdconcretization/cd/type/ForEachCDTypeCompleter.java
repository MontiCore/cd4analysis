package de.monticore.cdconcretization.cd.type;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CDRefSymbolHandlerDelegator;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.cd.CDCompletionContext;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.se_rwth.commons.Names;

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
      CDRefSymbolHandlerDelegator symbolHandler = new CDRefSymbolHandlerDelegator();
      symbolHandler.setTypeHandler(rTargetType -> completeTypeParameterizedByType(referenceType, rTargetType, context));
      // TODO Add support for other references
      symbolHandler.resolveSymbol(context.getReferenceCD().getEnclosingScope(), stereotypeValue.get(), referenceType.get_SourcePositionStart());
    } else {
      super.completeCDForType(concreteCD, referenceType, context);
    }
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
      String cTargetTypeIncName = SymbolUtil.getFullNameWithoutCD(cTargetTypeInc.getSymbol());
      String typeSuffix = targetTypeIncarnations.size() > 1
              ? "_" + NameUtil.escapeQualifiedNameAsIdentifier(cTargetTypeIncName)
              : "";

      ASTCDType newType = referenceType.deepClone();

      // TODO maybe introduce convention for type names
      // e.g., if the target name is a substring of the reference type name, we replace it
      // otherwise we append it with a suffix
      newType.setName(referenceType.getName() + typeSuffix);

      // remove forEach stereotype from concrete attribute but add a reference to the
      // original attribute
      StereotypeUtil.removeForEachStereotype(newType.getModifier());
      StereotypeUtil.addStereotype(
          newType.getModifier(), context.getMappingName(), referenceType.getSymbol().getFullName());
      StereotypeUtil.addIncarnationBindingStereotype(
          newType.getModifier(),
          rTargetType.getSymbol().getFullName(),
          cTargetTypeInc.getSymbol().getFullName());
      // not only add the binding to the AST. We also need to remember this while processing
      // this element further!
      // TODO we do not support packages at the moment (see issue 29)
      String newTypeQualifier = context.getConcreteCD().getCDDefinition().getSymbol().getFullName();
      String newTypeFullName = Names.getQualifiedName(newTypeQualifier, newType.getName());
      context
          .getScopedIncarnationBindings()
          .addTypeBinding(newTypeFullName, rTargetType.getSymbol(), cTargetTypeInc.getSymbol());

      // 4. pass the new attribute to the next completer
      super.completeCDForType(context.getConcreteCD().getCDDefinition(), newType, context);
    }
  }
}
