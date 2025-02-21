<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "withCheck")}

<#if withCheck>
if(!isValid()){
  throw new IllegalStateException();
}
</#if>
var v = new ${clazz}();
${defineHookPoint("methods.builder.build.build:Inner")}
return v;
