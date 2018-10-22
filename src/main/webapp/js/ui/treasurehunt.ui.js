(function () {
    'use strict'

    angular
        .module('treasurehunt.ui', [])
        .config(config);

    function config($stateProvider, $urlRouterProvider) {
        $stateProvider.state('main', {
            url: '/main',
            views: {
                '': {
                    templateUrl: 'main.html',
                    controller: 'MainCtrl as ctrl'
                }
            },
            resolve: {
                init: function($state, TreasureHuntService) {
                    if (TreasureHuntService.hasTeam()) {
                        $state.go('/map');
                    }
                },
                teams: function(TreasureHuntService) {
                    return TreasureHuntService.getTeams();
                }
            }
        });
        $stateProvider.state('map', {
            url: '/map',
            views: {
                '': {
                    templateUrl: 'map.html',
                    controller: 'MapCtrl as ctrl'
                }
            },
            resolve: {
                init: function($state, TreasureHuntService) {
                    if (!TreasureHuntService.hasTeam()) {
                        $state.go('/main');
                    }
                },
                currentPos: function(TreasureHuntService) {
                    return TreasureHuntService.getCurrentPos();
                },
                challenges: function(TreasureHuntService) {
                    return TreasureHuntService.getChallenges();
                }
            }
        });
        $stateProvider.state('challenge', {
            url: '/challenge/{id}',
            views: {
                '': {
                    templateUrl: 'challenge.html',
                    controller: 'ChallengeCtrl as ctrl'
                }
            },
            resolve: {
                init: function($state, TreasureHuntService) {
                    if (!TreasureHuntService.hasTeam()) {
                        $state.go('/main');
                    }
                },
                challenge: function($stateParams, TreasureHuntService) {
                    return TreasureHuntService.getChallenge($stateParams.id);
                }
            }
        });
        $urlRouterProvider.otherwise('/main');
    }
})();