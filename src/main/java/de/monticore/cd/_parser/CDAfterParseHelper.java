/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._parser;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.Stack;

public class CDAfterParseHelper {
  protected Stack<ASTCDAssociation> assocStack;
  protected Stack<ASTCDType> typeStack;
  protected Stack<ASTCDDefinition> packageStack; // TODO SVa: use new CDPackage instead?

  public CDAfterParseHelper() {
    this(new Stack<>(), new Stack<>(), new Stack<>());
  }

  public CDAfterParseHelper(CDAfterParseHelper cdAfterParseHelper) {
    this(cdAfterParseHelper.assocStack, cdAfterParseHelper.typeStack, cdAfterParseHelper.packageStack);
  }

  public CDAfterParseHelper(Stack<ASTCDAssociation> assocStack, Stack<ASTCDType> typeStack, Stack<ASTCDDefinition> packageStack) {
    this.assocStack = assocStack;
    this.typeStack = typeStack;
    this.packageStack = packageStack;
  }

  public Stack<ASTCDAssociation> getAssocStack() {
    return assocStack;
  }

  public void setAssocStack(Stack<ASTCDAssociation> assocStack) {
    this.assocStack = assocStack;
  }

  public Stack<ASTCDType> getTypeStack() {
    return typeStack;
  }

  public void setTypeStack(Stack<ASTCDType> typeStack) {
    this.typeStack = typeStack;
  }

  public Stack<ASTCDDefinition> getPackageStack() {
    return packageStack;
  }

  public void setPackageStack(Stack<ASTCDDefinition> packageStack) {
    this.packageStack = packageStack;
  }
}
