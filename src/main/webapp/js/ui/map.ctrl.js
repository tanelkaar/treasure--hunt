(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .controller('MapCtrl', MapCtrl);

  function MapCtrl(map, $rootScope, $scope, $state, $interval, NgMap, GameService, CHALLENGE_STATE) {
    $scope.$on('$destroy', () => {
      cleanup();
    });
    $rootScope.$on('mapRefresh', function (event, map) {
      initMap(map);
    });

    let _watcher;
    let ctrl = {
      map: null,
      uncompletedWaypoints: null,
      select: select
    };
    //init();
    return ctrl = angular.extend(this, ctrl);


    function init() {
      initMap();
      _watcher = $interval(() => {
        initMap();
      }, 500);
    }

    function initMap(map) {
      GameService.getMap().then((map) => {
        ctrl.map = angular.merge(ctrl.map || {}, map);
        ctrl.uncompletedWaypoints = _.filter(ctrl.map.waypoints, (wp) => {
          return wp.state != CHALLENGE_STATE.COMPLETED;
        });
        //checkMap();
      });
    }

    /*
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
    }*/

    function select(event, wp) {
      GameService.sendLocation(wp.coords);
    }

    function cleanup() {
      console.log('map cleanup');
      $interval.cancel(_watcher);
    }
  }
})();