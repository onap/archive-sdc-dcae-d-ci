function getListOfRuleEngine(targetFiled) {
  cy.server();
  cy
    .route({
      method: "GET",
      url: `${Cypress.env(
        "backendUrl"
      )}/rule-editor/rule/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1517823219961.0/${targetFiled}`,
      response: "fixture:loadAllActionsForRuleEngine"
    })
    .as("serverWithRuleList");
}

function emptyRuleEngine(targetFiled) {
  cy.server();
  cy
    .route({
      method: "GET",
      url: `${Cypress.env(
        "backendUrl"
      )}/rule-editor/rule/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1517823219961.0/${targetFiled}`,
      response: {}
    })
    .as("serverRuleList")
    .route({
      method: "GET",
      url: Cypress.env("backendUrl") + "/rule-editor/list-events-by-versions",
      response: "fixture:list-events-by-version"
    })
    .as("serverEventsAndVersion");
}

function getLatestMcUuid() {
  cy.server();
  cy
    .route({
      method: "GET",
      url:
        Cypress.env("backendUrl") +
        "/SERVICES/06c7d927-4e2f-47e1-a29d-b6ed229ebc0a/LiavSrv/6d436c07-8006-4335-8c84-d" +
        "65b4740f8d6/getLatestMcUuid",
      response: {
        uuid: "6d436c07-8006-4335-8c84-d65b4740f8d6"
      }
    })
    .as("getLatestMcUuid");
}

function httpTargetTree() {
  cy.server();
  cy
    .route({
      method: "GET",
      url:
        Cypress.env("backendUrl") + "/rule-editor/definition/4.1/syslogFields",
      response: "fixture:syslogTargetTree"
    })
    .as("targetData");
}

function getPhases() {
  cy.server();
  cy
    .route({
      method: "GET",
      url: Cypress.env("backendUrl") + "/conf/getPhases/default",
      response: "fixture:getPhases"
    })
    .as("phase");
}

function doneSaveRule() {
  cy.server();
  cy
    .route({
      method: "POST",
      url:
        Cypress.env("backendUrl") +
        "/rule-editor/rule/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1517823219961.0/Typ" +
        "e1",
      response: "fixture:doneSaveSimpleCopy"
    })
    .as("doneSaveCopyRule");
}

function doneSaveCopyRule() {
  cy.server();
  cy
    .route({
      method: "POST",
      url:
        Cypress.env("backendUrl") +
        "/rule-editor/rule/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1517823219961.0/Typ" +
        "e1",
      response: "fixture:doneSaveSimpleEditCopy"
    })
    .as("doneSaveCopyRule");
}

function deleteRule() {
  cy.server();
  cy
    .route({
      method: "DELETE",
      url:
        Cypress.env("backendUrl") +
        "/rule-editor/rule/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1517823219961.0/Typ" +
        "e1/f620724b-7170-43e1-8a8b-55e98cabe658",
      response: "{}"
    })
    .as("deleteCopyRule");
}

function doneSaveAndEditRule() {
  cy.server();
  cy
    .route({
      method: "POST",
      url:
        Cypress.env("backendUrl") +
        "/rule-editor/rule/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1517823219961.0/Typ" +
        "e1",
      response: "fixture:doneSaveEditSimpleCopy"
    })
    .as("doneSaveAndEditRule");
}

function doneSaveMapRule() {
  cy.server();
  cy
    .route({
      method: "POST",
      url:
        Cypress.env("backendUrl") +
        "/rule-editor/rule/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1517823219961.0/Typ" +
        "e1",
      response: "fixture:doneSaveSimpleMap"
    })
    .as("doneSaveMapRule");
}

function doneSaveLogTextRule() {
  cy.server();
  cy
    .route({
      method: "POST",
      url:
        Cypress.env("backendUrl") +
        "/rule-editor/rule/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1517823219961.0/Typ" +
        "e1",
      response: "fixture:doneSaveSimpleLogText"
    })
    .as("doneSaveLogTextRule");
}

function httpTransalte() {
  cy.server();
  cy
    .route({
      method: "POST",
      url: Cypress.env("backendUrl") + "/rule-editor/rule/translate",
      response: "fixture:TranslateSimpleCopy"
    })
    .as("TranslateSimpleCopy");
}

function httpGenerateMappingRulesFileName() {
  cy.server();
  cy
    .route({
      method: "GET",
      url:
        Cypress.env("backendUrl") +
        "/rule-editor/getExistingRuleTargets/6d436c07-8006-4335-8c84-d65b4740f8d6/map/n.1" +
        "517823219961.0",
      response: ["Type1", "json", "name"]
    })
    .as("TranslateSimpleCopy");
}

// Add cypress commands
Cypress.Commands.add("emptyRuleEngine", emptyRuleEngine);
Cypress.Commands.add("getListOfRuleEngine", getListOfRuleEngine);
Cypress.Commands.add("getLatestMcUuid", getLatestMcUuid);
Cypress.Commands.add("httpTargetTree", httpTargetTree);
Cypress.Commands.add("doneSaveRule", doneSaveRule);
Cypress.Commands.add("doneSaveCopyRule", doneSaveCopyRule);
Cypress.Commands.add("doneSaveAndEditRule", doneSaveAndEditRule);
Cypress.Commands.add("doneSaveMapRule", doneSaveMapRule);
Cypress.Commands.add("doneSaveLogTextRule", doneSaveLogTextRule);
Cypress.Commands.add("deleteRule", deleteRule);
Cypress.Commands.add("getPhases", getPhases);
Cypress.Commands.add("httpTransalte", httpTransalte);
Cypress.Commands.add(
  "httpGenerateMappingRulesFileName",
  httpGenerateMappingRulesFileName
);
