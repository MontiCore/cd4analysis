<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzName", "attributes")}

<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>

var v = new ${originalClazzName}();

<#list attributes as attribute>
<#if MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())>
v.set${attribute.getName()?cap_first}(this.${attribute.getName()});
<#------------------------------------>
<#else>
  <#if MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())>
v.add${attribute.getName()?cap_first}(this.${attribute.getName()});
<#------------------------------------>
  <#else>
    <#if MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())>
v.add${attribute.getName()?cap_first}(this.${attribute.getName()});
<#------------------------------------>
    <#else>
      <#if MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())>
if(this.${attribute.getName()}.isPresent()){
  v.set${attribute.getName()?cap_first}(this.${attribute.getName()}.get());
}
<#------------------------------------>
      <#else>
v.set${attribute.getName()?cap_first}(this.${attribute.getName()});
<#------------------------------------>
      </#if>
    </#if>
  </#if>
</#if>
</#list>
${defineHookPoint("methods.builder.unsafeBuild:Inner")}
return v;
