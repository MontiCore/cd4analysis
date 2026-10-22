<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
<#assign attributeType = TypeCheck3.symTypeFromAST(attribute.getMCType())>

<#if MCCollectionSymTypeRelations.isOptional(attributeType)>
this.${attribute.getName()} = Optional.ofNullable(${attribute.getName()});
<#else>
this.${attribute.getName()} = ${attribute.getName()};
</#if>
return this.realBuilder;
