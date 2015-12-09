/* globals $ */
'use strict';

angular.module('bankApp')
    .directive('bankAppPager', function() {
        return {
            templateUrl: 'scripts/components/form/pager.html'
        };
    });
