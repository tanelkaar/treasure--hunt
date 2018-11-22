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
        map: (GameService) => {
          return GameService.getMap().then((map) => {
            return map;
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
        challenge: ($stateParams, GameService) => {
          console.log('challenge resolve');
          return GameService.startChallenge($stateParams.id).then((rsp) => {
            return rsp.data;
          });
        }
      }
    });
    $urlRouterProvider.otherwise('main');
  }

  function run($rootScope, $state, GameService) {
    $rootScope.$on('$stateChangeStart', (event, toState, toParams, fromState, fromParams, options) => {
      if (_.contains(['main', 'map', 'challenge'], toState.name)) {
        console.log('change state: ', toState.name);
        if (toState.name != 'main' && !GameService.isMember()) {
          event.preventDefault();
          $state.go('main', { reload: fromState.name === 'main' });
          return;
        /*} else if (toState.name != 'challenge' && GameService.hasChallenge()) {
          event.preventDefault();
          //$state.go('challenge', { id: MemberService.getChallengeId() }, { reload: fromState.name === 'challenge' });
          return;
        } else if (toState.name != 'map' && GameService.hasGame() && !GameService.hasChallenge()) {
          event.preventDefault();
          //$state.go('map', { reload: fromState.name === 'map' });
          return;
        //} else if (fromState.name === toState.name) {
        //  event.preventDefault();
        //  return;*/
        }
      }
    });
  }
})();