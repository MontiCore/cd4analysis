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
import de.monticore.cd4codebasis._ast.ASTCDClass;

import java.util.stream.Collectors;

public class CD4CodeBasisPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil implements
    CD4CodeBasisVisitor2, CD4CodeBasisHandler {
  
  protected CD4CodeBasisTraverser traverser;
  
  public CD4CodeBasisPlantUMLPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }
  
  public CD4CodeBasisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil util) {
    super(util);
  }
  
  @Override
  public CD4CodeBasisTraverser getTraverser() { return traverser; }
  
  public void setTraverser(CD4CodeBasisTraverser traverser) { this.traverser = traverser; }
  
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
  
  @Override
  public void visit(de.monticore.cd4codebasis._ast.ASTCDClass node) {
    nameStack.push(node.getName());
    printComment(node);
    
    print("class " + node.getName());
    
    if (plantUMLConfig.getShowModifier() && hasModifier(node.getModifier())) {
      print(" << ");
      node.getModifier().accept(getTraverser());
      print(">>");
    }
    
    if (node.isPresentCDExtendUsage()) {
      print(" extends ");
      print(node.getSuperclassList().stream().map(s -> s.printType()).collect(Collectors.joining(
          ", ")));
    }
    if (node.isPresentCDInterfaceUsage()) {
      print(" implements ");
      print(node.getInterfaceList().stream().map(s -> s.printType()).collect(Collectors.joining(
          ", ")));
    }
    
    if (plantUMLConfig.getShowAtt() && !node.isEmptyCDMembers()) {
      println(" {");
      indent();
    }
  }
  
  @Override
  public void traverse(ASTCDClass node) {
    node.getCDMemberList().forEach(m -> m.accept(getTraverser()));
  }
  
  @Override
  public void endVisit(ASTCDClass node) {
    if (plantUMLConfig.getShowAtt() && !node.isEmptyCDMembers()) {
      unindent();
      println("}");
    }
    else {
      println();
    }
    
    nameStack.pop();
  }
  
}
