(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .controller('ChallengeCtrl', ChallengeCtrl);

    function ChallengeCtrl(challenge, $state, $stateParams, TreasureHuntService) {
        let ctrl = {
            challenge: challenge,
            finish: finish
        }
        return ctrl = angular.extend(this, ctrl);

        function finish() {
            TreasureHuntService.resolveChallenge({id: $stateParams.id}).then(() => {
                $state.go('map');
            });
        }
    }
})();