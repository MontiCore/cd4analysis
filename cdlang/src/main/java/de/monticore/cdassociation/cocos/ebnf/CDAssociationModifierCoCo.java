/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

public class CDAssociationModifierCoCo implements CDAssociationASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {

    //----------------------------------check the visibility modifiers-----------------------------------------------------------
    //main modifier
    boolean hasMainVisibility=false;
    if(a.getModifier()!=null) {
      ASTModifier mainModifier = a.getModifier();
      hasMainVisibility= mainModifier.isPublic()||mainModifier.isProtected()||mainModifier.isPrivate();
    }

    //left modifier
    boolean hasLeftVisibility=false;
    if(a.getLeft().getModifier()!=null) {
      ASTModifier leftModifier = a.getLeft().getModifier();
      hasLeftVisibility=leftModifier.isPublic()||leftModifier.isProtected()||leftModifier.isPrivate();
    }

    //right modifier
    boolean hasRightVisibility=false;
    if(a.getRight().getModifier()!=null) {
      ASTModifier rightModifier = a.getRight().getModifier();
      hasRightVisibility=rightModifier.isPublic()||rightModifier.isProtected()||rightModifier.isPrivate();
    }

    if(hasMainVisibility && (hasLeftVisibility || hasRightVisibility)) {
      Log.warn("Association cannot combine a main visibility modifier with individual role visibility modifiers.", a.get_SourcePositionStart());
    }

    //check whether the association or symbolRole use the static modifier
    boolean mainStatic = a.getModifier() != null && a.getModifier().isStatic();
    boolean leftStatic = a.getLeft().getModifier() != null && a.getLeft().getModifier().isStatic();
    boolean rightStatic = a.getRight().getModifier() != null && a.getRight().getModifier().isStatic();

    if(mainStatic || leftStatic || rightStatic) {
      String assocName = a.isPresentName() ? a.getName() : "unnamed";
      Log.error(String.format("0xCDA61: The association '%s' must not use static modifiers.", assocName),
        a.get_SourcePositionStart());
    }
  }
}
