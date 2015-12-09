'use strict';

angular.module('bankApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('transfer', {
                parent: 'entity',
                url: '/transfers',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'bankApp.transfer.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transfer/transfers.html',
                        controller: 'TransferController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transfer');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('transfer.detail', {
                parent: 'entity',
                url: '/transfer/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'bankApp.transfer.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transfer/transfer-detail.html',
                        controller: 'TransferDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transfer');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Transfer', function($stateParams, Transfer) {
                        return Transfer.get({id : $stateParams.id});
                    }]
                }
            })
            .state('transfer.new', {
                parent: 'transfer',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/transfer/transfer-dialog.html',
                        controller: 'TransferDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    creationDate: null,
                                    amount: null,
                                    description: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('transfer', null, { reload: true });
                    }, function() {
                        $state.go('transfer');
                    })
                }]
            })
            .state('transfer.edit', {
                parent: 'transfer',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/transfer/transfer-dialog.html',
                        controller: 'TransferDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Transfer', function(Transfer) {
                                return Transfer.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('transfer', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('transfer.delete', {
                parent: 'transfer',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/transfer/transfer-delete-dialog.html',
                        controller: 'TransferDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Transfer', function(Transfer) {
                                return Transfer.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('transfer', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
