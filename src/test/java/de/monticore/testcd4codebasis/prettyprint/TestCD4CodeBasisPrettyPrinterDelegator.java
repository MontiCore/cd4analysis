/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcd4codebasis.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPrettyPrinter;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.testcd4codebasis._visitor.TestCD4CodeBasisDelegatorVisitor;
import de.monticore.testcdbasis._visitor.TestCDBasisDelegatorVisitor;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

public class TestCD4CodeBasisPrettyPrinterDelegator
    extends TestCD4CodeBasisDelegatorVisitor {
  protected IndentPrinter printer;

  public TestCD4CodeBasisPrettyPrinterDelegator() {
    this(new IndentPrinter());
  }

  public TestCD4CodeBasisPrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    setRealThis(this);
    setCDBasisVisitor(new CDBasisPrettyPrinter(printer));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumPrettyPrinter(printer));
    setCD4CodeBasisVisitor(new CD4CodeBasisPrettyPrinter(printer));

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
