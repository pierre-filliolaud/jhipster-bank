'use strict';

angular.module('bankApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


