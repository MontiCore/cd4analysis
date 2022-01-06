/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.mcg;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd._visitor.CDMemberVisitor;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis.typescalculator.DeriveSymTypeOfCD4CodeBasis;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CD4CodeEnumConstantParameterMatchConstructorArguments
    implements CDInterfaceAndEnumASTCDEnumCoCo {

  final CDSymbolTableHelper helper;

  public CD4CodeEnumConstantParameterMatchConstructorArguments() {
    helper = new CDSymbolTableHelper(new DeriveSymTypeOfCD4CodeBasis());
  }

  @Override
  public void check(ASTCDEnum node) {
    final List<ASTCDEnumConstant> enumConstants = node.getCDEnumConstantList();
    final List<List<SymTypeExpression>> enumConstantsTypes = calculateEnumArgumentTypes(node);

    final List<ASTCDMethodSignature> availableConstructors = node
        .getCDMemberList(CDMemberVisitor.Options.METHOD_SIGNATURES);

    final List<List<ASTCDParameter>> constructorParameters = availableConstructors.stream()
        .filter(s -> s.getSymbol().isIsConstructor())
        .map(ASTCDMethodSignature::getCDParameterList)
        .collect(Collectors.toList());

    // if there is no constructor present, a default constructor with no arguments is available
    if (constructorParameters.isEmpty()) {
      constructorParameters.add(new ArrayList<>());
    }

    final List<List<SymTypeExpression>> constructors = calculateConstructorParameterTypes(node, constructorParameters);
    final List<Optional<SymTypeExpression>> ellipticTypes = IntStream.range(0, availableConstructors.size()).mapToObj(i -> {
      boolean constructorIsElliptic = availableConstructors.get(i).getSymbol().isIsElliptic();
      return constructorIsElliptic ? Optional.of(constructors.get(i).get(constructors.get(i).size() - 1)) : Optional.<SymTypeExpression>empty();
    }).collect(Collectors.toList());

    // if there is no constructor present, a default constructor with no arguments is available
    if (ellipticTypes.isEmpty()) {
      ellipticTypes.add(Optional.empty());
    }

    // iterate over all enum constants
    IntStream.range(0, enumConstants.size())
        // for each of the constant check each constructor
        .filter(e -> IntStream.range(0, constructors.size()).noneMatch(i -> {
              // if the size of the arguments differ, then the constructor has to have an elliptic type
              if (constructors.get(i).size() == enumConstantsTypes.get(e).size() || ellipticTypes.get(i).isPresent()) {
                // iterate each argument type
                return IntStream.range(0, enumConstantsTypes.get(e).size()).noneMatch(j -> {
                  // constructor has at least as much parameter
                  if (constructors.get(i).size() >= i) {
                    return TypeCheck.isSubtypeOf(enumConstantsTypes.get(e).get(j), constructors.get(i).get(j));
                  }
                  // there are more arguments than the constructor has parameter,
                  // check for the elliptic type
                  else {
                    return ellipticTypes.get(i).isPresent() && TypeCheck.isSubtypeOf(enumConstantsTypes.get(e).get(j), ellipticTypes.get(i).get());
                  }
                });
              }
              else {
                return false;
              }
            })
        ).forEach(e -> {
          ASTCDEnumConstant enumConstant = enumConstants.get(e);
          Log.error(
              String
                  .format(
                      "0xCDCD2: The enum constant %s uses %s which is incompatible with the available constructors of the enum %s [%s].",
                      enumConstants.get(e).getName(),
                      enumConstant instanceof ASTCD4CodeEnumConstant && ((ASTCD4CodeEnumConstant) enumConstant).isPresentArguments()
                          ? "the constructor " + node.getName() + helper.getPrettyPrinter().prettyprint(((ASTCD4CodeEnumConstant) enumConstant).getArguments())
                          : "the empty constructor",
                      node.getName(),
                      printAvailableConstructor(node, constructorParameters)),
              enumConstant.get_SourcePositionStart()
          );
        }
    );
  }

  public List<List<SymTypeExpression>> calculateEnumArgumentTypes(ASTCDEnum node) {
    return node.streamCDEnumConstants().map(enumConstant -> {
      if (!(enumConstant instanceof ASTCD4CodeEnumConstant)) {
        return new ArrayList<SymTypeExpression>();
      }
      final ASTCD4CodeEnumConstant codeEnumConstant = (ASTCD4CodeEnumConstant) enumConstant;
      if (!codeEnumConstant.isPresentArguments()) {
        return new ArrayList<SymTypeExpression>();
      }
      final ASTArguments arguments = codeEnumConstant.getArguments();
      if (arguments.sizeExpressions() == 0) {
        return new ArrayList<SymTypeExpression>();
      }

      // find type of each of the arguments
      final List<Optional<SymTypeExpression>> argumentTypes = arguments
          .streamExpressions()
          .map(e -> helper.getTypeChecker().calculateType(e))
          .collect(Collectors.toList());
      IntStream.range(0, argumentTypes.size()).filter(i -> !argumentTypes.get(i).isPresent()).forEach(i ->
          Log.error(
              String
                  .format(
                      "0xCDCD0: The type of the argument %s (index %d) can not be calculated.",
                      helper.getPrettyPrinter().prettyprint(arguments.getExpression(i)),
                      i),
              arguments.getExpression(i).get_SourcePositionStart())
      );

      return argumentTypes.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }).collect(Collectors.toList());
  }

  public List<List<SymTypeExpression>> calculateConstructorParameterTypes
      (ASTCDEnum node, List<List<ASTCDParameter>> constructorParameters) {
    return constructorParameters.stream().map(parameter -> {
      if (parameter.size() == 0) {
        return new ArrayList<SymTypeExpression>();
      }

      // find type of each of the parameter
      final List<Optional<SymTypeExpression>> parameterTypes = parameter
          .stream()
          .map(e -> helper.getTypeChecker().calculateType(e.getMCType()))
          .collect(Collectors.toList());
      IntStream.range(0, parameterTypes.size()).filter(i -> !parameterTypes.get(i).isPresent()).forEach(i ->
          Log.error(
              String
                  .format(
                      "0xCDCD1: The type of the parameter %s (index %d) of the enum (%s) constructor can not be calculated.",
                      parameter.get(i).getName(),
                      i, node.getName()),
              node.get_SourcePositionStart()
          )
      );

      return parameterTypes.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }).collect(Collectors.toList());
  }

  public String printAvailableConstructor(ASTCDEnum node, List<List<ASTCDParameter>> constructorParameters) {
    return constructorParameters.stream()
        .map(c -> node.getName() + "(" +
            c.stream()
                .map(p -> new CD4CodeFullPrettyPrinter().prettyprint(p))
                .collect(Collectors.joining(", ")) + ")")
        .collect(Collectors.joining("; "));
  }
}
