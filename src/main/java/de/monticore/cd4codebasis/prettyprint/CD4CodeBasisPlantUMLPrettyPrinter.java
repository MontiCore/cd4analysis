/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeBasisPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil
    implements CD4CodeBasisVisitor {
  protected CD4CodeBasisVisitor realThis;

  public CD4CodeBasisPlantUMLPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CD4CodeBasisPlantUMLPrettyPrinter(IndentPrinter printer) {
    super(printer);
    setRealThis(this);
  }

  @Override
  public CD4CodeBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4CodeBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void traverse(ASTCDMethod node) {
    node.getModifier().accept(getRealThis());

    node.getMCReturnType().accept(getRealThis());
    print(" " + node.getName() + "(");
    printSeparatorCD4CodeBasis(getRealThis(), node.getCDParameterList().iterator(), ", ");
    print(")");
    if (node.isPresentCDThrowsDeclaration()) {
      print(" ");
      node.getCDThrowsDeclaration().accept(getRealThis());
    }
    println();
  }

  @Override
  public void traverse(ASTCDConstructor node) {
    node.getModifier().accept(getRealThis());

    print(node.getName() + "(");
    printSeparatorCD4CodeBasis(getRealThis(), node.getCDParameterList().iterator(), ", ");
    print(")");
    if (node.isPresentCDThrowsDeclaration()) {
      print(" ");
      node.getCDThrowsDeclaration().accept(getRealThis());
    }
    println();
  }

  @Override
  public void traverse(ASTCDParameter node) {
    node.getMCType().accept(getRealThis());
    if (node.isEllipsis()) {
      print("...");
    }
    print(" " + node.getName());
  }
}
