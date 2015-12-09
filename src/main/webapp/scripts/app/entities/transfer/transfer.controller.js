'use strict';

angular.module('bankApp')
    .controller('TransferController', function ($scope, $state, $modal, Transfer, TransferSearch, ParseLinks) {
      
        $scope.transfers = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Transfer.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.transfers.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.transfers = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.search = function () {
            TransferSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.transfers = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.transfer = {
                creationDate: null,
                amount: null,
                description: null,
                id: null
            };
        };
    });
