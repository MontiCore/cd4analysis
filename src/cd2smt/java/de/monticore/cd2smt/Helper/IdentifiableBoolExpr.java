package de.monticore.cd2smt.Helper;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.se_rwth.commons.SourcePosition;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IdentifiableBoolExpr {
  protected static int counter = 0;
  protected static Map<Integer, IdentifiableBoolExpr> constraintMap = new HashMap<>();
  protected final int id;
  protected final SourcePosition sourcePosition;
  protected final BoolExpr value;
  protected Optional<String> invariantName;
  protected boolean wasNegated;

  private IdentifiableBoolExpr(BoolExpr value, SourcePosition sourcePosition, Optional<String> invariantName, boolean wasNegated) {
    this.value = value;
    this.id = counter;
    this.sourcePosition = sourcePosition;
    this.invariantName = invariantName;
    this.wasNegated = wasNegated;
    counter++;
  }

  private static IdentifiableBoolExpr buildBoolExprIdentifiable(BoolExpr value, SourcePosition sourcePosition, Optional<String> invariantName,boolean wasNegated) {
    IdentifiableBoolExpr identifiableBoolexpr = new IdentifiableBoolExpr(value, sourcePosition, invariantName, wasNegated);
    constraintMap.put(identifiableBoolexpr.getId(), identifiableBoolexpr);
    return identifiableBoolexpr;
  }
  public static IdentifiableBoolExpr buildBoolExprIdentifiable(BoolExpr value, SourcePosition sourcePosition, Optional<String> invariantName) {
    return  buildBoolExprIdentifiable(value, sourcePosition, invariantName, false);
  }

  public static IdentifiableBoolExpr getBoolExprIdentifiable(int id) {
    return constraintMap.get(id);
  }

  public int getId() {
    return id;
  }

  public SourcePosition getSourcePosition() {
    return sourcePosition;
  }

  public Optional<String> getInvariantName() {
    return invariantName;
  }

  public void setInvariantName(Optional<String> invariantName) {
    this.invariantName = invariantName;
  }

  public Path getFile() {
    assert sourcePosition.getFileName().isPresent();
    return Path.of(sourcePosition.getFileName().get());
  }

  public BoolExpr getValue() {
    return value;
  }

  public boolean wasNegated() {
    return this.wasNegated;
  }

  public IdentifiableBoolExpr negate(Context ctx) {
  return   buildBoolExprIdentifiable(ctx.mkNot(this.getValue()),this.sourcePosition,this.invariantName,true);
  }

}
