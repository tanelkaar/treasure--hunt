(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .factory('AdminService', AdminService);

  function AdminService($q, $http) {
    let _game = null;
    let _gameDefer = $q.defer();
    
    let service = {
      getGame: getGame,
    };
    return service;

    function getGame() {
      $http.get('/api/admin/export').then((rsp) => {
        _gameDefer.resolve(_game = rsp.data);
      });
      return _gameDefer.promise;
    }
  }
})();