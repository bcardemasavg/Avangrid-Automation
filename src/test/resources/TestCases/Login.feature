@login
Feature: un usuario puede logear correctamente

   @TestCaseKey=AUTEST-01
  Scenario: Login correcto
    Given cargo la data "usuarios" con id "id usuario genercio"
    And ingreso a la aplicación web y navego a la url
