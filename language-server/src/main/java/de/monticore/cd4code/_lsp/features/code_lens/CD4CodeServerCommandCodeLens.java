/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp.features.code_lens;

import java.util.List;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Range;

public class CD4CodeServerCommandCodeLens extends CodeLens {
  
  public CD4CodeServerCommandCodeLens(Range range, String title, String command,
      List<Object> arguments) {
    super(range, new Command(title, command, arguments), null);
  }
  
}
