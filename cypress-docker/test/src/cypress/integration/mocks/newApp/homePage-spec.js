export const buttonCreateMC = () => {
  return cy.get('button[data-tests-id="btn-create-mc"]');
};
export const buttonCreateMCSpan = () => {
  return cy.get('[data-tests-id="btn-span-create-mc"]');
};

export const tableItems = () => {
  return cy.get("datatable-row-wrapper");
};

export const monitoringComponentTable = () => {
  return cy.get('ngx-datatable[data-tests-id="monitoringComponentTable"]');
};
export const tableItemsDeleteButton = () => {
  return cy.get('button[data-tests-id="tableItemsButtonDelete"]');
};

export const tableItemsInfoButton = () => {
  return cy.get('button[data-tests-id="tableItemsButtonInfo"]');
};

export const popupGetDeleteBtn = () => {
  return cy.get('button[data-tests-id="btnDelete"]');
};

export const popupGetCancelBtn = () => {
  return cy.get('button[data-tests-id="btnCancel"]');
};

export const getMonitoringConfiguration = () => {
  return cy.get('div[data-tests-id="editMC"]');
};

export const doHoverOverFirstLine = () => {
  return tableItems()
    .first()
    .trigger("mouseover");
};

export const doHoverOverFirstLineMonitoringConfiguration = () => {
  tableItems()
    .first()
    .trigger("mouseover");
  return getMonitoringConfiguration();
};

const NUMBER_OF_ITEMS = 9;

const navigateButtonDisabled = () => {
  return buttonCreateMC()
    .should("be.visible")
    .and("be.disabled")
    .get('button[data-tests-id="btn-fab-create-mc"]')
    .should("be.visible")
    .and("be.disabled");
};

