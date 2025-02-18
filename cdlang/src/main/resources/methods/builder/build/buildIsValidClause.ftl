<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "errorCode")}

if (this.${attribute.getName()} == null) {
  Log.error("${errorCode}");
}
