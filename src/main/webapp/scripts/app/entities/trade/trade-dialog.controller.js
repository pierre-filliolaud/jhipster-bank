'use strict';

angular.module('bankApp').controller('TradeDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Trade', 'Transfer',
        function($scope, $stateParams, $modalInstance, entity, Trade, Transfer) {

        $scope.trade = entity;
        $scope.transfers = Transfer.query();
        $scope.load = function(id) {
            Trade.get({id : id}, function(result) {
                $scope.trade = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('bankApp:tradeUpdate', result);
            $modalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.trade.id != null) {
                Trade.update($scope.trade, onSaveSuccess, onSaveError);
            } else {
                Trade.save($scope.trade, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
