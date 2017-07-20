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

package de.monticore.umlcd4a.cd4analysis._ast;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

import java.util.Optional;

public class ASTCDAssociation extends ASTCDAssociationTOP {

  private CDAssociationSymbol leftToRightSymbol;
  private CDAssociationSymbol rightToLeftSymbol;

  public ASTCDAssociation() {
  }

  public ASTCDAssociation(ASTStereotype stereotype, String name, ASTModifier leftModifier, ASTCardinality leftCardinality, ASTQualifiedName leftReferenceName, ASTCDQualifier leftQualifier, String leftRole, String rightRole, ASTCDQualifier rightQualifier, ASTQualifiedName rightReferenceName, ASTCardinality rightCardinality, ASTModifier rightModifier, boolean r__composition, boolean r__association, boolean r__derived, boolean unspecified, boolean bidirectional, boolean rightToLeft, boolean leftToRight) {
    super(stereotype, name, leftModifier, leftCardinality, leftReferenceName, leftQualifier, leftRole, rightRole, rightQualifier, rightReferenceName, rightCardinality, rightModifier, r__composition, r__association, r__derived, unspecified, bidirectional, rightToLeft, leftToRight);
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
}
