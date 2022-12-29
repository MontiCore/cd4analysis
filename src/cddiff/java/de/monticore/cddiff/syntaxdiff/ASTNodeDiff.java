/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cdassociation._ast.ASTCDAssocDir;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff.Interpretation;
import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff.Op;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.util.Optional;

/**
 * Diff Type for Fields Use the constructor to create a diff between two given fields This diff type
 * contains information extracted from the provided fields, especially the type of change
 */
public class ASTNodeDiff<T1 extends ASTNode, T2 extends ASTNode> implements IASTNodeDiff<T1, T2> {

  protected Interpretation interpretation;

  protected Op operation;

  protected final Optional<T1> cd1Value;

  protected final Optional<T2> cd2Value;

  @Override
  public boolean isPresent() {
    return getOperation().isPresent();
  }

  @Override
  public Optional<Interpretation> getInterpretation() {
    if (interpretation == null) {
      return Optional.empty();
    } else {
      return Optional.of(interpretation);
    }
  }

  @Override
  public Optional<Op> getOperation() {
    if (operation == null) {
      return Optional.empty();
    } else {
      return Optional.of(operation);
    }
  }

  @Override
  public Optional<T1> getCd1Value() {
    return cd1Value;
  }

  @Override
  public Optional<T2> getCd2Value() {
    return cd2Value;
  }

  /**
   * Constructor of the field diff type
   *
   * @param cd1Value Field from the original model
   * @param cd2Value Field from the target(new) model
   */
  public ASTNodeDiff(Optional<T1> cd1Value, Optional<T2> cd2Value) {
    this.cd1Value = cd1Value;
    this.cd2Value = cd2Value;
    this.operation = setOp();

    setInterpretation();
  }

  public ASTNodeDiff(Op op, Optional<T1> cd1Value, Optional<T2> cd2Value) {
    this.cd1Value = cd1Value;
    this.cd2Value = cd2Value;
    this.operation = op;
  }

  /**
   * Set the operation which happens between the fields provided on creation of this object
   *
   * @return Operation to be set
   */
  protected Op setOp() {
    if (cd1Value.isPresent()
        && cd2Value.isPresent()
        && !cd1Value.get().deepEquals(cd2Value.get())) {
      // Diff reason: Value changed
      return Op.CHANGE;

    } else if (cd1Value.isPresent() && !cd2Value.isPresent()) {
      // Diff reason: Value deleted
      return Op.DELETE;

    } else if (!cd1Value.isPresent() && cd2Value.isPresent()) {
      // Diff reason: Value added
      return Op.ADD;

    } else {
      // No Diff reason: is equal
      return null;
    }
  }

