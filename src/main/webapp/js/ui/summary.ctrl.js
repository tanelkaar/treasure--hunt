(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .controller('SummaryCtrl', SummaryCtrl);

  function SummaryCtrl(games, AdminService, MessageService, MESSAGE_CODES) {
    let ctrl = {
      games: games,
      game: null,
      team: null,
      loadGame: loadGame,
      saveScore: saveScore
    };
    return ctrl = angular.extend(this, ctrl);

    function loadGame(id) {
      AdminService.getGame(id).then((rsp) => {
        ctrl.game = rsp.data;
      });
    }

    function saveScore() {
      AdminService.saveGame(ctrl.game).then((rsp) => {
        MessageService.showInfo({ text: MESSAGE_CODES.SCORE_SAVED });
      });
    }
  }
})();