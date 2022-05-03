package de.monticore.ow2cw;

import de.monticore.cdbasis._ast.*;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;

public class OWAwareCDBuilder {
  protected ASTCDCompilationUnit originalCD;  // Genutzt, um stereotype zu lesen
  protected ASTCDCompilationUnitBuilder builder;  // TODO: nutz einen existierenden Builder

  /**
   *
   * @param originalCD Used for checking if adding/etc is allowed
   */
  public OWAwareCDBuilder(ASTCDCompilationUnit originalCD){
    this.originalCD = originalCD;
  }

  /**
   * @param newClass
   */
  public OWAwareCDBuilder addNewClass(ASTCDClass newClass){
    /*if (check stereotype if allowed to add class ){
      // add class
    }
     */
    return this;
  }

  /**
   * @param newClass
   */
  public OWAwareCDBuilder addNewSubClass(ASTCDClass newClass, ASTCDType superType){
    /*
      newClass.setExtends(...);
      addNewClass(newClass);
     */
    return this;
  }
  public OWAwareCDBuilder addMissingAttributes(ASTCDType cdType, Collection<ASTCDAttribute> missingAttributes){
    /*if (check stereotype if allowed to add attribute to class ){
      // add Attribute
    }
     */
    return this;
  }

  public ASTCDCompilationUnit build(){
    builder.build();
    // todo ... bauen
    // todo removeDuplicateAttributes
    throw new NotImplementedException();
  }
}
