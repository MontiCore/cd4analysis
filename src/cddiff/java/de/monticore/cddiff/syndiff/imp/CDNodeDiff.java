package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import de.monticore.cdassociation._ast.ASTCDAssocDir;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cddiff.syndiff.interfaces.ICDNodeDiff;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.util.Optional;

/** TODO: Write Comments */
public class CDNodeDiff<SrcType extends ASTNode, TgtType extends ASTNode>
    implements ICDNodeDiff<SrcType, TgtType> {
  protected DiffTypes difference;

  protected Actions action;

  protected final Optional<SrcType> srcValue;

  protected final Optional<TgtType> tgtValue;

  @Override
  public boolean isPresent() {
    return getAction().isPresent();
  }

  @Override
  public Optional<DiffTypes> getDiff() {
    if (difference == null) {
      return Optional.empty();
    } else {
      return Optional.of(difference);
    }
  }

  @Override
  public Optional<Actions> getAction() {
    if (action == null) {
      return Optional.empty();
    } else {
      return Optional.of(action);
    }
  }

  @Override
  public Optional<SrcType> getSrcValue() {
    return srcValue;
  }

  @Override
  public Optional<TgtType> getTgtValue() {
    return tgtValue;
  }

  public CDNodeDiff(Optional<SrcType> srcValue, Optional<TgtType> tgtValue) {
    this.srcValue = srcValue;
    this.tgtValue = tgtValue;
    this.action = findAction();
    findDiffType();
  }

  public CDNodeDiff(Actions action, Optional<SrcType> srcValue, Optional<TgtType> tgtValue) {
    this.srcValue = srcValue;
    this.tgtValue = tgtValue;
    this.action = action;
  }

  protected Actions findAction() {
    if (srcValue.isPresent()
        && tgtValue.isPresent()
        && !srcValue.get().deepEquals(tgtValue.get())) {
      return Actions.CHANGED;

    } else if (srcValue.isPresent() && !tgtValue.isPresent()) {
      return Actions.ADDED;

    } else if (!srcValue.isPresent() && tgtValue.isPresent()) {
      return Actions.REMOVED;

    } else {
      return null;
    }
  }

  private void findDiffType() {
    if (tgtValue.isPresent()) {

      // IF input for tgtValue is Modifier
      if (tgtValue.get() instanceof ASTModifier) {
        ASTModifier tgtModifier = (ASTModifier) tgtValue.get();
        if (srcValue.isPresent() && srcValue.get() instanceof ASTModifier) {
          ASTModifier srcModifier = (ASTModifier) srcValue.get();
          if ((tgtModifier.isPublic() && srcModifier.isPublic())
              || (tgtModifier.isProtected() && srcModifier.isProtected())
              || (tgtModifier.isPrivate() && srcModifier.isPrivate())) {
            this.difference = DiffTypes.EQUAL;
          } else if (tgtModifier.isPublic()
              && (srcModifier.isPrivate() || srcModifier.isProtected())) {
            this.difference = DiffTypes.REFINEMENT;
          } else {
            this.difference = DiffTypes.SCOPE_CHANGE;
          }
        } else {
          if (!tgtModifier.isPublic()) {
            this.difference = DiffTypes.EXPANSION;
          }
        }
      }

      // IF input for tgtValue is Cardinality
      if (tgtValue.get() instanceof ASTCDCardinality) {
        int tgtLowerBound = ((ASTCDCardinality) tgtValue.get()).getLowerBound();
        int tgtUpperBound = ((ASTCDCardinality) tgtValue.get()).getUpperBound();
        if (srcValue.isPresent()) {
          int srcLowerBound = ((ASTCDCardinality) srcValue.get()).getLowerBound();
          int srcUpperBound = ((ASTCDCardinality) srcValue.get()).getUpperBound();

          if (((ASTCDCardinality) tgtValue.get()).toCardinality().isNoUpperLimit()
              || ((ASTCDCardinality) srcValue.get()).toCardinality().isNoUpperLimit()) {
            // One or both upper bounds are infinite
            if (tgtUpperBound == srcUpperBound) {
              // Both upper bounds are infinite
              if (tgtLowerBound < srcLowerBound) {
                this.difference = DiffTypes.RESTRICT_INTERVAL;
              } else {
                this.difference = DiffTypes.EXPAND_INTERVAL;
              }
            } else {
              if (((ASTCDCardinality) srcValue.get()).toCardinality().isNoUpperLimit()) {
                this.difference = DiffTypes.EXPAND_INTERVAL;
              } else {
                this.difference = DiffTypes.RESTRICT_INTERVAL;
              }
            }
          } else {
            if ((tgtUpperBound - tgtLowerBound) < (srcUpperBound - srcLowerBound)) {
              this.difference = DiffTypes.EXPAND_INTERVAL;
            } else if ((tgtUpperBound - tgtLowerBound) == (srcUpperBound - srcLowerBound)) {
              this.difference = DiffTypes.EQUAL_INTERVAL;
            } else {
              this.difference = DiffTypes.RESTRICT_INTERVAL;
            }
          }
        } else {
          // Cardinality was deleted
          if (((ASTCDCardinality) tgtValue.get()).toCardinality().isNoUpperLimit()
              && tgtLowerBound == 0) {
            // [0..*] == [*]
            this.difference = DiffTypes.EQUAL_INTERVAL;
          } else {
            // [n..m] -> [*] (n != 0) or (m != inf)
            this.difference = DiffTypes.EXPAND_INTERVAL;
          }
        }
      }

      // IF input for tgtValue is Name
      if (tgtValue.get() instanceof ASTMCQualifiedName) {
        this.difference = DiffTypes.RENAME;
      }

      // IF input for tgtValue is Role
      if (tgtValue.get() instanceof ASTCDRole) {
        if (srcValue.isPresent()) {
          // If the input consists of the old AND the new role, then there was a name change
          this.difference = DiffTypes.ROLE_CHANGE;
        } else {
          // If the input consists only of the old role name, then the role name was deleted in the
          // new CD
          this.difference = DiffTypes.DELETED;
        }
      }

      // IF input for tgtValue is AssocDir
      if (tgtValue.get() instanceof ASTCDAssocDir) {
        // Here, we don't have else()-case, because direction in association is non-optional
        if (srcValue.isPresent() && srcValue.get() instanceof ASTCDAssocDir) {
          ASTCDAssocDir tgtAssocDir = (ASTCDAssocDir) tgtValue.get();
          ASTCDAssocDir srcAssocDir = (ASTCDAssocDir) srcValue.get();
          if (tgtAssocDir.isBidirectional()) {
            if (!(srcAssocDir.isDefinitiveNavigableLeft()
                || srcAssocDir.isDefinitiveNavigableRight())) {
              // <-> to --
              this.difference = DiffTypes.ABSTRACTION;
            } else {
              // <-> to (<- or ->)
              this.difference = DiffTypes.RESTRICTION;
            }
          } else if (srcAssocDir.isBidirectional()) {
            if (tgtAssocDir.isDefinitiveNavigableRight()
                || tgtAssocDir.isDefinitiveNavigableLeft()) {
              // (-> or <-) to <->
              this.difference = DiffTypes.EXPANSION;
            } else {
              // -- to <->
              this.difference = DiffTypes.REFINEMENT;
            }
          } else if (tgtAssocDir.isDefinitiveNavigableRight()
              && !srcAssocDir.isDefinitiveNavigableRight()) {
            if (srcAssocDir.isDefinitiveNavigableLeft()) {
              // -> to <-
              this.difference = DiffTypes.REVERSED_ASSOCIATION;
            } else {
              // -> to --
              this.difference = DiffTypes.ABSTRACTION;
            }
          } else if (tgtAssocDir.isDefinitiveNavigableLeft()
              && !srcAssocDir.isDefinitiveNavigableLeft()) {
            if (srcAssocDir.isDefinitiveNavigableRight()) {
              // <- to ->
              this.difference = DiffTypes.REVERSED_ASSOCIATION;
            } else {
              // <- to --
              this.difference = DiffTypes.ABSTRACTION;
            }
          } else if (srcAssocDir.isDefinitiveNavigableRight()
              || srcAssocDir.isDefinitiveNavigableLeft()) {
            // -- to (<- or ->)
            this.difference = DiffTypes.REFINEMENT;
          } else {
            this.difference = DiffTypes.EQUAL;
          }
        }
      }
    } else { // IF (old) tgtValue is not present
      if (srcValue.isPresent()) {
        if (srcValue.get() instanceof ASTModifier) {
          if (((ASTModifier) srcValue.get()).isPublic()) {
            this.difference = DiffTypes.EQUAL;
          }
        }
        if (srcValue.get() instanceof ASTCDCardinality) {
          if (((ASTCDCardinality) srcValue.get()).getLowerBound() == 0
              && ((ASTCDCardinality) srcValue.get()).getUpperBound() == 0) {
            // [*] to [0..*]
            this.difference = DiffTypes.EQUAL_INTERVAL;
          } else {
            // [*] to [n..m] (n != 0) or ( m != inf)
            this.difference = DiffTypes.RESTRICT_INTERVAL;
          }
        }
        if (srcValue.get() instanceof ASTCDRole) {
          this.difference = DiffTypes.REFINEMENT;
        }
        if (srcValue.get() instanceof ASTMCType) {
          this.difference = DiffTypes.TYPE_CHANGE;
          // Todo: check for Sub/Supertype
        }
      }
    }
  }
}
