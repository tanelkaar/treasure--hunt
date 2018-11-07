(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .controller('MapCtrl', MapCtrl);

    function MapCtrl(currentPos, map, $scope, $state, $interval, NgMap, GameService) {
        $scope.$on('$destroy', () => {
            cleanup();
        });

        let _watcher;
        let ctrl = {
            currentPos: currentPos,
            waypoints: map.waypoints,
            map: null
        };
        init();
        return ctrl = angular.extend(this, ctrl);

        function init() {
            _watcher = $interval(() => {
                GameService.getCurrentPos().then((pos) => {
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
                        let challenge = _.find(ctrl.waypoints, (waypoint) => {
                            return waypoint.challengeId === shape.id;
                        });
                        cleanup();
                        $state.go('challenge', { id: waypoint.challengeId });
                    }
                });
            });
        }

        function cleanup() {
            $interval.cancel(_watcher);
        }
    }
})();