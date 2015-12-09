'use strict';

angular.module('bankApp')
	.controller('TradeDeleteController', function($scope, $modalInstance, entity, Trade) {

        $scope.trade = entity;
        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Trade.delete({id: id},
                function () {
                    $modalInstance.close(true);
                });
        };

    });