package de.monticore.cdconcretization.type.method;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconcretization.CDRefSymbolHandlerDelegator;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.cddiff.CDDiffUtil;
import de.se_rwth.commons.Names;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ForEachMethodCompleter extends AbstractMethodInTypeCompleter {

  @Override
  public void completeMethodInType(
          ASTCDType concreteType, ASTCDMethod referenceMethod, TypeCompletionContext context)
  throws CompletionException {
    Optional<String> stereotypeValue =
        StereotypeUtil.getForEachStereotypeValue(
            referenceMethod.getModifier(),
            "Stereotype value must not be empty for stereotype 'forEach'");
    if (stereotypeValue.isPresent()) {
      CDRefSymbolHandlerDelegator symbolHandler = new CDRefSymbolHandlerDelegator();
      symbolHandler.setAttributeHandler(
          paramAttr -> completeMethodUsingAttribute(referenceMethod, paramAttr, context));
      // TODO Add support for other parameter elements
      symbolHandler.resolveSymbol(
          context.getReferenceType().getSpannedScope(),
          stereotypeValue.get(),
          referenceMethod.get_SourcePositionStart());
      // each handler will call super.completeTypeForAttribute() if necessary
    } else {
      super.completeMethodInType(concreteType, referenceMethod, context);
    }
  }

  private void completeMethodUsingAttribute(
          ASTCDMethod referenceMethod,
          ASTCDAttribute paramAttribute,
          TypeCompletionContext context)
          throws CompletionException {
    // group attribute incarnations by their declaring type
    Map<CDTypeSymbol, List<ASTCDAttribute>> attributesByDeclaringType =
        context.getAttributeIncarnations(paramAttribute).stream()
            .collect(
                Collectors.groupingBy(
                   attr -> (CDTypeSymbol) attr.getEnclosingScope().getSpanningSymbol()));

    for (Map.Entry<CDTypeSymbol, List<ASTCDAttribute>> entry :
            attributesByDeclaringType.entrySet()) {
      CDTypeSymbol paramIncarnationDeclaringType = entry.getKey();
      List<ASTCDAttribute> paramAttributeIncarnations = entry.getValue();

      // if we have more than one declaring type incarnation, we need to add a suffix to the new
      // method name
      String declaringTypeNameWithoutCDQualifier = SymbolUtil.getFullNameWithoutCD(paramIncarnationDeclaringType);
      String declaringTypeSuffix =
              attributesByDeclaringType.size() > 1
                      ? "_" + NameUtil.escapeQualifiedNameAsIdentifier(declaringTypeNameWithoutCDQualifier)
                      : "";

      for (ASTCDAttribute paramAttributeInc : paramAttributeIncarnations) {
        // now we have a specific incarnation of the parameter attribute in the concrete CD.
        // we can now construct a new method based on this incarnation for the concrete type
        ASTCDMethod newMethod = referenceMethod.deepClone();

        // 1. decide name of the new method
        Optional<String> adaptedName = NameUtil.adaptTemplatedName(
                referenceMethod.getName(),
                paramAttribute.getName(),
                paramAttributeInc.getName());
        if (context.isForEachNameAdaptationEnabled() && adaptedName.isPresent()) {
          newMethod.setName(adaptedName.get() + declaringTypeSuffix);
        } else {
          // Default: add the param incarnation name as suffix
          // if we have more than one declaring type incarnation, we need to add a suffix to the
          // new method
          String attributeSuffix = paramAttributeIncarnations.size() > 1
                  ? "_" + paramAttributeInc.getName()
                  : "";
          newMethod.setName(
                  referenceMethod.getName() + declaringTypeSuffix + attributeSuffix);
        }

        // 2. decide return type of the new method
        if (referenceMethod.getMCReturnType().isPresentMCType()
                && referenceMethod.getMCReturnType().getMCType()
                .deepEquals(paramAttribute.getMCType())) {
          // Convention: If the param attribute type matches the reference method return type
          // -> Use the attribute incarnation type as return type
          newMethod.setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                  .setMCType(paramAttributeInc.getMCType()).build());
        }
        // ELSE: Default: keep the return type of the reference attribute resp.
        /*
         * NOTE: The base completer down the line might resolve the type of the reference
         * attribute to multiple incarnations and therefore add even more incarnations of the
         * attribute
         */

        // 3. adapt parameters
        for (ASTCDParameter newParameter : newMethod.getCDParameterList()) {
          // 3.1 parameter name
          Optional<String> adaptedParameterName = NameUtil.adaptTemplatedName(
                  newParameter.getName(),
                  paramAttribute.getName(),
                  paramAttributeInc.getName());
          if (context.isForEachNameAdaptationEnabled() && adaptedParameterName.isPresent()) {
            newParameter.setName(adaptedParameterName.get());
          }
          // ELSE: parameter name stays as is! only needs to be unique in scope of the method

          // 3.2 parameter type
          if (newParameter.getMCType().deepEquals(paramAttribute.getMCType())) {
            // Convention: If the param attribute type matches the reference method parameter type
            // -> Use the attribute incarnation type as parameter type
            newParameter.setMCType(paramAttributeInc.getMCType());
          }
          // ELSE: Default: keep the parameter type of the reference attribute resp.
          /*
           * NOTE: The base completer down the line might resolve the type of the reference
           * attribute to multiple incarnations and therefore add even more incarnations of the
           * attribute
           */
        }

        // 4. remove forEach stereotype from concrete method but add a reference to the
        // original method
        StereotypeUtil.removeForEachStereotype(newMethod.getModifier());
        StereotypeUtil.addStereotype(
                newMethod.getModifier(),
                context.getMappingName(),
                referenceMethod.getSymbol().getFullName());

        String newMethodQualifier = paramIncarnationDeclaringType.getFullName();
        String newMethodFullName = Names.getQualifiedName(newMethodQualifier, newMethod.getName());
        context.getScopedIncarnationBindings().addFieldBinding(
                newMethodFullName,
                paramAttribute.getSymbol(),
                Set.of(paramAttributeInc.getSymbol()));
        // TODO Should we also add a TYPE binding for the type of the parameter attribute?
        // alternative: is the ScopedIncarnationBindings class so "intelligent" that it looks up
        // type bindings by checking field symbol types, method return & parameter types? -> sounds messy!
        // --> first think about how we would use this information -> only to build an advanced
        // incarnation mapping data structure, e.g., as input to the OCL adaptation tool

        // TODO This does not seem right...
        // we need to have the symbol table information when further processing the new method (in BaseMethodInTypeCompleter)
        // TODO is there a way to only update the symbol table for a certain subgraph of the AST?
        // workaround: temporary add the method to the CD type, update symbol table and remove it again
        context.getConcreteType().addCDMember(newMethod);
        CDDiffUtil.refreshSymbolTable(context.getConcreteCD());
        context.getConcreteType().removeCDMember(newMethod);
        // TODO we should refresh the symbol table again here to get back to the old state, right?

        // 5. pass the new method to the next completer
        super.completeMethodInType(context.getConcreteType(), newMethod, context);
      }
    }
  }
}
