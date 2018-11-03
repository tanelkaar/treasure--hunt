(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .controller('MainCtrl', MainCtrl);

    function MainCtrl(teams, $state, TreasureHuntService) {
        let ctrl = {
            mode: 'SELECT',
            team: null,
            teams: teams,
            isCompatible: TreasureHuntService.isCompatible,
            createTeam: createTeam,
            selectTeam: selectTeam
        }
        return ctrl = angular.extend(this, ctrl);

        function createTeam() {
            console.log('create team');
            TreasureHuntService.createTeam(ctrl.team).then((rsp) => {
                ctrl.team = rsp.data;
                selectTeam();
            });
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