// describe('home screen', () => {
//     beforeEach(() => {
//         cy.visit('http://localhost:9000/#/dcae/home');
//     });

//     it('should verify create new asset button', () => {
//         cy.get('[data-tests-id=AddButtonsArea]').should('be.visible');
//     });

//     it('should get vfcmt from server', () => {
//         cy.server()
//             .route('GET', 'getResourcesByCategory', [{
//                 category: "Template",
//                 invariantUUID: "6cab2212-24c0-424f-8b67-e7ea48e005f1",
//                 lastUpdaterUserId: "is8413",
//                 lifecycleState: "NOT_CERTIFIED_CHECKOUT",
//                 name: "Igortest1",
//                 resourceType: "VFCMT",
//                 subCategory: "Monitoring Template",
//                 toscaModelURL: "/sdc/v1/catalog/resources/29161df9-f550-41be-9d7a-750eb72b39f1/",
//                 uuid: "29161df9-f550-41be-9d7a-750eb72b39f1",
//                 version: "0.1"
//             }])
//             .get('.dcae-dashboard-card').should('be.greaterThan', '1');
//     });
// })
