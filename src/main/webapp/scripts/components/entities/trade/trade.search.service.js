'use strict';

angular.module('bankApp')
    .factory('TradeSearch', function ($resource) {
        return $resource('api/_search/trades/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
