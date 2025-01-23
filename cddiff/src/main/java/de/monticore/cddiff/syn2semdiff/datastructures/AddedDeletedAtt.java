package de.monticore.cddiff.syn2semdiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import java.util.ArrayList;
import java.util.List;

public class AddedDeletedAtt {
  private List<ASTCDAttribute> addedAttributes = new ArrayList<>();
  private List<ASTCDAttribute> deletedAttributes = new ArrayList<>();

  public AddedDeletedAtt() {}

  public AddedDeletedAtt(
      List<ASTCDAttribute> addedAttributes, List<ASTCDAttribute> deletedAttributes) {
    this.addedAttributes = addedAttributes;
    this.deletedAttributes = deletedAttributes;
  }

  public void updateAdded(ASTCDAttribute attribute) {
    addedAttributes.add(attribute);
  }

  public void updateDeleted(ASTCDAttribute attribute) {
    deletedAttributes.add(attribute);
  }

  public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

  public List<ASTCDAttribute> getDeletedAttributes() {
    return deletedAttributes;
  }
}
