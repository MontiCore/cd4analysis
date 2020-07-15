/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.prettyprint;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;

public class CDInterfaceAndEnumPlantUMLPrettyPrinter
    extends PlantUMLPrettyPrintUtil
    implements CDInterfaceAndEnumVisitor {
  protected CDInterfaceAndEnumVisitor realThis;

  public CDInterfaceAndEnumPlantUMLPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CDInterfaceAndEnumPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil util) {
    super(util);
    setRealThis(this);
  }

  @Override
  public CDInterfaceAndEnumVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDInterfaceAndEnumVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTCDInterface node) {
    nameStack.push(node.getName());

    printComment(node, node.getName());

    println("interface " + node.getName());

    if (getPlantUMLConfig().getShowAtt() && !node.isEmptyCDMembers()) {
      println(" {");
      indent();
    }
  }

  @Override
  public void traverse(ASTCDInterface node) {
    node.getCDMemberList().forEach(m -> m.accept(getRealThis()));
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

    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getRealThis());
    }

    nameStack.pop();
  }

  @Override
  public void visit(ASTCDEnum node) {
    nameStack.push(node.getName());

    printComment(node, node.getName());

    println("enum " + node.getName());

    if (getPlantUMLConfig().getShowAtt() && !node.isEmptyCDEnumConstants()) {
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
      c.accept(getRealThis());
      println();
    });

    if (plantUMLConfig.getShowAtt() && !node.getCDMemberList().isEmpty()) {
      println("__ Attributes __");

      node.streamCDMembers().forEach(m -> {
        m.accept(getRealThis());
        println();
      });
    }
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    if (getPlantUMLConfig().getShowAtt() && !node.isEmptyCDEnumConstants()) {
      unindent();
      println("}");
    }
    else {
      println();
    }

    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getRealThis());
    }

    nameStack.pop();
  }

  @Override
  public void visit(ASTCDEnumConstant node) {
    print(node.getName());
  }
}
