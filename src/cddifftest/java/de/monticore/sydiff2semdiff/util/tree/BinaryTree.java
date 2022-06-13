package de.monticore.sydiff2semdiff.util.tree;

import java.util.*;

/**
 * Binary tree
 */
public class BinaryTree {
  private Node root;

  /**
   * Create new binary tree with root node
   * @param root Root node
   */
  public BinaryTree(Node root) {
    this.setRoot(root);
  }

  /**
   * Create new empty binary tree
   */
  public BinaryTree() {
    this.setRoot(null);
  }


  public Node getRoot() {
    return root;
  }

  public void setRoot(Node root) {
    this.root = root;
  }

  /**
   * Insert new node into the tree. <br>
   * This method will insert new node into last tree level, until last tree level is full, then add new level.
   * @param newNode new tree node
   */
  public void insert(Node newNode) {
    if (this.root == null) {
      this.root = newNode;
    } else if (this.root.getLeft() == null) {
      this.root.setLeft(newNode);
    } else if (this.root.getRight() == null) {
      this.root.setRight(newNode);
    } else {
      List<Node> siblingNodes = new LinkedList<Node>();
      siblingNodes.add(this.root.getLeft());
      siblingNodes.add(this.root.getRight());
      insert2(siblingNodes, newNode);
    }
  }

  /**
   * Check a level sibling nodes, find the node which dones't have left or right child, then insert the new node
   * @param siblingNodes List of current level tree nodes
   * @param newNode new tree node
   */
  private void insert2(List<Node> siblingNodes, Node newNode) {
    List<Node> nextSiblingNodes = new LinkedList<Node>();
    for (Node currentNode : siblingNodes) {
      if (currentNode.getLeft() == null) {
        currentNode.setLeft(newNode);
        return;
      } else if (currentNode.getRight() == null) {
        currentNode.setRight(newNode);
        return;
      }
      nextSiblingNodes.add(currentNode.getLeft());
      nextSiblingNodes.add(currentNode.getRight());
    }
    insert2(nextSiblingNodes, newNode);
  }

  /**
   * Deep first search pre-oder tree traversal
   * @return the pre-order tree nodes
   */
  public List<Node> preOrder() {
    return dlr(this.root, new LinkedList<Node>());
  }

  /**
   * Pre-order tree traversal
   * @param currentNode  current tree node
   * @param preOrderList current traversal result
   * @return current traversal result
   */
  private List<Node> dlr(Node currentNode, List<Node>preOrderList) {
    preOrderList.add(currentNode);
    if (currentNode.getLeft() != null) {
      dlr(currentNode.getLeft(), preOrderList);
    }
    if (currentNode.getRight() != null) {
      dlr(currentNode.getRight(), preOrderList);
    }
    return preOrderList;
  }

  /**
   * Deep first search in-oder tree traversal
   * @return the in-order tree nodes
   */
  public List<Node> inOrder() {
    return ldr(this.root, new LinkedList<Node>());
  }

  /**
   * In-order tree traversal
   * @param currentNode  current tree node
   * @param inOrderList current traversal result
   * @return current traversal result
   */
  private List<Node> ldr(Node currentNode, List<Node>inOrderList) {
    if (currentNode.getLeft() != null) {
      ldr(currentNode.getLeft(), inOrderList);
    }
    inOrderList.add(currentNode);
    if (currentNode.getRight() != null) {
      ldr(currentNode.getRight(), inOrderList);
    }
    return inOrderList;
  }

  /**
   * Deep first search post-oder tree traversal
   * @return the post-order tree nodes
   */
  public List<Node> postOrder() {
    return lrd(this.root, new LinkedList<Node>());
  }

  /**
   * Post-order tree traversal
   * @param currentNode  current tree node
   * @param postOrderList current traversal result
   * @return current traversal result
   */
  private List<Node> lrd(Node currentNode, List<Node>postOrderList) {
    if (currentNode.getLeft() != null) {
      lrd(currentNode.getLeft(), postOrderList);
    }
    if (currentNode.getRight() != null) {
      lrd(currentNode.getRight(), postOrderList);
    }
    postOrderList.add(currentNode);
    return postOrderList;
  }

  /**
   * Non-recursive method of pre-order traversal
   * @return the pre-order tree nodes
   */
  public List<Node> preOrder2() {
    List<Node> preOrderList = new LinkedList<Node>();
    Stack<Node> nodeStack = new Stack<Node>();
    nodeStack.push(this.root);

    while (!nodeStack.empty()) {
      Node n = nodeStack.pop();
      preOrderList.add(n);
      if (n.getRight() != null) nodeStack.push(n.getRight());
      if (n.getLeft() != null) nodeStack.push(n.getLeft());
    }
    return preOrderList;
  }

  /**
   * Non-recursive method of breadth first search traversal
   * @return the bfs tree nodes
   */
  public List<Node> bfs() {
    List<Node> bfsList = new LinkedList<Node>();
    Queue<Node> nodeQueue = new LinkedList<Node>();
    nodeQueue.add(this.root);

    while (nodeQueue.size() != 0) {
      Node n = nodeQueue.poll();
      bfsList.add(n);
      if (n.getLeft() != null) nodeQueue.add(n.getLeft());
      if (n.getRight() != null) nodeQueue.add(n.getRight());
    }
    return bfsList;
  }
}
