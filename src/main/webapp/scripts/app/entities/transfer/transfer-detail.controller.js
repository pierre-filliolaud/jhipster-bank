'use strict';

angular.module('bankApp')
    .controller('TransferDetailController', function ($scope, $rootScope, $stateParams, entity, Transfer, BankAccount, Trade) {
        $scope.transfer = entity;
        $scope.load = function (id) {
            Transfer.get({id: id}, function(result) {
                $scope.transfer = result;
            });
        };
        var unsubscribe = $rootScope.$on('bankApp:transferUpdate', function(event, result) {
            $scope.transfer = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
