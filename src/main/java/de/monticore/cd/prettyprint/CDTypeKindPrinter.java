/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.prettyprint;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;

public class CDTypeKindPrinter extends PrettyPrintUtil
    implements CDInterfaceAndEnumVisitor {
  protected IndentPrinter printer;

  public CDTypeKindPrinter() {
    this(new IndentPrinter());
  }

  public CDTypeKindPrinter(IndentPrinter printer) {
    this.printer = printer;
    setRealThis(this);
  }

  @Override
  public CDTypeKindPrinter getRealThis() {
    return this;
  }

  @Override
  public void visit(ASTCDClass node) {
    print("class");
  }

  @Override
  public void visit(ASTCDInterface node) {
    print("interface");
  }

  @Override
  public void visit(ASTCDEnum node) {
    print("enum");
  }

  public String print(ASTCDType type) {
    type.accept(getRealThis());
    return getRealThis().getPrinter().getContent();
  }

  public String print(OOTypeSymbol type) {
    if (type.isIsClass()) {
      print("class");
    }
    if (type.isIsInterface()) {
      print("interface");
    }
    if (type.isIsEnum()) {
      print("enum");
    }

    return getRealThis().getPrinter().getContent();
  }
}
