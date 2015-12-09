'use strict';

angular.module('bankApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('trade', {
                parent: 'entity',
                url: '/trades',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'bankApp.trade.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/trade/trades.html',
                        controller: 'TradeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('trade');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('trade.detail', {
                parent: 'entity',
                url: '/trade/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'bankApp.trade.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/trade/trade-detail.html',
                        controller: 'TradeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('trade');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Trade', function($stateParams, Trade) {
                        return Trade.get({id : $stateParams.id});
                    }]
                }
            })
            .state('trade.new', {
                parent: 'trade',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/trade/trade-dialog.html',
                        controller: 'TradeDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('trade', null, { reload: true });
                    }, function() {
                        $state.go('trade');
                    })
                }]
            })
            .state('trade.edit', {
                parent: 'trade',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/trade/trade-dialog.html',
                        controller: 'TradeDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Trade', function(Trade) {
                                return Trade.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('trade', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('trade.delete', {
                parent: 'trade',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/trade/trade-delete-dialog.html',
                        controller: 'TradeDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Trade', function(Trade) {
                                return Trade.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('trade', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
