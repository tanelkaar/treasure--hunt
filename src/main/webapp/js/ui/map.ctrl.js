(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .controller('MapCtrl', MapCtrl);

    function MapCtrl($state, $interval, NgMap) {
        let refreshMapWatcher;
        let locationWatcher;
        let ctrl = {
            currentPos: null,
            map: null,
            markers: []
        };
        init();
        return ctrl = angular.extend(this, ctrl);

        function init() {
            initPos();
            refreshMapWatcher = $interval(refreshMap, 5000);
        }

        function initPos() {
            console.log('init pos');
            locationWatcher = navigator.geolocation.watchPosition(function (pos) {
                console.log('up pos: ', pos);
                ctrl.currentPos = {
                    lat: pos.coords.latitude,
                    lng: pos.coords.longitude
                };
                if (_.isEmpty(ctrl.markers)) {
                    for (let i = 0; i <= 5; i++) {
                        ctrl.markers.push({
                            id: 'marker: ' + i,
                            pos: [getRandomPos(ctrl.currentPos.lat), getRandomPos(ctrl.currentPos.lng)],
                            visible: true
                        });
                    }
                    console.log('markers: ', ctrl.markers);
                }
            }, function (error) {
                console.error('error getting pos');
            }, {
                enableHighAccuracy: true,
                timeout: 5000,
                maximumAge: 0
            });
        }

        function getRandomPos(pos) {
            return pos + ((parseInt(Math.random() * 100) % 2 == 0 ? -1 : 1) * (Math.random() / 500))
        }

        function refreshMap() {
            NgMap.getMap().then(function (map) {
                ctrl.map = map;
                if (!ctrl.currentPos) {
                    console.log('no position');
                    return;
                }

                console.log('up shapes');
                let pos = new google.maps.LatLng(ctrl.currentPos.lat, ctrl.currentPos.lng);
                _.each(map.shapes, (shape) => {
                    if (shape.getBounds().contains(pos)) {
                        console.log('start challenge');
                        let marker = _.find(ctrl.markers, (marker) => {
                            return marker.id === shape.id;
                        });
                        marker.visible = false;
                        cleanup();
                        $state.go('challenge', { id: marker.id });
                    }
                });
            });
        }

        function cleanup() {
            navigator.geolocation.clearWatch(locationWatcher);
            $interval.cancel(refreshMapWatcher);
        }
    }
})();