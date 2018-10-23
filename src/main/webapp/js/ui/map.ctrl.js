(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .controller('MapCtrl', MapCtrl);

    function MapCtrl(currentPos, challenges, $scope, $state, $interval, NgMap, TreasureHuntService) {
        $scope.$on('$destroy', () => {
            cleanup();
        });

        let _watcher;

        let ctrl = {
            currentPos: currentPos,
            challenges: challenges,
            map: null
        };
        init();
        return ctrl = angular.extend(this, ctrl);

        function init() {
            _watcher = $interval(() => {
                TreasureHuntService.getCurrentPos().then((pos) => {
                    ctrl.currentPos = pos;
                    refreshMap();
                });
            }, 2000);
        }

        function refreshMap() {
            console.log('refresh map');
            NgMap.getMap().then((map) => {
                ctrl.map = map;

                let pos = new google.maps.LatLng(ctrl.currentPos.lat, ctrl.currentPos.lng);
                _.each(map.shapes, (shape) => {
                    if (shape.getBounds().contains(pos)) {
                        console.log('start challenge');
                        let challenge = _.find(ctrl.challenges, (challenge) => {
                            return challenge.id === shape.id;
                        });
                        cleanup();
                        $state.go('challenge', { id: challenge.id });
                    }
                });
            });
        }

        function cleanup() {
            $interval.cancel(_watcher);
        }
    }
})();