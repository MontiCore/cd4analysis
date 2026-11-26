/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Warns if an association declares a visibility (public, protected, private) and one of its roles
 * declares a different visibility, e.g. {@code public association A -> B private;}. A role
 * visibility that matches the association visibility is accepted, as is a role visibility on an
 * association without one.
 * <p>
 * This CoCo does not require a symbol table and can therefore be checked before the symbol table
 * is created (and before trafos that add default visibilities).
 * <p>
 * Tools that transform the model before checking (e.g., the code generator propagating
 * association visibilities and adding default visibilities) should check the model as written
 * with the default variant ({@link #ERROR_CODE}), and the transformed model with the
 * {@code afterTrafos} variant ({@link #ERROR_CODE_AFTER_TRAFOS}). The latter makes clear that
 * the reported visibilities may stem from a transformation rather than from the model.
 */
public class CDAssociationVisibilityConflictCoCo implements CDAssociationASTCDAssociationCoCo {
  
  /** Conflict in the model as written */
  public static final String ERROR_CODE = "0xCDA69";
  
  /** Conflict in the model after transformations have been applied */
  public static final String ERROR_CODE_AFTER_TRAFOS = "0xCDA70";
  
  protected final boolean afterTrafos;
  
  /** Checks the model as written. */
  public CDAssociationVisibilityConflictCoCo() {
    this(false);
  }
  
  /**
   * @param afterTrafos whether the checked model has already been transformed (e.g., by
   * propagating association visibilities or adding default visibilities)
   */
  public CDAssociationVisibilityConflictCoCo(boolean afterTrafos) {
    this.afterTrafos = afterTrafos;
  }
  
  @Override
  public void check(ASTCDAssociation a) {
    Optional<String> assocVisibility = getVisibility(a.getModifier());
    if (assocVisibility.isEmpty()) {
      return;
    }
    checkSide(a, a.getLeft(), "left", assocVisibility.get());
    checkSide(a, a.getRight(), "right", assocVisibility.get());
  }
  
  protected void checkSide(ASTCDAssociation a, ASTCDAssocSide side, String sideName,
      String assocVisibility) {
    Optional<String> roleVisibility = getVisibility(side.getModifier());
    if (roleVisibility.isPresent() && !roleVisibility.get().equals(assocVisibility)) {
      String assocName = a.isPresentName() ? "'" + a.getName() + "'" : "between '" + a.getLeft()
          .getMCQualifiedType().printType() + "' and '" + a.getRight().getMCQualifiedType()
              .printType() + "'";
      String message = String.format(
          "The association %s is declared '%s', but its %s role '%s' is declared '%s'", assocName,
          assocVisibility, sideName, side.getName(), roleVisibility.get());
      if (afterTrafos) {
        message = String.format("%s: %s after applying model transformations (e.g., propagated "
            + "or default visibilities). If the model itself has no conflict, this is caused by a "
            + "transformation.", ERROR_CODE_AFTER_TRAFOS, message);
      }
      else {
        message = String.format("%s: %s. Either remove the visibility of the association or use "
            + "the same visibility for the association and its roles.", ERROR_CODE, message);
      }
      Log.warn(message, side.get_SourcePositionStart(), side.get_SourcePositionEnd());
    }
  }
  
  /**
   * @return the visibility declared by the modifier (public, protected or private), if any
   */
  protected Optional<String> getVisibility(ASTModifier modifier) {
    if (modifier == null) {
      return Optional.empty();
    }
    if (modifier.isPublic()) {
      return Optional.of("public");
    }
    if (modifier.isProtected()) {
      return Optional.of("protected");
    }
    if (modifier.isPrivate()) {
      return Optional.of("private");
    }
    return Optional.empty();
  }
  
}
