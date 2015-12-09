'use strict';

describe('Trade Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockTrade, MockTransfer;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockTrade = jasmine.createSpy('MockTrade');
        MockTransfer = jasmine.createSpy('MockTransfer');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'Trade': MockTrade,
            'Transfer': MockTransfer
        };
        createController = function() {
            $injector.get('$controller')("TradeDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'bankApp:tradeUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
