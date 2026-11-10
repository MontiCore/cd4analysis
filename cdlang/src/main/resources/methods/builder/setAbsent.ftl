<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
<#assign attributeType = TypeCheck3.symTypeFromAST(attribute.getMCType())>

<#if MCCollectionSymTypeRelations.isList(attributeType)>
this.${attribute.name} = new ArrayList<>();
  <#else>
  <#if MCCollectionSymTypeRelations.isSet(attributeType)>
this.${attribute.name} = new LinkedHashSet<>();
    <#else>
    <#if MCCollectionSymTypeRelations.isOptional(attributeType)>
this.${attribute.name} = Optional.empty();
    </#if>
  </#if>
</#if>
return this.realBuilder;


