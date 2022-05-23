package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;

import java.util.Optional;

public class SyntaxDiff {

  public enum Op { CHANGE, ADD, DELETE}

  // AssocName
  public static <NodeType extends ASTNode> FieldDiff<Op, NodeType> fieldDiffOptional(Optional<NodeType> cd1Field,
  Optional<NodeType> cd2Field) {
    if (cd1Field.isPresent() && cd2Field.isPresent() && !cd1Field.get().deepEquals(cd2Field.get())) {
      // Diff reason: Value changed
      return new FieldDiff<>(Op.CHANGE, cd1Field.get(), cd2Field.get());

    } else if (cd1Field.isPresent() && !cd2Field.isPresent()) {
      // Diff reason: Value deleted
      return new FieldDiff<>(Op.DELETE, cd1Field.get(), null);

    } else if (!cd1Field.isPresent() && cd2Field.isPresent()) {
      // Diff reason: Value added
      return new FieldDiff<>(Op.ADD, null, cd2Field.get());

    } else {
      // No Diff reason: is equal
      return new FieldDiff<>();
    }
  }
}
