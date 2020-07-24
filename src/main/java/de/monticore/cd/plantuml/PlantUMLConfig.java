/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.plantuml;

public class PlantUMLConfig {
  protected Boolean showAtt = false;
  protected Boolean showAssoc = false;
  protected Boolean showRoles = false;
  protected Boolean showCard = false;
  protected Boolean showModifier = false;
  protected int nodesep = -1;
  protected int ranksep = -1;
  protected Boolean ortho = false;
  protected Boolean shortenWords = false;
  protected Boolean showComments = false;

  public PlantUMLConfig() {
  }

  public PlantUMLConfig(Boolean showAtt, Boolean showAssoc, Boolean showRoles, Boolean showCard, Boolean showModifier, int nodesep, int ranksep, Boolean ortho, Boolean shortenWords, Boolean showComments) {
    this.showAtt = showAtt;
    this.showAssoc = showAssoc;
    this.showRoles = showRoles;
    this.showCard = showCard;
    this.showModifier = showModifier;
    this.nodesep = nodesep;
    this.ranksep = ranksep;
    this.ortho = ortho;
    this.shortenWords = shortenWords;
    this.showComments = showComments;
  }

  public PlantUMLConfig(Boolean showAtt, Boolean showAssoc, Boolean showRoles, Boolean showCard, Boolean showModifier) {
    this.showAtt = showAtt;
    this.showAssoc = showAssoc;
    this.showRoles = showRoles;
    this.showCard = showCard;
    this.showModifier = showModifier;
  }

  public Boolean getShowAtt() {
    return showAtt;
  }

  public void setShowAtt(Boolean showAtt) {
    this.showAtt = showAtt;
  }

  public Boolean getShowAssoc() {
    return showAssoc;
  }

  public void setShowAssoc(Boolean showAssoc) {
    this.showAssoc = showAssoc;
  }

  public Boolean getShowRoles() {
    return showRoles;
  }

  public void setShowRoles(Boolean showRoles) {
    this.showRoles = showRoles;
  }

  public Boolean getShowCard() {
    return showCard;
  }

  public void setShowCard(Boolean showCard) {
    this.showCard = showCard;
  }

  public Boolean getShowModifier() {
    return showModifier;
  }

  public void setShowModifier(Boolean showModifier) {
    this.showModifier = showModifier;
  }

  public int getNodesep() {
    return nodesep;
  }

  public void setNodesep(int nodesep) {
    this.nodesep = nodesep;
  }

  public int getRanksep() {
    return ranksep;
  }

  public void setRanksep(int ranksep) {
    this.ranksep = ranksep;
  }

  public Boolean getOrtho() {
    return ortho;
  }

  public void setOrtho(Boolean ortho) {
    this.ortho = ortho;
  }

  public Boolean getShortenWords() {
    return shortenWords;
  }

  public void setShortenWords(Boolean shortenWords) {
    this.shortenWords = shortenWords;
  }

  public Boolean getShowComments() {
    return showComments;
  }

  public void setShowComments(Boolean showComments) {
    this.showComments = showComments;
  }
}
