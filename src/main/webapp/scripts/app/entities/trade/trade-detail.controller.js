'use strict';

angular.module('bankApp')
    .controller('TradeDetailController', function ($scope, $rootScope, $stateParams, entity, Trade, Transfer) {
        $scope.trade = entity;
        $scope.load = function (id) {
            Trade.get({id: id}, function(result) {
                $scope.trade = result;
            });
        };
        var unsubscribe = $rootScope.$on('bankApp:tradeUpdate', function(event, result) {
            $scope.trade = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
