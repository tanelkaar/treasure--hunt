(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .factory('AdminService', AdminService);

  function AdminService($q, $http) {
    let service = {
      getGames: getGames,
      getGame: getGame,
      saveGame: saveGame
    };
    return service;

    function getGames() {
      return $http.get('/api/admin/game/list');
    }

    function getGame(gameId) {
      return $http.get('/api/admin/game/' + gameId + '/export');
    }

    function saveGame(game) {
      return $http.post('/api/admin/import', game);
    }
  }
})();