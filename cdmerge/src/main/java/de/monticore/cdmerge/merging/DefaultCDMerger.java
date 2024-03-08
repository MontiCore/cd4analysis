/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging;

import de.monticore.ast.Comment;
import de.monticore.cd4code._ast.ASTCD4CodeNode;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.merging.strategies.AssociationMerger;
import de.monticore.cdmerge.merging.strategies.TypeMerger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultCDMerger extends CDMerger {

  public DefaultCDMerger(
      MergeBlackBoard blackboard, TypeMerger typeMerger, AssociationMerger associationMerger) {
    super(blackboard, typeMerger, associationMerger);
  }

  @Override
  public void mergeComments(ASTCD4CodeNode left, ASTCD4CodeNode right, ASTCD4CodeNode merged) {
    // Clean up anything from possibly cloned AST Nodes
    merged.get_PostCommentList().clear();
    merged.get_PreCommentList().clear();
    // We always assume, that the comments are only prior to an element
    Set<Comment> comments = new HashSet<Comment>();
    comments.addAll(left.get_PreCommentList());
    comments.addAll(right.get_PreCommentList());
    merged.addAll_PreComments(comments);
  }

  @Override
  public void mergeComments(ASTCDBasisNode left, ASTCDBasisNode right, ASTCDBasisNode merged) {
    // Clean up anything from possibly cloned AST Nodes
    merged.clear_PreComments();
    merged.clear_PostComments();
    Set<Comment> comments = new HashSet<Comment>();

    // merge PreComments
    List<Comment> preComments = left.get_PreCommentList();
    if (preComments == null) {
      preComments = new ArrayList<Comment>();
    }
    // Add all different comments
    if (right.get_PreCommentList() != null && right.get_PreCommentList().size() > 0) {
      boolean newComment = true;
      for (Comment cR : right.get_PreCommentList()) {
        for (Comment cL : comments) {
          if (cL.getText().equalsIgnoreCase(cR.getText())) {
            newComment = false;
            break;
          }
        }
        if (newComment) {
          comments.add(cR);
        }
      }
    }
    merged.addAll_PreComments(preComments);

    // merge PostComments
    List<Comment> postComments = left.get_PostCommentList();
    if (postComments == null) {
      postComments = new ArrayList<Comment>();
    }
    comments.addAll(postComments);
    if (right.get_PostCommentList() != null && right.get_PostCommentList().size() > 0) {

      boolean newComment = true;
      for (Comment cR : right.get_PostCommentList()) {
        for (Comment cL : comments) {
          if (cL.getText().equalsIgnoreCase(cR.getText())) {
            newComment = false;
            break;
          }
        }
        if (newComment) {
          comments.add(cR);
        }
      }
    }
    merged.addAll_PostComments(postComments);
  }
}
