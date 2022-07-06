package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.cd4codebasis._ast.ASTCDThrowsDeclaration;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.syntaxdiff.SyntaxDiff.Op;
import de.monticore.syntaxdiff.SyntaxDiff.Interpretation;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;

import java.util.Optional;
/**
 * Diff Type for Fields
 * Use the constructor to create a diff between two given fields
 * This diff type contains information extracted from the provided fields, especially the type of change
 */
public class FieldDiff<ASTNodeType extends ASTNode> {

  protected Interpretation interpretation;

  protected Op operation;

  protected String cd1pp;

  protected String cd2pp;

  protected final Optional<ASTNodeType> cd1Value;

  protected final Optional<ASTNodeType> cd2Value;

  public boolean isPresent(){
    return getOperation().isPresent();
  }

  public Optional<Interpretation> getInterpretation() {
    if (interpretation == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(interpretation);
    }
  }

  public Optional<Op> getOperation() {
    if (operation == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(operation);
    }
  }

  public Optional<String> getCd1pp() {
    if (cd1pp == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(cd1pp);
    }
  }
  public Optional<String> getCd2pp() {
    if (cd2pp == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(cd2pp);
    }
  }

  public Optional<ASTNodeType> getCd1Value() {
    return cd1Value;
  }

  public Optional<ASTNodeType> getCd2Value() {
    return cd2Value;
  }

  /**
   * Constructor of the field diff type
   * @param cd1Value Field from the original model
   * @param cd2Value Field from the target(new) model
   */
  public FieldDiff(Optional<ASTNodeType> cd1Value, Optional<ASTNodeType> cd2Value) {
    this.cd1Value = cd1Value;
    this.cd2Value = cd2Value;
    this.operation = setOp();

    setInterpretation();
  }

  public FieldDiff(Op op, Optional<ASTNodeType> cd1Value, Optional<ASTNodeType> cd2Value) {
    this.cd1Value = cd1Value;
    this.cd2Value = cd2Value;
    this.operation = op;
  }