describe("Home Page - E2E test flow with mock", () => {
  describe("MC List empty", () => {
    beforeEach(() => {
      cy.getMCListEmpty();
      cy.homePage();
    });

    it("Shouldn't have create table with headers", () => {
      monitoringComponentTable().should("not.be.visible");
    });
    it("Shouldn't have create table with items", () => {
      buttonCreateMC()
        .get('div[data-tests-id="new-monitoring-title"]')
        .should("contain", "Monitoring");
    });
  });

  describe("Check Edit Save and Submit", () => {
    beforeEach(() => {
      cy.getMCList();
      cy.homePage();
      cy.getMC();
      cy.submitMonitoringComponent();
      cy.saveMonitoringComponent();
    });

    it("Edit VFCMT icon visablity", () => {
      doHoverOverFirstLine();
      cy.get('[data-tests-id="editMC"').should("be.visible");
    });
  });

  describe("Revert Mc table item", () => {
    beforeEach(() => {
      cy.getMCList();
      cy.homePage();
      cy.getMC();
      cy.submitMonitoringComponent();
      cy.saveMonitoringComponent();
    });

    it("should not see revert icon on version that is lower then 1.x", () => {
      tableItems()
        .first()
        .trigger("mouseover");
      cy.get('[data-tests-id="revertMC"').should("not.be.visible");
    });

    it("should get revert icon on version that is above then 1.x", () => {
      tableItems()
        .last()
        .click()
        .trigger("mouseover");
      cy.get('[data-tests-id="revertMC"]').should("be.visible");
    });
    it("should get revert open model", () => {
      tableItems()
        .last()
        .click()
        .trigger("mouseover");
      cy
        .get('[data-tests-id="revertMC"]')
        .should("be.visible")
        .click({ force: true })
        .get('[data-tests-id="revert-dialog-title"]')
        .should("contain", "Are you sure you want to revert?");
    });

    it("should get revert open model clicking on cancel should close popup", () => {
      tableItems()
        .last()
        .click()
        .trigger("mouseover");
      cy
        .get('[data-tests-id="revertMC"]')
        .should("be.visible")
        .click({ force: true })
        .get('[data-tests-id="revert-cancel"]')
        .click()
        .get('[data-tests-id="revert-dialog-title"]')
        .should("not.be.visible");
    });
  });

  describe("View only", () => {
    beforeEach(() => {
      cy.getMCList();
      cy.homePage();
      cy.getMC();
      cy.submitMonitoringComponent();
      cy.saveMonitoringComponent();
    });

    it("should not see view Submitted icon in 0.x version", () => {
      tableItems()
        .first()
        .click()
        .trigger("mouseover");
      cy.get('[data-tests-id="viewSubmitted"]').should("not.be.visible");
    });
    it("should see view Submitted icon in 1.x version", () => {
      tableItems()
        .last()
        .click()
        .trigger("mouseover");
      cy.get('[data-tests-id="viewSubmitted"]').should("be.visible");
    });
    it("should see view Submitted status in mc data page", () => {
      tableItems()
        .last()
        .click()
        .trigger("mouseover");
      cy
        .get('[data-tests-id="viewSubmitted"]')
        .should("be.visible")
        .click()
        .get('[data-tests-id="viewOnlyLabel"]')
        .should("contain", "view only");
    });
    it("save and submit buttons need to be disabled on view only mode", () => {
      tableItems()
        .last()
        .click()
        .trigger("mouseover");
      cy
        .get('[data-tests-id="viewSubmitted"]')
        .should("be.visible")
        .click()
        .get('[data-tests-id="save-btn"]')
        .should("be.disabled")
        .get('[data-tests-id="submit-btn"]')
        .should("be.disabled");
    });
    it("setting inputs need to be disabled on view only mode", () => {
      tableItems()
        .last()
        .click()
        .trigger("mouseover");
      cy
        .get('[data-tests-id="viewSubmitted"]')
        .should("be.visible")
        .click()
        .get("#ui-tabpanel-1-label")
        .should("be.visible")
        .click()
        .get('[data-tests-id="setting-gear"]')
        .first()
        .click()
        .get(".field-text")
        .first()
        .should("be.disabled");
    });
  });

  describe("MC List", () => {
    beforeEach(() => {
      cy.getMCList();
      cy.homePage();
    });

    it("Should have create button on top of the screen", () => {
      buttonCreateMC().should("be.visible");
    });

    it("Should have create table with headers", () => {
      monitoringComponentTable().should("be.visible");
    });
    it("Should have create table with items", () => {
      tableItems().should("have.length", NUMBER_OF_ITEMS);
    });
  });

  describe("MC List Delete Tests", () => {
    beforeEach(() => {
      cy.getMCList();
      cy.homePage();
      cy.deleteMonitoringComponent();
      cy.deleteMonitoringComponentWithBlueprint();
    });
    it("Mouse hover over item, delete is visible, info not visible", () => {
      doHoverOverFirstLine();
      tableItemsDeleteButton().should("be.visible");
      tableItemsInfoButton().should("not.be.visible");
    });

    it(
      "Mouse hover over item, call delete and remove not submitted (call delete without" +
        " blueprint api)",
      () => {
        tableItems().should("have.length", NUMBER_OF_ITEMS);
        doHoverOverFirstLine();
        tableItemsDeleteButton()
          .should("be.visible")
          .click({ force: true });
        popupGetDeleteBtn().click({ force: true });
        tableItems().should("have.length", NUMBER_OF_ITEMS - 1);
      }
    );
    it(
      "Mouse hover over item, call delete and remove submitted (call delete with bluepr" +
        "int api)",
      () => {
        tableItems()
          .should("have.length", NUMBER_OF_ITEMS)
          .last()
          .trigger("mouseover");
        tableItemsDeleteButton()
          .should("be.visible")
          .click({ force: true });
        popupGetDeleteBtn().click({ force: true });
        tableItems().should("have.length", NUMBER_OF_ITEMS - 1);
      }
    );
    it("Mouse hover over item, call delete and cancelOperation", () => {
      tableItems().should("have.length", NUMBER_OF_ITEMS);
      doHoverOverFirstLine();
      tableItemsDeleteButton()
        .should("be.visible")
        .click({ force: true });
      popupGetCancelBtn().click({ force: true });
      tableItems().should("have.length", NUMBER_OF_ITEMS);
    });
  });

  // describe("Show Info icon", () => {
  //   beforeEach(() => {
  //     cy.getMCList();
  //     cy.homePageCertified();
  //   });
  //   it("Mouse hover over item, delete is not visible, info visible", () => {
  //     doHoverOverFirstLine();
  //     tableItemsInfoButton().should("be.visible");
  //     tableItemsDeleteButton().should("not.be.visible");
  //   });
  // });

  describe("Successfully Entry Home Page Monitoring Configuration", () => {
    beforeEach(() => {
      cy.httpGetDDLData();
      cy.getMCListEmpty();
      cy.homePage();
    });

    it("Buttons looks Assertion", () => {
      buttonCreateMC()
        .should("contain", "Create New MC")
        .and("be.visible")
        .and("not.be.disabled");

      cy
        .get('[data-tests-id="btn-span-create-mc"]')
        .should("contain", "Add First MC")
        .and("be.visible")
        .and("not.be.disabled");
    });

    it("Buttons Functionality Assertion", () => {
      buttonCreateMC()
        .click()
        .get('div[data-tests-id="new-monitorying-titie"]')
        .should("contain", "Monitoring");
    });
  });

  describe("Not Auth Entry Home Page Monitoring Configuration", () => {
    beforeEach(() => {
      cy.httpGetDDLData();
      cy.getMCListEmpty();
    });

    it("Buttons disabled when user not owner", () => {
      cy.sdcIsOwnerFalse();
      navigateButtonDisabled();
    });

    it("Buttons disabled when user not checkout", () => {
      cy.sdcUserNotCheckout();
      navigateButtonDisabled();
    });
  });
});
