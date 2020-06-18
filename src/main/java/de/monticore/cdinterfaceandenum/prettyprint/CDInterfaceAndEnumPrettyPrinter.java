/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

public class CDInterfaceAndEnumPrettyPrinter extends PrettyPrintUtil
    implements CDInterfaceAndEnumVisitor {
  protected IndentPrinter printer;
  protected CDInterfaceAndEnumVisitor realThis;

  public CDInterfaceAndEnumPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDInterfaceAndEnumPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
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
    printPreComments(node.iterator_PreComments());

    node.getModifier().accept(getRealThis());
    getPrinter().print("interface " + node.getName());
    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getRealThis());
    }

    if (!node.isEmptyCDMembers()) {
      getPrinter().print(" {");
      printPostComments(node.iterator_PostComments());
      getPrinter().println();
      getPrinter().indent();
    }
  }

  @Override
  public void traverse(ASTCDInterface node) {
    node.getCDMemberList().forEach(m -> m.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    if (!node.isEmptyCDMembers()) {
      getPrinter().unindent();
      getPrinter().println("}");
    }
    else {
      getPrinter().print(";");
      printPostComments(node.iterator_PostComments());
      getPrinter().println();
    }
  }

  @Override
  public void visit(ASTCDEnum node) {
    printPreComments(node.iterator_PreComments());

    node.getModifier().accept(getRealThis());
    getPrinter().print("enum " + node.getName());
    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getRealThis());
    }

    if (!node.isEmptyCDMembers()) {
      getPrinter().print(" {");
      printPostComments(node.iterator_PostComments());
      getPrinter().indent();
    }
  }

  @Override
  public void traverse(ASTCDEnum node) {
    final Iterator<ASTCDEnumConstant> iterator = node.getCDEnumConstantList().iterator();

    if (!iterator.hasNext()) {
      getPrinter().println(";");
    }
    while (iterator.hasNext()) {
      final ASTCDEnumConstant constant = iterator.next();
      printPreComments(constant.get_PreCommentList().iterator());
      getPrinter().print(constant.getName());
      if (iterator.hasNext()) {
        getPrinter().print(",");
      }
      else {
        getPrinter().print(";");
      }
      printPostComments(constant.get_PostCommentList().iterator());
      getPrinter().println();
    }

    node.getCDMemberList().forEach(m -> m.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    if (!node.isEmptyCDMembers()) {
      getPrinter().unindent();
      getPrinter().println("}");
    }
    else {
      getPrinter().print(";");
      printPostComments(node.iterator_PostComments());
      getPrinter().println();
    }
  }
}
