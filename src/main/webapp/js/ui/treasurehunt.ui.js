(function () {
    'use strict'

    angular
        .module('treasurehunt.ui', [])
        .config(config)
        .run(run);

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
                teams: (TreasureHuntService) => {
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
                currentPos: (TreasureHuntService) => {
                    return TreasureHuntService.getCurrentPos();
                },
                challenges: (TreasureHuntService) => {
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
                challenge: ($stateParams, TreasureHuntService) => {
                    return TreasureHuntService.getChallenge($stateParams.id);
                }
            }
        });
        $urlRouterProvider.otherwise('main');
    }

    function run($rootScope, $state, TreasureHuntService) {
        $rootScope.$on('$stateChangeStart', (event, toState, toParams, fromState, fromParams, options) => {
            if (toState.name != 'main' && !TreasureHuntService.hasTeam()) {
                event.preventDefault();
                $state.go('main');
                return;
            }

            if (toState.name === 'main' && TreasureHuntService.hasTeam()) {
                event.preventDefault();
                $state.go('map');
                return;
            }
        });
    }
})();