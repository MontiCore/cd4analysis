/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators.matcher;

import java.util.ArrayList;
import java.util.List;

public class MatcherData {
  protected final List<IStereoMatcher> stereoMatchers = new ArrayList<>();
  protected final List<ITagMatcher> tagMatchers = new ArrayList<>();
  protected final List<ICLIMatcher> cliMatchers = new ArrayList<>();

  protected MatchResult globalDefault = MatchResult.IGNORE;

  public MatcherData() {
  }

  public List<IStereoMatcher> getStereoMatchers() {
    return stereoMatchers;
  }

  public List<ITagMatcher> getTagMatchers() {
    return tagMatchers;
  }

  public List<ICLIMatcher> getCLIMatchers() {
    return cliMatchers;
  }

  public MatchResult getGlobalDefault() {
    return globalDefault;
  }

  public void setGlobalDefault(MatchResult globalDefault) {
    this.globalDefault = globalDefault;
  }
}
