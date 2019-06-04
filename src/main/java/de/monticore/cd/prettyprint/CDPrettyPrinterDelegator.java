/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */


package de.monticore.cd.prettyprint;

import de.monticore.MCBasicLiteralsPrettyPrinter;
import de.monticore.cd.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisDelegatorVisitor;
import de.monticore.mcbasicliterals._ast.ASTMCBasicLiteralsNode;
import de.monticore.mcbasics._ast.ASTMCBasicsNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mccollectiontypes._ast.ASTMCCollectionTypesNode;
import de.monticore.types.mccollectiontypes._od.MCCollectionTypes2OD;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

public class CDPrettyPrinterDelegator extends CD4AnalysisDelegatorVisitor {

  protected CD4AnalysisDelegatorVisitor realThis = this;

  protected IndentPrinter printer = null;

  public CDPrettyPrinterDelegator() {
    this.printer = new IndentPrinter();
    realThis = this;
    setCD4AnalysisVisitor(new CDPrettyPrinter(printer));
    setMCBasicLiteralsVisitor(new MCBasicLiteralsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
  }

  public CDPrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
    setCD4AnalysisVisitor(new CDPrettyPrinter(printer));
    setMCBasicLiteralsVisitor(new MCBasicLiteralsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
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

  public String prettyprint(ASTMCBasicLiteralsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCCollectionTypesNode a) {
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
  public CD4AnalysisDelegatorVisitor getRealThis() {
    return realThis;
  }


}
