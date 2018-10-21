// functionality
function sdcIsOwnerFalse() {
  cy.visit(
    "home?userId=ym903w&userRole=DESIGNER&displayType=context&contextType=SERVICES&uu" +
      "id=06c7d927-4e2f-47e1-a29d-b6ed229ebc0a&lifecycleState=NOT_CERTIFIED_CHECKOUT&is" +
      "Owner=false&version=0.1"
  );
}

function sdcUserNotCheckout() {
  cy.visit(
    "home?userId=ym903w&userRole=DESIGNER&displayType=context&contextType=SERVICES&uu" +
      "id=06c7d927-4e2f-47e1-a29d-b6ed229ebc0a&lifecycleState=READY_FOR_CERTIFICATION&i" +
      "sOwner=true&version=0.1"
  );
}

function homePage() {
  cy.visit(
    "home?userId=ym903w&userRole=DESIGNER&displayType=context&contextType=SERVICES&uu" +
      "id=06c7d927-4e2f-47e1-a29d-b6ed229ebc0a&lifecycleState=NOT_CERTIFIED_CHECKOUT&is" +
      "Owner=true&version=0.1"
  );
}

function homePageCertified() {
  cy.visit(
    "home?userId=ym903w&userRole=DESIGNER&displayType=context&contextType=SERVICES&uu" +
      "id=06c7d927-4e2f-47e1-a29d-b6ed229ebc0a&lifecycleState=CERTIFIED_CHECKOUT&isOwne" +
      "r=true&version=0.1"
  );
}

function deleteMonitoringComponent() {
  cy
    .server()
    .route({
      method: "DELETE",
      url:
        Cypress.env("backendUrl") + "/SERVICES/**/**/**/deleteVfcmtReference",
      response: "{}"
    })
    .as("deleteMonitoringComponent");
}

function saveMonitoringComponent() {
  cy
    .server()
    .route({
      method: "POST",
      url: Cypress.env("backendUrl") + "/SERVICES/**/**/saveComposition/**",
      response: "{}"
    })
    .as("saveMonitoringComponent");
}

function submitMonitoringComponent() {
  cy
    .server()
    .route({
      method: "POST",
      url: Cypress.env("backendUrl") + "/SERVICES/createBluePrint/**/**/**/",
      response: "{}"
    })
    .as("submitMonitoringComponent");
}

function deleteMonitoringComponentWithBlueprint() {
  cy
    .server()
    .route({
      method: "DELETE",
      url:
        Cypress.env("backendUrl") + "/SERVICES/**/**/**/deleteVfcmtReference",
      response: "{}"
    })
    .as("deleteMonitoringComponentWithBlueprint");
}

function getMCList() {
  cy
    .server()
    .route({
      method: "GET",
      url: Cypress.env("backendUrl") + "/SERVICES/**/**/monitoringComponents",
      response: "fixture:McTable"
    })
    .as("mcList");
  // monitoringComponentsMock
}

function getMC() {
  cy
    .server()
    .route({
      method: "GET",
      url: Cypress.env("backendUrl") + "/getMC/**",
      response: "fixture:getMCMock"
    })
    .as("getMC");
}

function getMCListEmpty() {
  cy
    .server()
    .route({
      method: "GET",
      url: Cypress.env("backendUrl") + "/SERVICES/**/**/monitoringComponents",
      response: "{}"
    })
    .as("mcListEmpty");
}

function getTemplateApiError() {
  cy.server().route({
    method: "GET",
    url:
      Cypress.env("backendUrl") +
      "/service/06c7d927-4e2f-47e1-a29d-b6ed229ebc0a",
    status: 500,
    response: {
      requestError: {
        policyException: {
          messageId: "POL5000",
          text: "Error: Internal Server Error. Please try again later.",
          variables: [],
          formattedErrorMessage:
            "Error: Internal Server Error. Please try again later."
        }
      },
      notes: 'Error: Requested "123" resource was not found.'
    }
  });
  cy.homePage();
  cy.get('button[data-tests-id="btn-create-mc"]').click();
}

// Add cypress commands
Cypress.Commands.add("homePage", homePage);
Cypress.Commands.add("getTemplateApiError", getTemplateApiError);
Cypress.Commands.add("homePageCertified", homePageCertified);
Cypress.Commands.add("sdcIsOwnerFalse", sdcIsOwnerFalse);
Cypress.Commands.add("sdcUserNotCheckout", sdcUserNotCheckout);
Cypress.Commands.add("getMCList", getMCList);
Cypress.Commands.add("getMCListEmpty", getMCListEmpty);
Cypress.Commands.add("deleteMonitoringComponent", deleteMonitoringComponent);
Cypress.Commands.add(
  "deleteMonitoringComponentWithBlueprint",
  deleteMonitoringComponentWithBlueprint
);
Cypress.Commands.add("submitMonitoringComponent", submitMonitoringComponent);
Cypress.Commands.add("saveMonitoringComponent", saveMonitoringComponent);
Cypress.Commands.add("getMC", getMC);
