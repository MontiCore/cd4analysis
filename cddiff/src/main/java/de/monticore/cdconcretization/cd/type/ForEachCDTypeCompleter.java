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

/**
 * Processes reference types that are annotated with the stereotype 'forEach'. The value of the
 * stereotype is expected to be a reference to another model element (hook element). For each
 * incarnation of this hook element, one type incarnation is created parameterized by the hook
 * element incarnation.<br>
 * <br>
 * Currently supported hook elements are:
 *
 * <ul>
 *   <li>types (e.g., 'Foo')
 * </ul>
 */
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
      symbolHandler.setTypeHandler(hookType -> completeTypeParameterizedByType(referenceType, hookType, context));
      // TODO Add support for other hook elements
      symbolHandler.resolveSymbol(context.getReferenceCD().getEnclosingScope(), stereotypeValue.get(), referenceType.get_SourcePositionStart());
    } else {
      super.completeCDForType(concreteCD, referenceType, context);
    }
  }

  /**
   * Creates a new type for each incarnation of the hook type.
   *
   * @param referenceType the original reference type
   * @param hookType the type that is used to parameterize the new type
   * @param context the completion context
   */
  private void completeTypeParameterizedByType(
      ASTCDType referenceType,
      ASTCDType hookType,
      CDCompletionContext context)
      throws CompletionException {

    Set<ASTCDType> hookTypeIncarnations =
        ConcretizationHelper.getCDTypes(context.getConcreteCD()).stream()
            .filter(type -> context.getTypeIncStrategy().isMatched(type, hookType))
            .collect(Collectors.toSet());
    System.out.println(
        "Found type incarnations for " + hookType.getName() + ": " + hookTypeIncarnations);

    for (ASTCDType hookTypeIncarnation : hookTypeIncarnations) {
      // if we have more than one type incarnation, we need to add a suffix to the new type
      String hookIncName = SymbolUtil.getFullNameWithoutCD(hookTypeIncarnation.getSymbol());
      String typeSuffix = hookTypeIncarnations.size() > 1
              ? "_" + NameUtil.escapeQualifiedNameAsIdentifier(hookIncName)
              : "";

      ASTCDType newType = referenceType.deepClone();

      // TODO maybe introduce convention for type names (issue 33)
      // e.g., if the hook type name is a substring of the reference type name, we replace it
      // otherwise we append it with a suffix
      newType.setName(referenceType.getName() + typeSuffix);

      // remove forEach stereotype from concrete attribute but add a reference to the
      // original attribute
      StereotypeUtil.removeForEachStereotype(newType.getModifier());
      StereotypeUtil.addStereotype(
          newType.getModifier(), context.getMappingName(), referenceType.getSymbol().getFullName());
      StereotypeUtil.addIncarnationBindingStereotype(
          newType.getModifier(),
          hookType.getSymbol().getFullName(),
          hookTypeIncarnation.getSymbol().getFullName());
      // not only add the binding to the AST. We also need to remember this while processing
      // this element further!
      // TODO we do not support packages at the moment (see issue 29)
      String newTypeQualifier = context.getConcreteCD().getCDDefinition().getSymbol().getFullName();
      String newTypeFullName = Names.getQualifiedName(newTypeQualifier, newType.getName());
      context
          .getScopedIncarnationBindings()
          .addTypeBinding(newTypeFullName, hookType.getSymbol(), hookTypeIncarnation.getSymbol());

      // 4. pass the new attribute to the next completer
      super.completeCDForType(context.getConcreteCD().getCDDefinition(), newType, context);
    }
  }
}
