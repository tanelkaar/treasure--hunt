(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .controller('MapCtrl', MapCtrl);

  function MapCtrl(map, $scope, $state, $interval, NgMap, GameService, CHALLENGE_STATE) {
    $scope.$on('$destroy', () => {
      cleanup();
    });

    let _watcher;
    let ctrl = {
      map: null,
      uncompletedWaypoints: null,
    };
    init();
    return ctrl = angular.extend(this, ctrl);

    function init() {
      initMap(map);
      _watcher = $interval(() => {
        loadMap();
      }, 5000);
    }
    
    function initMap(map) {
      ctrl.map = angular.merge(ctrl.map || {}, map);
      ctrl.uncompletedWaypoints = _.filter(ctrl.map.waypoints, (wp) => {
        return wp.state != CHALLENGE_STATE.COMPLETED;
      });
      //checkMap();
    }

    function loadMap() {
      GameService.getMap().then((rsp) => {
        initMap(rsp.data);
      });
    }

    function checkMap() {
      console.log('check map');
      if (!ctrl.map.location) {
        return;
      }

      NgMap.getMap().then((map) => {
        let pos = new google.maps.LatLng(ctrl.map.location.lat, ctrl.map.location.lng);
        let shape = _.find(map.shapes, (shape) => {
          return shape.getBounds().contains(pos);
        });
        if (!shape) {
          return;
        }
        let waypoint = _.find(ctrl.map.waypoints, (wp) => {
          return wp.challengeId === shape.id;
        });
        $state.go('challenge', { id: waypoint.challengeId });
      });
    }

    function cleanup() {
      console.log('map cleanup');
      $interval.cancel(_watcher);
    }
  }
})();