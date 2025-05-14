@login
Feature: un usuario puede logear correctamente

  @TestCaseKey=SC001
  Scenario: Navigate to Url Lqat
    Given Enter the web application and navigate to the URL

  @TestCaseKey=SC002
  Scenario: User logs in with incorrect credentials
    Given a user with incorrect username and password
    When the user tries to log in on the app
    Then the system displays an incorrect user message

  @TestCaseKey=SC003
  Scenario: User logs in with correct credentials
    Given a user with correct username and password
    When the user logs in on the app
    Then the system allows the user to log in

  @TestCaseKey=SC004
  Scenario: Navigate to Url in web
    Given Enter the web application and navigate to the URL

  @TestCaseKey=SC005
  Scenario: User navigates to the side menu
    Given a user with correct username and password
    When the user logs in on the app
    And the system allows the user to log in
    Then the system displays the following tabs:
      | My account |
      | Manage your preferences |
      | View bills and payments |
      | Start another service |
      | Move to a new house |
      | Book an appointment |
      | Stop service |
      | View personal details |
      | Log out |

