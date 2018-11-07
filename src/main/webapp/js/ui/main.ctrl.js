(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .controller('MainCtrl', MainCtrl);

    function MainCtrl(isRunning, teams, $state, GameService) {
        let ctrl = {
            isRunning: isRunning,
            mode: 'SELECT',
            team: null,
            teams: teams,
            isCompatible: GameService.isCompatible,
            startGame: startGame,
            selectTeam: selectTeam
        }
        return ctrl = angular.extend(this, ctrl);

        function startGame() {
          GameService.startGame().then(() => {
            ctrl.isRunning = true;
          });
        }

        function selectTeam() {
            console.log('select team');
            if (_.isEmpty(ctrl.team)) {
                return;
            }
            GameService.selectTeam(ctrl.team).then(() => {
                $state.go('map');
            });
        }
    }
})();