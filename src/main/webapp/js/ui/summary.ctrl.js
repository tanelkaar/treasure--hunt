(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .controller('SummaryCtrl', SummaryCtrl);

  function SummaryCtrl(game) {
    let ctrl = {
      game: game,
      team: null,
      selectTeam: selectTeam
    };
    return ctrl = angular.extend(this, ctrl);

    function selectTeam(team) {
      ctrl.team = team;
    }
  }
})();