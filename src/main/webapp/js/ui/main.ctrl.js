(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .controller('MainCtrl', MainCtrl);

  function MainCtrl(games, $state, GameService, MessageService) {
    let ctrl = {
      isCompatible: GameService.isCompatible,
      game: null,
      team: null,
      games: games,
      selectGame: selectGame,
      selectTeam: selectTeam,
      addGame: addGame,
      addTeam: addTeam,
      reset: reset,
      start: start
    }
    return ctrl = angular.extend(this, ctrl);

    function selectGame(game) {
      ctrl.game = game;
      _.each(ctrl.games, (g) => {
        if (game.id != g.id) {
          g.selected = false;
        }
      });
    }

    function selectTeam(team) {
      ctrl.team = team;
      _.each(ctrl.game.teams, (t) => {
        if (team.id != t.id) {
          t.selected = false;
        }
      });
    }

    function addGame(name) {
      GameService.addGame(name).then((rsp) => {
        ctrl.gameName = null;
        ctrl.games.push(rsp.data);
      });
    }

    function addTeam(name) {
      GameService.addTeam(ctrl.game.id, name).then((rsp) => {
        ctrl.teamName = null;
        ctrl.game.teams.push(rsp.data);
      });
    }

    function reset() {
      if (ctrl.game) {
        ctrl.game.selected = false;
        ctrl.game = null;
      }
      if (ctrl.team) {
        ctrl.team.selected = false;
        ctrl.team = null;
      }
    }

    function start() {
      GameService.startGame(ctrl.game.id, ctrl.team.id).then(() => {
        MessageService.showSuccess({ text: 'Mäng läks!' });
        $state.go('map');
      });
    }
  }
})();