  /**
   * Set the operation which happens between the fields provided on creation of this object
   * @return Operation to be set
   */
  protected Op setOp() {
    if (cd1Value.isPresent() && cd2Value.isPresent() && !cd1Value.get().deepEquals(cd2Value.get())) {
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
  private void setInterpretation(){
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    if (cd1Value.isPresent()){
      if(cd1Value.get() instanceof ASTCDOrdered){
        this.cd1pp = pp.prettyprint((ASTCDOrdered) cd1Value.get());
      }
      if(cd1Value.get() instanceof ASTModifier){
        this.cd1pp = pp.prettyprint((ASTModifier) cd1Value.get());
        ASTModifier cd1 = (ASTModifier) cd1Value.get();
        if (cd2Value.isPresent()){
          ASTModifier cd2 = (ASTModifier) cd2Value.get();
          if (cd1.isPublic() && cd2.isPublic()){
            this.interpretation = Interpretation.EQUAL;
          }else if (cd1.isPublic() && (cd2.isPrivate() || cd2.isProtected())){
            this.interpretation = Interpretation.REFINEMENT;
          }else {
            this.interpretation = Interpretation.SCOPECHANGE;
          }
        }else {
          if(!cd1.isPublic()){
            this.interpretation = Interpretation.EXPANSION;
          }
        }
      }
      if(cd1Value.get() instanceof ASTCDCardinality){
        this.cd1pp = pp.prettyprint((ASTCDCardinality) cd1Value.get());

        int cd1Lower = ((ASTCDCardinality) cd1Value.get()).getLowerBound();
        int cd1Upper = ((ASTCDCardinality) cd1Value.get()).getUpperBound();
        if (cd2Value.isPresent()){
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
          }else {
            if( (cd1Upper-cd1Lower) < (cd2Upper-cd2Lower)){
              this.interpretation = Interpretation.EXPAND_INTERVAL;
            } else if ((cd1Upper-cd1Lower) == (cd2Upper-cd2Lower)) {
              this.interpretation = Interpretation.EQUAL_INTERVAL;
            } else {
              this.interpretation = Interpretation.RESTRICT_INTERVAL;
            }
          }
        }else {
          // Cardninality was deleted
          if (((ASTCDCardinality) cd1Value.get()).toCardinality().isNoUpperLimit() && cd1Lower == 0){
            // [0..*] == [*]
            this.interpretation = Interpretation.EQUAL_INTERVAL;
          }else {
            // [n..m] -> [*] (n != 0) or (m != inf)
            this.interpretation = Interpretation.EXPAND_INTERVAL;
          }
        }
      }
      if(cd1Value.get() instanceof ASTMCQualifiedName){
        this.cd1pp = pp.prettyprint((ASTMCQualifiedName) cd1Value.get());
        this.interpretation = Interpretation.RENAME;
      }
      if(cd1Value.get() instanceof ASTCDQualifier){
        this.cd1pp = pp.prettyprint((ASTCDQualifier) cd1Value.get());
      }
      if(cd1Value.get() instanceof ASTCDRole){
        this.cd1pp = pp.prettyprint((ASTCDAssociationNode) cd1Value.get());
        if (cd2Value.isPresent()){
          // Role was changed
          this.interpretation = Interpretation.ROLECHANGE;
        }else {
          this.interpretation = Interpretation.DELETED;
        }
      }
      if(cd1Value.get() instanceof ASTStereotype){
        this.cd1pp = pp.prettyprint((ASTStereotype) cd1Value.get());
      }
      if(cd1Value.get() instanceof ASTCDAssocDir){
        this.cd1pp = pp.prettyprint((ASTCDAssocDir) cd1Value.get());
        // Should always be the case, Direction is non-optional
        if (cd2Value.isPresent()){
          ASTCDAssocDir cd1 = (ASTCDAssocDir) cd1Value.get();
          ASTCDAssocDir cd2 = (ASTCDAssocDir) cd2Value.get();
          if (cd1.isBidirectional()) {
            if( !(cd2.isDefinitiveNavigableLeft() || cd2.isDefinitiveNavigableRight())){
              // <-> to -> or <-
              this.interpretation = Interpretation.ABSTRACTION;
            } else {
              // <-> to --
              this.interpretation = Interpretation.ABSTRACTION;
            }
          }else {
            if ( (cd1.isDefinitiveNavigableRight() && cd2.isDefinitiveNavigableLeft() )
              || (cd2.isDefinitiveNavigableRight() && cd1.isDefinitiveNavigableLeft() )){
              // (-> to <-)  (<- to ->)
              this.interpretation = Interpretation.REVERSED;
              //Todo: Or REPURPOSED? Because it can we viewed as a new association
            }else {
              if (!(cd1.isDefinitiveNavigableLeft() || cd1.isDefinitiveNavigableRight())){
                // -- to (-> or <-)
                this.interpretation = Interpretation.REFINEMENT;
              }else {
                // (-> or <-) to --
                this.interpretation = Interpretation.ABSTRACTION;
              }
            }
          }
        }
      }
      if(cd1Value.get() instanceof ASTCDExtendUsage){
        this.cd1pp = pp.prettyprint((ASTCDExtendUsage) cd1Value.get());
      }
      if(cd1Value.get() instanceof ASTMCType){
        this.cd1pp = pp.prettyprint((ASTMCType) cd1Value.get());
      }
      if(cd1Value.get() instanceof ASTCDAttribute){
        this.cd1pp = pp.prettyprint((ASTCDAttribute) cd1Value.get());
        this.interpretation = Interpretation.RENAME;
      }
      if(cd1Value.get() instanceof ASTExpression){
        this.cd1pp = pp.prettyprint((ASTExpression) cd1Value.get());
        if (!cd2Value.isPresent()){
          this.interpretation = Interpretation.DELETED;
        }else {
          this.interpretation = Interpretation.DEFAULTVALUECHANGED;
        }
      }
      if(cd1Value.get() instanceof ASTMCReturnType){
        this.cd1pp = pp.prettyprint((ASTMCReturnType) cd1Value.get());
      }
      if(cd1Value.get() instanceof ASTCDThrowsDeclaration){
        this.cd1pp = pp.prettyprint((ASTCDThrowsDeclaration) cd1Value.get());
      }

    }
    if (cd2Value.isPresent()){
      // Only Adds need to be considered, changes are already covered by the cd1value part
      if(cd2Value.get() instanceof ASTCDOrdered){
        this.cd2pp = pp.prettyprint((ASTCDOrdered) cd2Value.get());
      }
      if(cd2Value.get() instanceof ASTModifier){
        this.cd2pp = pp.prettyprint((ASTModifier) cd2Value.get());
        ASTModifier cd2 = (ASTModifier) cd2Value.get();
        if (!cd1Value.isPresent()){
          if(cd2.isPublic()){
            this.interpretation = Interpretation.EQUAL;
          }
        }

      }
      if(cd2Value.get() instanceof ASTCDCardinality){
        this.cd2pp = pp.prettyprint((ASTCDCardinality) cd2Value.get());
        // Default value is [*]
        int cd2Lower = ((ASTCDCardinality) cd2Value.get()).getLowerBound();
        int cd2Upper = ((ASTCDCardinality) cd2Value.get()).getUpperBound();
        if (cd2Lower == 0 && cd2Upper == 0) {
          // [*] to [0..*]
          this.interpretation = Interpretation.EQUAL_INTERVAL;
        }else {
          // [*] to [n..m] (n != 0) or ( m != inf)
          this.interpretation = Interpretation.RESTRICT_INTERVAL;
        }
      }
      if(cd2Value.get() instanceof ASTMCQualifiedName){
        this.cd2pp = pp.prettyprint((ASTMCQualifiedName) cd2Value.get());
      }
      if(cd2Value.get() instanceof ASTCDQualifier){
        this.cd2pp = pp.prettyprint((ASTCDQualifier) cd2Value.get());
      }
      if(cd2Value.get() instanceof ASTCDRole){
        this.cd2pp = pp.prettyprint((ASTCDAssociationNode) cd2Value.get());
        //Todo: Is adding a role therefore () -> (Role) a refinement? Is there a default value?
        this.interpretation = Interpretation.REFINEMENT;
      }
      if(cd2Value.get() instanceof ASTStereotype){
        this.cd2pp = pp.prettyprint((ASTStereotype) cd2Value.get());
      }
      if(cd2Value.get() instanceof ASTCDAssocDir){
        this.cd2pp = pp.prettyprint((ASTCDAssocDir) cd2Value.get());
      }
      if(cd2Value.get() instanceof ASTCDExtendUsage){
        this.cd2pp = pp.prettyprint((ASTCDExtendUsage) cd2Value.get());
      }
      if(cd2Value.get() instanceof ASTMCType){
        this.cd2pp = pp.prettyprint((ASTMCType) cd2Value.get());
        this.interpretation = Interpretation.TYPECHANGE;
        //Todo: check for Sub/Supertype
      }
      if(cd2Value.get() instanceof ASTCDAttribute){
        this.cd2pp = pp.prettyprint((ASTCDAttribute) cd2Value.get());
      }
      if(cd2Value.get() instanceof ASTExpression){
        this.cd2pp = pp.prettyprint((ASTExpression) cd2Value.get());
        if (!cd1Value.isPresent()){
          this.interpretation = Interpretation.DEFAULTVALUE_ADDED;
        }
      }
      if(cd2Value.get() instanceof ASTMCReturnType){
        this.cd2pp = pp.prettyprint((ASTMCReturnType) cd2Value.get());
      }
      if(cd2Value.get() instanceof ASTCDThrowsDeclaration){
        this.cd2pp = pp.prettyprint((ASTCDThrowsDeclaration) cd2Value.get());
      }
    }
  }
}
