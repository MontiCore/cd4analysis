package de.monticore.cd4analysis._lsp.features.syntax_highlighting;

import de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule.*;
import de.monticore.cd4analysis._lsp.language_access.CD4AnalysisLanguageAccess;

public class CD4AnalysisLexerProvider extends CD4AnalysisLexerProviderTOP {

  public CD4AnalysisLexerProvider(CD4AnalysisLanguageAccess languageAccess) {
    super(languageAccess);
    addClassificationRule(new HighlightClassNameRule());
    addClassificationRule(new HighlightInterfaceNameRule());
    addClassificationRule(new HighlightAttributeTypeRule());
    addClassificationRule(new HighlightPackageNameRule());
    addClassificationRule(new HighlightEnumNameRule());
    addClassificationRule(new HighlightEnumMemberNameRule());
  }
}
