'use strict';

describe('Transfer Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockTransfer, MockBankAccount, MockTrade;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockTransfer = jasmine.createSpy('MockTransfer');
        MockBankAccount = jasmine.createSpy('MockBankAccount');
        MockTrade = jasmine.createSpy('MockTrade');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'Transfer': MockTransfer,
            'BankAccount': MockBankAccount,
            'Trade': MockTrade
        };
        createController = function() {
            $injector.get('$controller')("TransferDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'bankApp:transferUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
