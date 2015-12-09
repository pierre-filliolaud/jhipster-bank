'use strict';

angular.module('bankApp').controller('TransferDialogController',
    ['$scope', '$stateParams', '$modalInstance', '$q', 'entity', 'Transfer', 'BankAccount', 'Trade',
        function($scope, $stateParams, $modalInstance, $q, entity, Transfer, BankAccount, Trade) {

        $scope.transfer = entity;
        $scope.bankaccounts = BankAccount.query();
        $scope.trades = Trade.query({filter: 'transfer-is-null'});
        $q.all([$scope.transfer.$promise, $scope.trades.$promise]).then(function() {
            if (!$scope.transfer.tradeId) {
                return $q.reject();
            }
            return Trade.get({id : $scope.transfer.tradeId}).$promise;
        }).then(function(trade) {
            $scope.trades.push(trade);
        });
        $scope.load = function(id) {
            Transfer.get({id : id}, function(result) {
                $scope.transfer = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('bankApp:transferUpdate', result);
            $modalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.transfer.id != null) {
                Transfer.update($scope.transfer, onSaveSuccess, onSaveError);
            } else {
                Transfer.save($scope.transfer, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
