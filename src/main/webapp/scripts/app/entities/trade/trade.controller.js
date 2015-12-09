'use strict';

angular.module('bankApp')
    .controller('TradeController', function ($scope, $state, $modal, Trade, TradeSearch, ParseLinks) {
      
        $scope.trades = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Trade.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.trades.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.trades = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.search = function () {
            TradeSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.trades = result;
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
            $scope.trade = {
                name: null,
                id: null
            };
        };
    });
