(function () {
  'use strict'

  angular
    .module('treasurehunt.ui', [])
    .config(config)
    .constant('CHALLENE_TYPE', {
      QUESTION: 'QUESTION',
      TASK: 'TASK'
    })
    .constant('ANSWER_TYPE', {
      TEXT: 'TEXT',
      SINGLE_CHOICE: 'SINGLE_CHOICE',
      MULTI_CHOICE: 'MULTI_CHOICE',
      IMAGE: 'IMAGE'
    })
    .constant('CHALLENGE_STATE', {
      UNCOMPLETED: 'UNCOMPLETED',
      IN_PROGRESS: 'IN_PROGRESS',
      COMPLETED: 'COMPLETED'
    })
    .constant('TEAM_STATE', {
      UNCOMPLETED: 'STARTING',
      IN_PROGRESS: 'IN_PROGRESS',
      IN_PROGRESS: 'COMPLETING',
      COMPLETED: 'COMPLETED'
    })
    .run(run);

  function config($stateProvider, $urlRouterProvider) {
    $stateProvider.state('main', {
      url: '/main',
      views: {
        '': {
          templateUrl: '/main.html',
          controller: 'MainCtrl as ctrl'
        }
      },
      resolve: {
        register: (GameService) => {
          return GameService.register().then(() => {
            if (GameService.hasGame()) {
              GameService.startGame();
            }
            return;
          });
        },
        games: (register, GameService) => {
          return GameService.getGames().then((rsp) => {
            return rsp.data;
          });
        }
      }
    });
    $stateProvider.state('map', {
      url: '/map',
      views: {
        '': {
          templateUrl: '/map.html',
          controller: 'MapCtrl as ctrl'
        }
      },
      resolve: {
        map: (GameService) => {
          return GameService.getMap().then((map) => {
            return map;
          });
        }
      }
    });
    $stateProvider.state('challenge', {
      url: '/challenge',
      views: {
        '': {
          templateUrl: '/challenge.html',
          controller: 'ChallengeCtrl as ctrl'
        }
      },
      resolve: {
        challenge: (GameService) => {
          return GameService.startChallenge().then((rsp) => {
            return rsp.data;
          });
        }
      }
    });
    $stateProvider.state('summary', {
      url: '/summary',
      views: {
        '': {
          templateUrl: '/summary.html',
          controller: 'SummaryCtrl as ctrl'
        }
      },
      resolve: {
        games: (AdminService) => {
          return AdminService.getGames().then((rsp) => {
            return rsp.data;
          });
        },
      }
    });
    $stateProvider.state('reset', {
      url: '/reset',
      resolve: {
        reset: ($cookies, $state) => {
          $cookies.remove('game-token');
          $state.go('main');
        }
      }
    });
    $urlRouterProvider.otherwise('main');
  }

  function run($rootScope, $state, GameService) {
    $rootScope.$on('$stateChangeStart', (event, toState, toParams, fromState, fromParams, options) => {
      if (_.contains(['main', 'map', 'challenge'], toState.name)) {
        if (toState.name === 'main' && GameService.hasGame()) {
          event.preventDefault();
          $state.go('map');
          return;
        } else if (toState.name === 'map' && !GameService.hasGame()) {
          event.preventDefault();
          $state.go('main');
          return;
        } else if (toState.name === 'challenge' && !GameService.hasChallenge()) {
          event.preventDefault();
          $state.go('map');
          return;
        }
      }
    });
  }
})();