  /**
   * Set the interpretation and pretty print for each field in this diff (if there is a value given)
   */
  private void setInterpretation() {
    if (cd1Value.isPresent()) {
      if (cd1Value.get() instanceof ASTModifier) {
        ASTModifier cd1 = (ASTModifier) cd1Value.get();
        if (cd2Value.isPresent() && cd2Value.get() instanceof ASTModifier) {
          ASTModifier cd2 = (ASTModifier) cd2Value.get();
          if ((cd1.isPublic() && cd2.isPublic())
              || (cd1.isProtected() && cd2.isProtected())
              || (cd1.isPrivate() && cd2.isPrivate())) {
            this.interpretation = Interpretation.EQUAL;
          } else if (cd1.isPublic() && (cd2.isPrivate() || cd2.isProtected())) {
            this.interpretation = Interpretation.REFINEMENT;
          } else {
            this.interpretation = Interpretation.SCOPECHANGE;
          }
        } else {
          if (!cd1.isPublic()) {
            this.interpretation = Interpretation.EXPANSION;
          }
        }
      }
      if (cd1Value.get() instanceof ASTCDCardinality) {

        int cd1Lower = ((ASTCDCardinality) cd1Value.get()).getLowerBound();
        int cd1Upper = ((ASTCDCardinality) cd1Value.get()).getUpperBound();
        if (cd2Value.isPresent()) {
          // Cardinality was changed
          int cd2Lower = ((ASTCDCardinality) cd2Value.get()).getLowerBound();
          int cd2Upper = ((ASTCDCardinality) cd2Value.get()).getUpperBound();

          if (((ASTCDCardinality) cd1Value.get()).toCardinality().isNoUpperLimit()
              || ((ASTCDCardinality) cd2Value.get()).toCardinality().isNoUpperLimit()) {
            // One/both upper bounds are infinite
            if (cd1Upper == cd2Upper) {
              // Both upper bounds are infinite
              if (cd1Lower < cd2Lower) {
                this.interpretation = Interpretation.RESTRICT_INTERVAL;
              } else {
                this.interpretation = Interpretation.EXPAND_INTERVAL;
              }
            } else {
              if (((ASTCDCardinality) cd2Value.get()).toCardinality().isNoUpperLimit()) {
                this.interpretation = Interpretation.EXPAND_INTERVAL;
              } else {
                this.interpretation = Interpretation.RESTRICT_INTERVAL;
              }
            }
          } else {
            if ((cd1Upper - cd1Lower) < (cd2Upper - cd2Lower)) {
              this.interpretation = Interpretation.EXPAND_INTERVAL;
            } else if ((cd1Upper - cd1Lower) == (cd2Upper - cd2Lower)) {
              this.interpretation = Interpretation.EQUAL_INTERVAL;
            } else {
              this.interpretation = Interpretation.RESTRICT_INTERVAL;
            }
          }
        } else {
          // Cardinality was deleted
          if (((ASTCDCardinality) cd1Value.get()).toCardinality().isNoUpperLimit()
              && cd1Lower == 0) {
            // [0..*] == [*]
            this.interpretation = Interpretation.EQUAL_INTERVAL;
          } else {
            // [n..m] -> [*] (n != 0) or (m != inf)
            this.interpretation = Interpretation.EXPAND_INTERVAL;
          }
        }
      }
      if (cd1Value.get() instanceof ASTMCQualifiedName) {
        this.interpretation = Interpretation.RENAME;
      }
      if (cd1Value.get() instanceof ASTCDRole) {
        if (cd2Value.isPresent()) {
          // Role was changed
          this.interpretation = Interpretation.ROLECHANGE;
        } else {
          this.interpretation = Interpretation.DELETED;
        }
      }
      if (cd1Value.get() instanceof ASTCDAssocDir) {
        // Should always be the case, Direction is non-optional
        if (cd2Value.isPresent() && cd2Value.get() instanceof ASTCDAssocDir) {
          ASTCDAssocDir cd1 = (ASTCDAssocDir) cd1Value.get();
          ASTCDAssocDir cd2 = (ASTCDAssocDir) cd2Value.get();
          if (cd1.isBidirectional() && !cd2.isBidirectional()) {
            if (!(cd2.isDefinitiveNavigableLeft() || cd2.isDefinitiveNavigableRight())) {
              // <-> to --
              this.interpretation = Interpretation.ABSTRACTION;
            } else if (!(cd2.isDefinitiveNavigableLeft() && cd2.isDefinitiveNavigableRight())) {
              // <-> to -> or <-
              this.interpretation = Interpretation.RESTRICTION;
            }
          } else if (cd2.isBidirectional()) {
            if (cd1.isDefinitiveNavigableRight() || cd1.isDefinitiveNavigableLeft()) {
              // (-> or <-) to <->
              this.interpretation = Interpretation.EXPANSION;
            } else {
              // -- to <->
              this.interpretation = Interpretation.REFINEMENT;
            }
          } else if (cd1.isDefinitiveNavigableRight() && !cd2.isDefinitiveNavigableRight()) {
            if (cd2.isDefinitiveNavigableLeft()) {
              // -> to <-
              this.interpretation = Interpretation.REVERSED;
            } else {
              // -> to --
              this.interpretation = Interpretation.ABSTRACTION;
            }
          } else if (cd1.isDefinitiveNavigableLeft() && !cd2.isDefinitiveNavigableLeft()) {
            if (cd2.isDefinitiveNavigableRight()) {
              // <- to ->
              this.interpretation = Interpretation.REVERSED;
            } else {
              // <- to --
              this.interpretation = Interpretation.ABSTRACTION;
            }

          } else if (cd2.isDefinitiveNavigableRight() || cd2.isDefinitiveNavigableLeft()) {
            // -- to (<- or ->)
            this.interpretation = Interpretation.REFINEMENT;
          } else {
            this.interpretation = Interpretation.EQUAL;
          }
        }
      }
      if (cd1Value.get() instanceof ASTExpression) {
        if (!cd2Value.isPresent()) {
          this.interpretation = Interpretation.DELETED;
        } else {
          this.interpretation = Interpretation.DEFAULTVALUECHANGED;
        }
      }
    }
    if (cd2Value.isPresent()) {
      if (cd2Value.get() instanceof ASTModifier) {
        ASTModifier cd2 = (ASTModifier) cd2Value.get();
        if (!cd1Value.isPresent()) {
          if (cd2.isPublic()) {
            this.interpretation = Interpretation.EQUAL;
          }
        }
      }
      if (cd2Value.get() instanceof ASTCDCardinality) {
        // Default value is [*]
        int cd2Lower = ((ASTCDCardinality) cd2Value.get()).getLowerBound();
        int cd2Upper = ((ASTCDCardinality) cd2Value.get()).getUpperBound();
        if (cd2Lower == 0 && cd2Upper == 0) {
          // [*] to [0..*]
          this.interpretation = Interpretation.EQUAL_INTERVAL;
        } else {
          // [*] to [n..m] (n != 0) or ( m != inf)
          this.interpretation = Interpretation.RESTRICT_INTERVAL;
        }
      }
      if (cd2Value.get() instanceof ASTCDRole) {
        this.interpretation = Interpretation.REFINEMENT;
      }
      if (cd2Value.get() instanceof ASTMCType) {
        this.interpretation = Interpretation.TYPECHANGE;
        // Todo: check for Sub/Supertype
      }
      if (cd2Value.get() instanceof ASTExpression) {
        if (!cd1Value.isPresent()) {
          this.interpretation = Interpretation.DEFAULTVALUE_ADDED;
        }
      }
    }
  }
}
