(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .controller('MainCtrl', MainCtrl);

    function MainCtrl(teams, $state, TreasureHuntService) {
        let ctrl = {
            teams: teams,
            createTeam: createTeam,
            selectTeam: selectTeam
        }
        return ctrl = angular.extend(this, ctrl);

        function createTeam() {
            console.log('create team - implement meh');
        }

        function selectTeam(team) {
            TreasureHuntService.selectTeam(team).then(() => {
                $state.go('map');
            });
        }
    }
})();