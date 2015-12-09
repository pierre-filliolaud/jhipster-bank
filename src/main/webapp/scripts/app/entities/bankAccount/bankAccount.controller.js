'use strict';

angular.module('bankApp')
    .controller('BankAccountController', function ($scope, $state, $modal, BankAccount, BankAccountSearch, ParseLinks) {
      
        $scope.bankAccounts = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            BankAccount.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.bankAccounts.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.bankAccounts = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.search = function () {
            BankAccountSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.bankAccounts = result;
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
            $scope.bankAccount = {
                name: null,
                balance: null,
                id: null
            };
        };
    });
