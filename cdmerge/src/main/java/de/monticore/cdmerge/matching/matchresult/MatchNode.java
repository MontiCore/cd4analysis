/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.matchresult;

import de.monticore.ast.ASTNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A Match Node represents an element in a match graph {@link ASTMatchGraph}
 *
 * @param <U>
 * @param <E2>
 */
public class MatchNode<Element extends ASTNode, Parent extends ASTNode> {

  /** The model-element which was considered during the match operation */
  private Element element;

  /** */
  private Optional<String> cdPackage;

  /** The model-elements' parent node, e.g. a CD or a class */
  private Parent parent;

  /** The graps's edges: The list of adjacent matching nodes in this match graph */
  private List<Match<Element, Parent>> matches;

  public MatchNode(Element matchedElement, Parent parent) {
    this(matchedElement, parent, Optional.empty());
  }

  public MatchNode(Element matchedElement, Parent parent, Optional<String> cdPackage) {
    this.element = matchedElement;
    this.cdPackage = cdPackage;
    this.parent = parent;
    this.matches = new LinkedList<Match<Element, Parent>>();
  }

  public Element getElement() {
    return this.element;
  }

  public Parent getParent() {
    return this.parent;
  }

  /** Checks if this node has a reference to this match */
  public boolean hasMatch(MatchNode<Element, Parent> other) {
    for (Match<Element, Parent> m : matches) {
      if (m.connects(this, other)) {
        return true;
      }
    }
    return false;
  }

  /** Returns true if this node has at least one matching edge */
  public boolean hasMatch() {
    return this.matches.size() > 0;
  }

  /**
   * Returns true if this node has at least one matching edge which refers to the specified parent
   * node
   */
  public boolean hasMatch(Parent parent) {
    return getMatchedNodes(parent).size() > 0;
  }

  /**
   * Adds an match (edge) to the node if there wasn't already one. Adds the reverse match to other
   * node, too
   */
  public void addMatch(MatchNode<Element, Parent> other) {
    if (!hasMatch(other)) {
      Match<Element, Parent> m = new Match<Element, Parent>(this, other);
      this.matches.add(m);
      // Reverse Direction
      other.addMatch(m);
    }
  }

  /** Adds the bidirectional reverse pointer */
  private void addMatch(Match<Element, Parent> m) {
    if (!m.getOther(this).isPresent() || !hasMatch(m.getOther(this).get())) {
      this.matches.add(m);
    }
  }

  /** Returns all the matching elements pairwise */
  public List<Match<Element, Parent>> getMatches() {
    return this.matches;
  }

  /** Returns a flat list of all matching nodes */
  public List<MatchNode<Element, Parent>> getMatchedNodes() {
    List<MatchNode<Element, Parent>> matchedNodes = new ArrayList<>(matches.size());
    for (Match<Element, Parent> m : matches) {
      if (m.getOther(this).isPresent()) {
        matchedNodes.add(m.getOther(this).get());
      }
    }
    return matchedNodes;
  }

  /** Returns a flat list of all matching nodes */
  public List<Element> getMatchedElements() {
    List<Element> matchedNodes = new ArrayList<>(matches.size());
    for (Match<Element, Parent> m : matches) {
      if (m.getOther(this).isPresent()) {
        matchedNodes.add(m.getOther(this).get().getElement());
      }
    }
    return matchedNodes;
  }

  /** Returns a flat list of all matching nodes which have the specified parent */
  public List<MatchNode<Element, Parent>> getMatchedNodes(Parent parent) {
    List<MatchNode<Element, Parent>> matchedNodes = new ArrayList<>(matches.size());
    Optional<MatchNode<Element, Parent>> match;
    for (Match<Element, Parent> m : matches) {
      match = m.getOther(this);
      if (match.isPresent() && match.get().getParent() == parent) {
        matchedNodes.add(match.get());
      }
    }
    return matchedNodes;
  }

  public Optional<String> getPackage() {
    return this.cdPackage;
  }
}
