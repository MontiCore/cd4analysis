${tc.signature()}
<#assign parameter = ast.getCDAttributeList()?filter(a -> a.getModifier().isPublic())>
${cd4c.constructor("public HelloWorldWithConstructor(" + parameter?map(a -> a.printType() + " " + a.getName())?join(", ") + ")")}
{
  <#list parameter as param>
    this.${param.getName()} = ${param.getName()};
  </#list>
}