(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .controller('MapCtrl', MapCtrl);

  function MapCtrl(map, $rootScope, $scope, GameService, CHALLENGE_STATE) {
    $rootScope.$on('mapRefresh', function (event, map) {
      initMap(map);
    });

    let ctrl = {
      map: null,
      uncompletedWaypoints: null,
      select: select
    };
    initMap(map);
    return ctrl = angular.extend(this, ctrl);

    function initMap(map) {
      ctrl.map = angular.merge(ctrl.map || {}, map);
      ctrl.uncompletedWaypoints = _.filter(ctrl.map.waypoints, (wp) => {
        return wp.state != CHALLENGE_STATE.COMPLETED;
      });
    }

    // todo for testing only
    function select(event, wp) {
      GameService.sendLocation(wp.coords);
    }
  }
})();