/* globals $ */
'use strict';

angular.module('bankApp')
    .directive('bankAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
