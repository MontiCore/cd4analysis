/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._ast.ASTCDThrowsDeclaration;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.typeparameters._ast.ASTTypeParameters;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.util.Collection;
import java.util.List;

/** AST specific helper to print AST nodes. */
public class CdUtilsPrinter {

  public static final String EMPTY_STRING = "";

  /**
   * Print the string of a ASTModifier type, e.g. abstract private final static
   *
   * @param modifier the ASTModifier object
   * @return a string, e.g. abstract private final static
   */
  public String printModifier(ASTModifier modifier) {
    return CD4CodeMill.prettyPrint(modifier, false);
  }

  public String printSimpleModifier(ASTModifier modifier) {
    StringBuilder modifierStr = new StringBuilder();
    if (modifier.isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.isPublic()) {
      modifierStr.append(" public ");
    } else if (modifier.isPrivate()) {
      modifierStr.append(" private ");
    } else if (modifier.isProtected()) {
      modifierStr.append(" protected ");
    }
    if (modifier.isFinal()) {
      modifierStr.append(" final ");
    }
    if (modifier.isStatic()) {
      modifierStr.append(" static ");
    }

    return modifierStr.toString();
  }

  public String printTypeParameters(ASTTypeParameters ast) {
    return CD4CodeMill.prettyPrint(ast, false);
  }

  /**
   * Prints the fully qualified name of an {@code ASTCDPackage}
   *
   * @param astPackage the package whose name to print
   * @return a dot-separated package name
   */
  public String printPackageName(ASTCDPackage astPackage) {
    return String.join(".", astPackage.getMCQualifiedName().getQName());
  }

  /**
   * Converts a list of import statements to a string list.
   *
   * @param importStatements the list of import statements
   * @return a string list of all import statements
   */
  public String printImportList(Collection<ASTMCImportStatement> importStatements) {
    CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    StringBuilder sb = new StringBuilder();
    importStatements.forEach(i -> sb.append(printer.prettyprint(i)).append("\n"));
    return sb.toString();
  }

  /**
   * Converts a list of enum constants to a string list of enum constants
   *
   * @param enumConstants list of enum constants
   * @return a string list of enum constants
   */
  public String printEnumConstants(List<ASTCDEnumConstant> enumConstants) {
    checkNotNull(enumConstants);
    return Joiner.on(",").join(Collections2.transform(enumConstants, ASTCDEnumConstant::getName));
  }

  /**
   * Prints an ASTType
   *
   * @param type an ASTType
   * @return String representation of the ASTType
   */
  public String printType(ASTMCType type) {
    return new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(type);
  }

  public String printType(ASTMCReturnType type) {
    return new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(type);
  }

  /**
   * Prints the parameter declarations that can be used in methods, constructors, etc.
   *
   * @param parameterList a list of all parameters
   * @return a string list of parameter declarations, e.g. type name
   */
  public String printCDParametersDecl(List<ASTCDParameter> parameterList) {
    checkNotNull(parameterList);
    return Joiner.on(",")
        .join(
            Collections2.transform(
                parameterList,
                arg0 ->
                    arg0.getAnnotation().map(a -> a + " ").orElse("")
                        + printType(arg0.getMCType())
                        + " "
                        + arg0.getName()));
  }

  /**
   * Prints the throws declaration for methods, constructors, etc.
   *
   * @param throwsDecl a list of all qualified exceptions
   * @return a string list of all exceptions
   */
  public String printThrowsDecl(ASTCDThrowsDeclaration throwsDecl) {
    return "throws "
        + Joiner.on(",")
            .join(
                Collections2.transform(
                    throwsDecl.getExceptionList(),
                    arg0 -> Joiner.on(".").join(arg0.getPartsList())));
  }

  /**
   * Prints a list of extends declarations.
   *
   * @param extendsList a list of extends declarations
   * @return a string list of all extends declarations
   */
  public String printObjectTypeList(List<ASTMCObjectType> extendsList) {
    checkNotNull(extendsList);
    return Joiner.on(",").join(Collections2.transform(extendsList, this::printType));
  }

  public String printExpression(ASTExpression expr) {
    return new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(expr);
  }
}
