/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import com.google.common.base.Joiner;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.NoSuchElementException;

/**
 * Checks that the types connected by associations exist.
 */
public class CDAssociationSrcAndTargetTypeExistChecker implements
    CDAssociationASTCDAssociationCoCo {

  protected final CDAssociationPrettyPrinter prettyPrinter = new CDAssociationPrettyPrinter();

  @Override
  public void check(ASTCDAssociation assoc) {
    checkTypeExists(assoc.getLeft(), assoc);
    checkTypeExists(assoc.getRight(), assoc);
  }

  private void checkTypeExists(ASTCDAssocSide side, ASTCDAssociation assoc) {
    if (!side.isPresentSymbol()) {
      // no symbol present
      return;
    }

    try {
      //noinspection ResultOfMethodCallIgnored
      side.getSymbol().getType().getTypeInfo();
    }
    catch (NoSuchElementException | IllegalStateException e) {
      Log.error(
          String
              .format(
                  "0xCDC6A: Type %s of %s is unknown. (%s)",
                  MCBasicTypesMill.mCQualifiedNameBuilder().setPartsList(side.getMCQualifiedType().getNameList()).build().getQName(), prettyPrinter.prettyprint(assoc),
                  Joiner.on("\n").join(e.getStackTrace())),
          assoc.get_SourcePositionStart());
    }
  }
}
