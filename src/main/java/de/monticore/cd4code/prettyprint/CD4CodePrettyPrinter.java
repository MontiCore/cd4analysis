/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPrettyPrinter;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPrettyPrinter;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

public class CD4CodePrettyPrinter extends CD4CodeDelegatorVisitor
    implements ExpressionsBasisVisitor {
  protected IndentPrinter printer;

  public CD4CodePrettyPrinter() {
    this(new IndentPrinter());
  }

  public CD4CodePrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    setRealThis(this);
    setCDBasisVisitor(new CDBasisPrettyPrinter(printer));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumPrettyPrinter(printer));
    setCDAssociationVisitor(new CDAssociationPrettyPrinter(printer));
    setCD4CodeBasisVisitor(new CD4CodeBasisPrettyPrinter(printer));
    setCD4CodeVisitor(this);

    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setUMLStereotypeVisitor(new UMLStereotypePrettyPrinter(printer));
    setUMLModifierVisitor(new UMLModifierPrettyPrinter(printer));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    setBitExpressionsVisitor(new BitExpressionsPrettyPrinter(printer));
    setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public String prettyprint(ASTCDCompilationUnit node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
}
