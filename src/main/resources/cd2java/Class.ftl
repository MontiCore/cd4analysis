<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdClass")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
/* (c) https://github.com/MontiCore/monticore */

${defineHookPoint("ClassContent:addComment")}

${tc.include("cd2java.Package")}

${tc.include("cd2java.Imports")}
import de.monticd2java.ast.ASTCNode;

<#-- Imports hook -->
${defineHookPoint("ClassContent:Imports")}

${tc.include("cd2java.Annotations")}

<#-- Annotations hook -->
${defineHookPoint("ClassContent:Annotations")}

${cdPrinter.printSimpleModifier(cdClass.getModifier())} class ${cdClass.getName()} <#rt><#lt>
<#if cdClass.isPresentCDExtendUsage()>extends ${cdPrinter.printType(cdClass.getCDExtendUsage().getSuperclass(0))} </#if> <#rt><#lt>
<#if cdClass.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(cdClass.getCDInterfaceUsage().getInterfaceList())} </#if>{

<#list cdClass.getCDAttributeList() as attribute>
    ${tc.include("cd2java.Attribute", attribute)}
</#list>

<#list cdClass.getCDConstructorList() as constructor>
    ${tc.include("cd2java.Constructor", constructor)}
</#list>

<#list cdClass.getCDMethodList() as method>
    ${tc.include("cd2java.Method", method)}
</#list>

<#-- Elements HOOK -->
${defineHookPoint("ClassContent:Elements")}
}
