/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.prettyprint;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumHandler;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumTraverser;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;

public class CDInterfaceAndEnumPlantUMLPrettyPrinter
    extends PlantUMLPrettyPrintUtil
    implements CDInterfaceAndEnumVisitor2, CDInterfaceAndEnumHandler {
  protected CDInterfaceAndEnumTraverser traverser;

  public CDInterfaceAndEnumPlantUMLPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CDInterfaceAndEnumPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil util) {
    super(util);
  }

  @Override
  public CDInterfaceAndEnumTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CDInterfaceAndEnumTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void visit(ASTCDInterface node) {
    nameStack.push(node.getName());

    printComment(node, node.getName());

    print("interface " + node.getName());

    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getTraverser());
    }

    if (getPlantUMLConfig().getShowAtt() && !node.isEmptyCDMembers()) {
      println(" {");
      indent();
    }
  }

  @Override
  public void traverse(ASTCDInterface node) {
    node.getCDMemberList().forEach(m -> m.accept(getTraverser()));
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    if (getPlantUMLConfig().getShowAtt() && !node.isEmptyCDMembers()) {
      unindent();
      println("}");
    }
    else {
      println();
    }

    nameStack.pop();
  }

  @Override
  public void visit(ASTCDEnum node) {
    nameStack.push(node.getName());

    printComment(node, node.getName());

    print("enum " + node.getName());

    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getTraverser());
    }

    if (getPlantUMLConfig().getShowAtt() || !node.isEmptyCDEnumConstants()) {
      println(" {");
      indent();
    }
  }

  @Override
  public void traverse(ASTCDEnum node) {
    if (!node.getCDEnumConstantList().isEmpty()) {
      println("__ Enum Constants __");
    }
    node.streamCDEnumConstants().forEach(c -> {
      c.accept(getTraverser());
      println();
    });

    if (plantUMLConfig.getShowAtt() && !node.getCDMemberList().isEmpty()) {
      println("__ Attributes __");

      node.streamCDMembers().forEach(m -> {
        m.accept(getTraverser());
        println();
      });
    }
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    if (getPlantUMLConfig().getShowAtt() || !node.isEmptyCDEnumConstants()) {
      unindent();
      println("}");
    }
    else {
      println();
    }

    nameStack.pop();
  }

  @Override
  public void visit(ASTCDEnumConstant node) {
    print(node.getName());
  }
}
