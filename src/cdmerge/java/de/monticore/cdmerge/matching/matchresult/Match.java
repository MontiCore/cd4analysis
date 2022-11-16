/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.matchresult;

import de.monticore.ast.ASTNode;
import java.util.Optional;

/** Represents an edge in a match graph */
public class Match<E extends ASTNode, P extends ASTNode> {

  private MatchNode<E, P> node1;

  private MatchNode<E, P> node2;

  public Match(MatchNode<E, P> node1, MatchNode<E, P> node2) {
    this.node1 = node1;
    this.node2 = node2;
  }

  public MatchNode<E, P> getNode1() {
    return this.node1;
  }

  public MatchNode<E, P> getNode2() {
    return this.node2;
  }

  /**
   * Checks whether this edge bidirectionally connects the two nodes
   *
   * @return true if both nodes are connected with this edge
   */
  public boolean connects(MatchNode<E, P> node1, MatchNode<E, P> node2) {
    // bidirectional
    return this.node1 == node1 && this.node2 == node2 || this.node1 == node2 && this.node2 == node1;
  }

  /** Returns the corresponding adjacent node of this edge. */
  public Optional<MatchNode<E, P>> getOther(MatchNode<E, P> node) {
    if (this.node1 == node) {
      return Optional.of(node2);
    }
    if (this.node2 == node) {
      return Optional.of(node1);
    }
    return Optional.empty();
  }
}
