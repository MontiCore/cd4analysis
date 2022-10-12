package de.monticore.cd2smt.Helper;

import com.microsoft.z3.BoolExpr;
import de.se_rwth.commons.SourcePosition;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Identifiable<T> {
  protected static int counter = 0;
  protected static Map<Integer,Identifiable<BoolExpr>> constraintMap = new HashMap<>();
  protected final T value;
  protected final int id;
  protected final SourcePosition sourcePosition;
  protected Optional<String> invariantName;

  private Identifiable(T value, SourcePosition sourcePosition, Optional<String> invariantName) {
    this.value = value;
    this.id = counter;
    this.sourcePosition = sourcePosition;
    this.invariantName = invariantName;
    counter++;
  }

  public static  Identifiable<BoolExpr> buildBoolExprIdentifiable(BoolExpr value, SourcePosition sourcePosition,Optional<String> invariantName) {
    Identifiable<BoolExpr> identifiable = new Identifiable<>(value, sourcePosition, invariantName);
    constraintMap.put(identifiable.getId(),identifiable);
    return identifiable;
  }

  public static Identifiable<BoolExpr> getBoolExprIdentifiable(int id){
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
  public Path getFile(){
    assert sourcePosition.getFileName().isPresent();
    return Path.of(sourcePosition.getFileName().get());
  }
  public void setInvariantName(Optional<String> invariantName) {
    this.invariantName = invariantName;
  }

  public T getValue() {
    return value;
  }

}
