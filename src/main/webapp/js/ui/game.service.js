(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .factory('GameService', GameService)
    .factory('gameAuthInterceptor', gameAuthInterceptor)
    .config(config);

  function config($httpProvider) {
    $httpProvider.interceptors.push('gameAuthInterceptor');
  }

  function gameAuthInterceptor($cookies, $injector) {
    return {
      response: (rsp) => {
        if (rsp.config.url.startsWith('/api/game') && $cookies.get('game-token')) {
          $injector.get('GameService').authData($cookies.get('game-token'));
        }
        return rsp;
      }
    };
  }

  function GameService($q, $http, $state, jwtHelper) {
    let _posWatcher = null;
    let _currentPos = null;
    let _authData;
    let _gameToken;
    let _posOpts = {
      enableHighAccuracy: true,
      //timeout: 0,
      maximumAge: 0
    };
    let _posDefer = $q.defer();

    let service = {
      isCompatible: isCompatible,
      authData: authData,
      isMember: isMember,
      hasGame: hasGame,
      hasChallenge: hasChallenge,
      register: register,
      //getCurrentPos: getCurrentPos,

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

    function authData(gameToken) {
      if (!gameToken) {
        _gameToken = null;
        _authData = null;
        return;
      }
      if (_gameToken === gameToken) {
        return;
      }
      _gameToken = gameToken;
      _authData = jwtHelper.decodeToken(_gameToken);

      console.log('AUTH: ', _authData);
      if (!isMember()) {
        $state.go('main');
      } else if (_authData.challengeId) {
        $state.go('challenge', { id: _authData.challengeId });
      } else if (_authData.gameId) {
        $state.go('map');
      }
    }

    function isMember() {
      return !!getMemberId();
    }

    function getMemberId() {
      return _authData ? _authData.memberId : null;
    }

    function hasGame() {
      return _authData ? _authData.gameId : null;
    }

    function hasChallenge() {
      return _authData ? _authData.challengeId : null;
    }

    function register() {
      console.log('register')
      return $http.post('/api/game/register');
    }

    function getGames() {
      return $http.get('/api/game/list');
    }

    function addGame(name) {
      return getCurrentPos().then((pos) => {
        return $http.post('/api/game/add', {
          name: name,
          start: pos
        });
      });
    }

    function addTeam(gameId, name) {
      return $http.post('/api/game/' + gameId + '/team/add', {
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
        _posDefer.resolve(_currentPos);
        sendLocation(_currentPos);
      }, (err) => {
        console.error('error watching current pos: ', err);
      }, _posOpts);
    }

    function sendLocation(pos) {
      $http.post('/api/game/location', pos).then((rsp) => {
        //$state.reload();
      });
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

      /*
      navigator.geolocation.getCurrentPosition((pos) => {
        _posDefer.resolve(_currentPos = {
          lat: pos.coords.latitude,
          lng: pos.coords.longitude
        });
      }, (err) => {
        console.error('error getting current pos: ', err);
        getCurrentPos();
      }, _posOpts);*/
      return _posDefer.promise;
    }
  }
})();