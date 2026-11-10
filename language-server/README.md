# CD4Analysis and CD4Code language servers
This project contains language servers for CD4Analysis and CD4Code.

Build the executable servers with `gradle :language-server:packCD4AnalysisLanguageServer`
and `gradle :language-server:packCD4CodeLanguageServer`. The JARs are written to `target/libs`.

References for the CD4Analysis:
- https://github.com/MontiCore/cd4analysis
- https://mbse.se-rwth.de/book1/index.php?c=chapter2

You can customize the resulting LSP via the TOP mechanism.

# Running the servers
Use `gradle :language-server:runCD4AnalysisVscodePluginAttached -PbuildVscodePlugin=true`
or `gradle :language-server:runCD4CodeVscodePluginAttached -PbuildVscodePlugin=true` to run a
server with its VS Code plugin attached.
