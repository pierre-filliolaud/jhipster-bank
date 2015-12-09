'use strict';

angular.module('bankApp')
    .factory('TransferSearch', function ($resource) {
        return $resource('api/_search/transfers/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
