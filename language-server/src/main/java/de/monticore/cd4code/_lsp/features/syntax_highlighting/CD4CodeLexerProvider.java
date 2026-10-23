/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp.features.syntax_highlighting;

import de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule.HighlightAttributeTypeRule;
import de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule.HighlightClassNameRule;
import de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule.HighlightEnumMemberNameRule;
import de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule.HighlightEnumNameRule;
import de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule.HighlightInterfaceNameRule;
import de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule.HighlightPackageNameRule;
import de.monticore.cd4code._lsp.language_access.CD4CodeLanguageAccess;

public class CD4CodeLexerProvider extends CD4CodeLexerProviderTOP {
  
  public CD4CodeLexerProvider(CD4CodeLanguageAccess languageAccess) {
    super(languageAccess);
    addClassificationRule(new HighlightClassNameRule());
    addClassificationRule(new HighlightInterfaceNameRule());
    addClassificationRule(new HighlightAttributeTypeRule());
    addClassificationRule(new HighlightPackageNameRule());
    addClassificationRule(new HighlightEnumNameRule());
    addClassificationRule(new HighlightEnumMemberNameRule());
  }
  
}
