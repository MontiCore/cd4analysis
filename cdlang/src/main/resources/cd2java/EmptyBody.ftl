<#-- (c) https://github.com/MontiCore/monticore -->
<#if ast.isPresentSymbol() && !ast.getSymbol().getType().isVoidType()>
  throw new UnsupportedOperationException();
</#if>
