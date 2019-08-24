/* (c) https://github.com/MontiCore/monticore */


package de.monticore.cd.cd4code;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.cd.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd.prettyprint.CDPrettyPrinter;
import de.monticore.literals.mccommonliterals._ast.ASTMCCommonLiteralsNode;
import de.monticore.mcbasics._ast.ASTMCBasicsNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mccollectiontypes._ast.ASTMCCollectionTypesNode;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCSimpleGenericTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

public class CD4CodePrettyPrinterDelegator extends CD4CodeDelegatorVisitor {

  protected CD4CodeDelegatorVisitor realThis = this;

  protected IndentPrinter printer = null;

  public CD4CodePrettyPrinterDelegator() {
    this.printer = new IndentPrinter();
    realThis = this;
    setCD4AnalysisVisitor(new CDPrettyPrinter(printer));
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
    setMCSimpleGenericTypesVisitor(new MCSimpleGenericTypesPrettyPrinter(printer));
    setMCFullGenericTypesVisitor(new MCFullGenericTypesPrettyPrinter(printer));
  }

  public CD4CodePrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
    setCD4AnalysisVisitor(new CDPrettyPrinter(printer));
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
    setMCSimpleGenericTypesVisitor(new MCSimpleGenericTypesPrettyPrinter(printer));
    setMCFullGenericTypesVisitor(new MCFullGenericTypesPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTCD4AnalysisNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBasicsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCCommonLiteralsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCCollectionTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCSimpleGenericTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBasicTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public CD4CodeDelegatorVisitor getRealThis() {
    return realThis;
  }


}
