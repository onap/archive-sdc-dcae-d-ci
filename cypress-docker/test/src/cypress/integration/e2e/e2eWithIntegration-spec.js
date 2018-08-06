// const randomNodeName = () => Math     .random()     .toString(36)
// .substr(2, 7); const NODE_NAME = randomNodeName(); const serverGetDDLData =
// () => {     return cy         .server()         .route('GET',
// Cypress.env('backendUrl') + '/getResourcesByMonitoringTemplateCategory')
//    .as('templateAPi')         .route('GET', Cypress.env('backendUrl') +
// '/service/**')         .as('vfniListAPi'); }; const saveAndDoneHttp = () => {
//     cy.server();     cy.route({         method: 'POST',         url:
// Cypress.env('backendUrl') + '/rule-editor/rule/**/**/**/**'
// }).as('doneSaveCopyRule'); }; const createNewMC = () => {     cy
// .get('input[data-tests-id="nameMc"]')         .type(`Hello${NODE_NAME}`)
//    .get('textarea[data-tests-id="descMc"]')         .type('Hello
// Description')         .get('select[data-tests-id="templateDdl"]')
// .then($els => {             const opt = $els.find('option');
// const first = opt.get(1);             return $els.val(first.value);
// })         .trigger('change')         .get('select[data-tests-id="vfniDdl"]')
//         .then($els => {             const opt = $els.find('option');
//    const first = opt.get(1);             return $els.val(first.value);
//  })         .trigger('change')
// .get('button[data-tests-id="createMonitoring"]')         .not('[disabled]')
//       .should('not.contain', 'Disabled');     cy         .server()
// .route({             method: 'POST',             url:
// Cypress.env('backendUrl') + '/createMC'         })         .as('newMC')
//   .get('button[data-tests-id="createMonitoring"]')         .click()
// .wait('@newMC'); }; import {buttonCreateMC} from
// '../mocks/newApp/homePage-spec'; import {selectVersionAndTypeAndAddFirstRule,
// fillRuleDescription, addCopyAction, editFirstRule} from
// '../mocks/newApp/ruleEngine-spec'; //
// https://zldcrdm2sdc4cfe01.3f1a87.rdm2.tci.att.com:9444/dcaed/#/
// describe('DCAED - forntend e2e and inagration test ', () => {
// context('Empty Monitoring Configuration list for service ', () => {
// it(' Loads ', () => {
// cy.visit('home?userId=ym903w&userRole=DESIGNER&displayType=context&contextType
// =SERVICE&uui' +
// 'd=a94a0c90-5549-4c3a-a5fd-b122cac085cd&lifecycleState=NOT_CERTIFIED_CHECKOUT&
// isO' +                     'wner=true&
// version=0.2&parentUrl=https://www.e-access.att.com&eventsClientId=DCA' +
//                'ED');         });     });     context('Create new monitoring
// configuration', () => {         beforeEach(() => {
// serverGetDDLData();
// cy.visit('home?userId=ym903w&userRole=DESIGNER&displayType=context&contextType
// =SERVICE&uui' +
// 'd=a94a0c90-5549-4c3a-a5fd-b122cac085cd&lifecycleState=NOT_CERTIFIED_CHECKOUT&
// isO' +                     'wner=true&
// version=0.2&parentUrl=https://www.e-access.att.com&eventsClientId=DCA' +
//                'ED');             buttonCreateMC()                 .click()
//               .wait(['@templateAPi', '@vfniListAPi']);         });
// it('After api call success verify create button is disabled', () => {
//     cy                 .get('button[data-tests-id="createMonitoring"]')
//           .should('be.visible')                 .and('be.disabled');
// });         it('click on create mc - more then one tab should be visible', ()
// => {             createNewMC();             cy
// .get('ul[p-tabviewnav]')                 .children()
// .should($el => {                     expect($el.length)
//   .to                         .be                         .greaterThan(1);
//              });         });         it('should enter rule engine in map tab
// and create new rule', () => {             createNewMC();             cy
//           .get('#ui-tabpanel-1-label')                 .should('contain',
// 'map')                 .click();
// selectVersionAndTypeAndAddFirstRule();
// fillRuleDescription('newRule');             addCopyAction();
// saveAndDoneHttp();             cy
// .get('button[data-tests-id="btnDone"]')                 .click();
// cy                 .wait('@doneSaveCopyRule')
// .get('div[data-tests-id="ruleElement"]')
// .should('be.visible')                 .then(function ($lis) {
//     expect($lis)                         .to                         .have
//                      .length(1);                     expect($lis.eq(0))
//                   .to                         .contain('newRule');
//      });             editFirstRule();
// fillRuleDescription('LiavRule');             saveAndDoneHttp();
// cy                 .get('button[data-tests-id="btnSave"]')
// .click()                 .wait('@doneSaveCopyRule')
// .get('a[data-tests-id="btnBackRule"]')                 .click()
//   .get('div[data-tests-id="ruleElement"]')
// .should('be.visible')                 .then(function ($lis) {
//     expect($lis)                         .to                         .have
//                      .length(1);                     expect($lis.eq(0))
//                   .to                         .contain('LiavRule');
//       });         });     }); });
