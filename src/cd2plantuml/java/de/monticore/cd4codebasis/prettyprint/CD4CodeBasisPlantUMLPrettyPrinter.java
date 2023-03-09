/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisHandler;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;

public class CD4CodeBasisPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil
    implements CD4CodeBasisVisitor2, CD4CodeBasisHandler {

  protected CD4CodeBasisTraverser traverser;

  public CD4CodeBasisPlantUMLPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CD4CodeBasisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil util) {
    super(util);
  }

  @Override
  public CD4CodeBasisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTCDMethod node) {
    if (plantUMLConfig.getShowAtt()) {
      print("{method} "); // be sure that this is handled as a field

      if (plantUMLConfig.getShowModifier()) {
        node.getModifier().accept(getTraverser());
      }

      node.getMCReturnType().accept(getTraverser());
      print(" " + node.getName() + "(");
      printSeparatorCD4CodeBasis(getTraverser(), node.getCDParameterList().iterator(), ", ");
      print(")");
      if (node.isPresentCDThrowsDeclaration()) {
        print(" ");
        node.getCDThrowsDeclaration().accept(getTraverser());
      }
      println();
    }
  }

  @Override
  public void traverse(ASTCDConstructor node) {
    if (plantUMLConfig.getShowAtt()) {
      print("{method} "); // be sure that this is handled as a field

      if (plantUMLConfig.getShowModifier()) {
        node.getModifier().accept(getTraverser());
      }

      print(node.getName() + "(");
      printSeparatorCD4CodeBasis(getTraverser(), node.getCDParameterList().iterator(), ", ");
      print(")");
      if (node.isPresentCDThrowsDeclaration()) {
        print(" ");
        node.getCDThrowsDeclaration().accept(getTraverser());
      }
      println();
    }
  }

  @Override
  public void traverse(ASTCDParameter node) {
    node.getAnnotation().ifPresent(annotation -> print(annotation + " "));
    node.getMCType().accept(getTraverser());
    if (node.isEllipsis()) {
      print("...");
    }
    print(" " + node.getName());
  }

  @Override
  public void visit(ASTCD4CodeEnumConstant node) {
    print(node.getName());
  }
}
