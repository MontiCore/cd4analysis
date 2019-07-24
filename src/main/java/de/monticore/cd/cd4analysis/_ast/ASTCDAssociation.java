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

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.CDPrettyPrinterConcreteVisitor;
import de.monticore.cd.symboltable.CDAssociationSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociationTOP;

import java.util.Optional;

public class ASTCDAssociation extends ASTCDAssociationTOP {

  private CDAssociationSymbol leftToRightSymbol;
  private CDAssociationSymbol rightToLeftSymbol;

  public ASTCDAssociation() {
  }

  @Override
  public String toString() {
    IndentPrinter ip = new IndentPrinter();
    this.accept(new CDPrettyPrinterConcreteVisitor(ip));
    return ip.getContent().replace("\r","").replace("\n", "");
  }

  protected  ASTCDAssociation (Optional<de.monticore.cd.cd4analysis._ast.ASTCDStereotype> stereotype,
                               Optional<String> name,
                               Optional<de.monticore.cd.cd4analysis._ast.ASTModifier> leftModifier,
                               Optional<de.monticore.cd.cd4analysis._ast.ASTCardinality> leftCardinality,
                               ASTMCQualifiedName leftReferenceName,
                               Optional<de.monticore.cd.cd4analysis._ast.ASTCDQualifier> leftQualifier,
                               Optional<String> leftRole,
                               Optional<String> rightRole,
                               Optional<de.monticore.cd.cd4analysis._ast.ASTCDQualifier> rightQualifier,
                               ASTMCQualifiedName rightReferenceName,
                               Optional<de.monticore.cd.cd4analysis._ast.ASTCardinality> rightCardinality,
                               Optional<de.monticore.cd.cd4analysis._ast.ASTModifier> rightModifier,
                               boolean r__association,
                               boolean r__composition,
                               boolean r__derived,
                               boolean leftToRight,
                               boolean rightToLeft,
                               boolean bidirectional,
                               boolean unspecified)  {
    super(stereotype, Optional.empty(), name, leftModifier, leftCardinality, leftReferenceName, leftQualifier, leftRole, rightRole, rightQualifier, rightReferenceName, rightCardinality, rightModifier, r__composition, r__association, r__derived, unspecified, bidirectional, rightToLeft, leftToRight);
  }

  protected  ASTCDAssociation (Optional<de.monticore.cd.cd4analysis._ast.ASTCDStereotype> stereotype,
     Optional<String> readOnly,
     Optional<String> name,
     Optional<de.monticore.cd.cd4analysis._ast.ASTModifier> leftModifier,
     Optional<de.monticore.cd.cd4analysis._ast.ASTCardinality> leftCardinality,
     ASTMCQualifiedName leftReferenceName,
     Optional<de.monticore.cd.cd4analysis._ast.ASTCDQualifier> leftQualifier,
     Optional<String> leftRole,
     Optional<String> rightRole,
     Optional<de.monticore.cd.cd4analysis._ast.ASTCDQualifier> rightQualifier,
     ASTMCQualifiedName rightReferenceName,
     Optional<de.monticore.cd.cd4analysis._ast.ASTCardinality> rightCardinality,
     Optional<de.monticore.cd.cd4analysis._ast.ASTModifier> rightModifier,
     boolean r__association,
     boolean r__composition,
     boolean r__derived,
     boolean leftToRight,
     boolean rightToLeft,
     boolean bidirectional,
     boolean unspecified)  {
    super(stereotype, readOnly, name, leftModifier, leftCardinality, leftReferenceName, leftQualifier, leftRole, rightRole, rightQualifier, rightReferenceName, rightCardinality, rightModifier, r__composition, r__association, r__derived, unspecified, bidirectional, rightToLeft, leftToRight);
  }

  public Optional<CDAssociationSymbol> getLeftToRightSymbol() {
    return Optional.ofNullable(leftToRightSymbol);
  }

  public void setLeftToRightSymbol(CDAssociationSymbol leftToRightSymbol) {
    this.leftToRightSymbol = leftToRightSymbol;
  }

  public Optional<CDAssociationSymbol> getRightToLeftSymbol() {
    return Optional.ofNullable(rightToLeftSymbol);
  }

  public void setRightToLeftSymbol(CDAssociationSymbol rightToLeftSymbol) {
    this.rightToLeftSymbol = rightToLeftSymbol;
  }

  public boolean isReadOnly() {
    return getReadOnlyOpt().isPresent();
  }

  public void setReadOnly(boolean isReadOnly) {
    if (isReadOnly) {
      this.setReadOnly("read-only");
    }
    else {
      this.setReadOnly((String)null);
    }
  }
}