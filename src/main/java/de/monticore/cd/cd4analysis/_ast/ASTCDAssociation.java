/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.CDPrettyPrinter;
import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.Optional;

public class ASTCDAssociation extends ASTCDAssociationTOP {

  private CDAssociationSymbol leftToRightSymbol;
  private CDAssociationSymbol rightToLeftSymbol;

  public ASTCDAssociation() {
  }

  @Override
  public String toString() {
    IndentPrinter ip = new IndentPrinter();
    this.accept(new CDPrettyPrinter(ip));
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
    super(stereotype, Optional.empty(), name, leftModifier, leftCardinality, leftReferenceName, leftQualifier, leftRole, rightRole, rightQualifier, rightReferenceName, rightCardinality, rightModifier, r__association, r__composition, r__derived, leftToRight, rightToLeft, bidirectional, unspecified);
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
    super(stereotype, readOnly, name, leftModifier, leftCardinality, leftReferenceName, leftQualifier, leftRole, rightRole, rightQualifier, rightReferenceName, rightCardinality, rightModifier, r__association, r__composition, r__derived, leftToRight, rightToLeft, bidirectional, unspecified);
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

  public String getName() {
   return getNameOpt().orElse("");
  }
}
