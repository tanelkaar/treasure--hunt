(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .controller('MainCtrl', MainCtrl);

    function MainCtrl(teams, $state, TreasureHuntService) {
        let ctrl = {
            team: null,
            teams: teams,
            createTeam: createTeam,
            selectTeam: selectTeam
        }
        return ctrl = angular.extend(this, ctrl);

        function createTeam() {
            console.log('create team - implement meh');
        }

        function selectTeam() {
            console.log('select team');
            if (_.isEmpty(ctrl.team)) {
                return;
            }
            TreasureHuntService.selectTeam(ctrl.team).then(() => {
                $state.go('map');
            });
        }
    }
})();