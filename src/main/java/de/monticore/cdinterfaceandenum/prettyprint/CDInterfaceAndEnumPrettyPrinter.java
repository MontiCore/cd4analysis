/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumHandler;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumTraverser;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import java.util.Iterator;

public class CDInterfaceAndEnumPrettyPrinter extends PrettyPrintUtil
    implements CDInterfaceAndEnumVisitor2, CDInterfaceAndEnumHandler {
  protected CDInterfaceAndEnumTraverser traverser;

  public CDInterfaceAndEnumPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDInterfaceAndEnumPrettyPrinter(IndentPrinter printer) {
    super(printer);
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
    printPreComments(node);

    node.getModifier().accept(getTraverser());
    print("interface " + node.getName());
    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getTraverser());
    }

    if (!node.isEmptyCDMembers()) {
      print(" {");
      printPostComments(node);
      println();
      indent();
    }
  }

  @Override
  public void traverse(ASTCDInterface node) {
    node.getCDMemberList().forEach(m -> m.accept(getTraverser()));
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    if (!node.isEmptyCDMembers()) {
      unindent();
      println("}");
    } else {
      print(";");
      printPostComments(node);
      println();
    }
  }

  @Override
  public void visit(ASTCDEnum node) {
    printPreComments(node);

    node.getModifier().accept(getTraverser());
    print("enum " + node.getName());
    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getTraverser());
    }

    if (!node.isEmptyCDMembers() || !node.isEmptyCDEnumConstants()) {
      print(" {");
      printPostComments(node);
      println();
      indent();
    }
  }

  @Override
  public void traverse(ASTCDEnum node) {
    final Iterator<ASTCDEnumConstant> iterator = node.getCDEnumConstantList().iterator();

    if (!iterator.hasNext()) {
      println(";");
    }
    while (iterator.hasNext()) {
      printPreComments(node);
      iterator.next().accept(getTraverser());
      if (iterator.hasNext()) {
        print(",");
      } else {
        print(";");
      }
      printPostComments(node);
      println();
    }

    node.getCDMemberList().forEach(m -> m.accept(getTraverser()));
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    if (!node.isEmptyCDMembers() || !node.isEmptyCDEnumConstants()) {
      unindent();
      println("}");
    } else {
      print(";");
      printPostComments(node);
      println();
    }
  }

  @Override
  public void visit(ASTCDEnumConstant node) {
    print(node.getName());
  }
}
