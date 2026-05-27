<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("getter", "clazzname")}
for (var elem : node.${getter}()) {
  elem.accept((${clazzname})this);
}
