'use strict';

angular.module('bankApp')
    .factory('BankAccountSearch', function ($resource) {
        return $resource('api/_search/bankAccounts/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
