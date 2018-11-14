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
    .run(run);

  function config($stateProvider, $urlRouterProvider, $httpProvider) {
    $httpProvider.interceptors.push(() => {
      return {
        'request': (config) => {
          //config.url = '/treasure-hunt' + config.url;
          return config;
        }
      }
    });

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
            return;
          });
        },
        games: (register, GameService) => {
          return GameService.getGames().then((rsp) => {
            return rsp.data;
          });
        },
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
        currentPos: (GameService) => {
          return GameService.getCurrentPos();
        },
        map: (GameService) => {
          return GameService.getMap().then((rsp) => {
            return rsp.data;
          });
        }
      }
    });
    $stateProvider.state('challenge', {
      url: '/challenge/{id}',
      views: {
        '': {
          templateUrl: '/challenge.html',
          controller: 'ChallengeCtrl as ctrl'
        }
      },
      resolve: {
        challenge: ($q, $stateParams, GameService) => {
          console.log('challenge resolve');
          //return GameService.startChallenge($stateParams.id).then((rsp) => {
          //  return rsp.data;
          //});
          let textChallenge = { id: 1, type: 'IMAGE', question: 'Which came first - the chicken or the egg?' };
          let defer = $q.defer();
          defer.resolve(textChallenge);
          return defer.promise;
        }
      }
    });
    $urlRouterProvider.otherwise('main');
  }

  function run($rootScope, $state, MemberService, GameService) {
    $rootScope.$on('$stateChangeStart', (event, toState, toParams, fromState, fromParams, options) => {
      if (toState.name != 'main' && !MemberService.isRegistered()) {
        event.preventDefault();
        $state.go('main');
        return;
      }

      if (toState.name === 'challenge') {
        return;
      }

      if (toState.name != 'main' && !MemberService.hasGame()) {
        event.preventDefault();
        $state.go('main');
        return;
      }

      if (toState.name === 'main' && MemberService.hasGame()) {
        event.preventDefault();
        $state.go('map');
        return;
      }
    });
  }
})();