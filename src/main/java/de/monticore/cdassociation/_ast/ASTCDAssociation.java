/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._ast;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;

public class ASTCDAssociation extends ASTCDAssociationTOP {

  public String getPrintableName() {
    if (isPresentName()) {
      return getName();
    }

    return CDAssociationMill.cDAssociationPrettyPrinter().prettyprint(this);
  }

  public ASTMCQualifiedName getLeftQualifiedName() {
    return getLeft().getMCQualifiedType().getMCQualifiedName();
  }

  public List<String> getLeftReferenceName() {
    return getLeft().getMCQualifiedType().getNameList();
  }

  public ASTMCQualifiedName getRightQualifiedName() {
    return getRight().getMCQualifiedType().getMCQualifiedName();
  }

  public List<String> getRightReferenceName() {
    return getRight().getMCQualifiedType().getNameList();
  }

}