(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .factory('GameService', GameService);

  function GameService($q, $http, MemberService) {
    let _posWatcher = null;
    let _currentPos = null;
    let _posOpts = {
      enableHighAccuracy: true,
      //timeout: 0,
      maximumAge: 0
    };
    let _posDefer = $q.defer();

    let service = {
      isCompatible: isCompatible,
      register: register,
      getCurrentPos: getCurrentPos,

      getGames: getGames,
      addGame: addGame,
      addTeam: addTeam,
      startGame: startGame,
      getMap: getMap,

      startChallenge: startChallenge,
      completeChallenge: completeChallenge
    };
    init();
    return service;

    function init() {
      startTracking();
    }

    function isCompatible() {
      return !!navigator.geolocation;
    }

    function register() {
      console.log('register')
      return $http.post('/api/register');
    }

    function getGames() {
      return $http.get('/api/games');
    }

    function addGame(name) {
      return getCurrentPos().then((pos) => {
        return $http.post('/api/add-game', {
          name: name,
          start: pos
        });
      });
    }

    function addTeam(gameId, name) {
      return $http.post('/api/game/' + gameId + '/add-team', {
        name: name
      });
    }

    function startGame(gameId, teamId) {
      return $http.post('/api/game/start', {
        gameId: gameId,
        teamId: teamId
      });
    }

    function getMap() {
      return $http.get('/api/game/map');
    }

    function startChallenge(id) {
      return getCurrentPos().then((pos) => {
        return $http.post('/api/game/challenge/' + id + '/start', pos);
      });
    }

    function completeChallenge(response) {
      return getCurrentPos().then((pos) => {
        response.coords = pos;
        return $http.post('/api/game/challenge/' + response.challengeId + '/complete', response);
      });
    }

    function startTracking() {
      console.log('start tracking');
      if (!isCompatible() || _posWatcher) {
        return;
      }
      _posWatcher = navigator.geolocation.watchPosition((pos) => {
        console.log('watcher: ', pos);
        _currentPos = {
          lat: pos.coords.latitude,
          lng: pos.coords.longitude
        };

        if (MemberService.hasGame()) {
          $http.post('/api/game/send-location', _currentPos);
        }
      }, (err) => {
        console.error('error watching current pos: ', err);
      }, _posOpts);
    }

    function stopTracking() {
      if (!isCompatible() || !_posWatcher) {
        return;
      }
      navigator.geolocation.clearWatch(_posWatcher);
    }

    function getCurrentPos() {
      console.log('get current pos');
      if (!isCompatible()) {
        _posDefer = $q.defer();
        _posDefer.reject();
        return _posDefer.promise;
      }
      if (_currentPos) {
        _posDefer = $q.defer();
        _posDefer.resolve(_currentPos);
        return _posDefer.promise;
      }
      navigator.geolocation.getCurrentPosition((pos) => {
        _posDefer.resolve(_currentPos = {
          lat: pos.coords.latitude,
          lng: pos.coords.longitude
        });
      }, (err) => {
        console.error('error getting current pos: ', err);
        getCurrentPos();
      }, _posOpts);
      return _posDefer.promise;
    }
  }
})();