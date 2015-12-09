'use strict';

angular.module('bankApp')
	.controller('TransferDeleteController', function($scope, $modalInstance, entity, Transfer) {

        $scope.transfer = entity;
        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Transfer.delete({id: id},
                function () {
                    $modalInstance.close(true);
                });
        };

    });