<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "printer")}
${printer.prettyprint(ast.getModifier())} ${printer.prettyprint(ast.getMCType())} ${ast.getName()}<#rt>
<#if ast.isPresentInitial()>
    <#t> ${printer.prettyprint(ast.getInitial())}
</#if>
<#lt>;
