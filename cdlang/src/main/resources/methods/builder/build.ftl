<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz")}

var v = new ${clazz}();

${defineHookPoint("methods.builder.build:Inner")}

return v;

