/* (c) https://github.com/MontiCore/monticore */
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
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processes reference types that are annotated with the stereotype 'forEach'. The value of the
 * stereotype is expected to be a reference to another model element (parameter element). For each
 * incarnation of this parameter element, one type incarnation is created parameterized by the
 * parameter element incarnation.<br>
 * <br>
 * Currently supported parameter elements are:
 *
 * <ul>
 * <li>types (e.g., 'Foo')
 * </ul>
 */
public class ForEachTypeInCDCompleter extends AbstractTypeInCDCompleter {
  
  private static final String LOG_NAME = ForEachTypeInCDCompleter.class.getName();
  
  @Override
  public void completeTypeInCD(ASTCDDefinition concreteCD, ASTCDType referenceType,
      CDCompletionContext context) throws CompletionException {
    Optional<String> stereotypeValue = StereotypeUtil.getForEachStereotypeValue(referenceType
        .getModifier(), "Stereotype value must not be empty for stereotype 'forEach' @ "
            + referenceType.get_SourcePositionStart());
    if (stereotypeValue.isPresent()) {
      CDRefSymbolHandlerDelegator symbolHandler = new CDRefSymbolHandlerDelegator();
      symbolHandler.setTypeHandler(paramType -> completeTypeParameterizedByType(referenceType,
          paramType, context));
      // TODO Add support for other parameter elements
      symbolHandler.resolveSymbol(context.getReferenceCD().getEnclosingScope(), stereotypeValue
          .get(), referenceType.get_SourcePositionStart());
    }
    else {
      super.completeTypeInCD(concreteCD, referenceType, context);
    }
  }
  
  /**
   * Creates a new type for each incarnation of the parameter type.
   *
   * @param referenceType the original reference type
   * @param paramType the type that is used to parameterize the new type
   * @param context the completion context
   */
  private void completeTypeParameterizedByType(ASTCDType referenceType, ASTCDType paramType,
      CDCompletionContext context) throws CompletionException {
    
    Set<ASTCDType> paramTypeIncarnations = ConcretizationHelper.getCDTypes(context.getConcreteCD())
        .stream().filter(type -> context.getTypeIncStrategy().isMatched(type, paramType)).collect(
            Collectors.toSet());
    Log.debug("Found type incarnations for " + paramType.getName() + ": " + paramTypeIncarnations,
        LOG_NAME);
    
    for (ASTCDType paramTypeInc : paramTypeIncarnations) {
      ASTCDType newType = referenceType.deepClone();
      
      // 1. decide name of the new type
      Optional<String> adaptedName = NameUtil.adaptTemplatedName(referenceType.getName(), paramType
          .getName(), paramTypeInc.getName());
      if (context.isForEachNameAdaptationEnabled() && adaptedName.isPresent()) {
        newType.setName(adaptedName.get());
      }
      else {
        // Default: add the param incarnation name as suffix
        String paramIncName = SymbolUtil.getFullNameWithoutCD(paramTypeInc.getSymbol());
        String typeSuffix = paramTypeIncarnations.size() > 1 ? "_" + NameUtil
            .escapeQualifiedNameAsIdentifier(paramIncName) : "";
        newType.setName(referenceType.getName() + typeSuffix);
      }
      
      // remove forEach stereotype from concrete type but add a reference to the original type
      StereotypeUtil.removeForEachStereotype(newType.getModifier());
      StereotypeUtil.addStereotype(newType.getModifier(), context.getMappingName(), referenceType
          .getSymbol().getFullName());
      
      // Remember the "binding" while processing this element further!
      // TODO we do not support packages at the moment (see issue 29)
      String newTypeQualifier = context.getConcreteCD().getCDDefinition().getSymbol().getFullName();
      String newTypeFullName = Names.getQualifiedName(newTypeQualifier, newType.getName());
      context.getScopedIncarnationBindings().addTypeBinding(newTypeFullName, paramType.getSymbol(),
          paramTypeInc.getSymbol());
      
      // 4. pass the new type to the next completer
      super.completeTypeInCD(context.getConcreteCD().getCDDefinition(), newType, context);
    }
  }
  
}
