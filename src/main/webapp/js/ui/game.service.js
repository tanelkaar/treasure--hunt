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

  function GameService($rootScope, $q, $http, $state, $interval, jwtHelper, MessageService, TEAM_STATE, CHALLENGE_STATE, MESSAGE_CODES) {
    let _authData;
    let _gameToken;

    let _posWatcher = null;
    let _currentPos = null;
    let _posDefer = $q.defer();
    let _posOpts = {
      enableHighAccuracy: true,
      //timeout: 0,
      maximumAge: 0
    };

    let _map = null;
    let _mapDefer = $q.defer();
    let _mapWatcher = null;

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
      sendLocation: sendLocation, // for testing only

      startChallenge: startChallenge,
      completeChallenge: completeChallenge,
    };
    startTracking(); // for testing
    return service;

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
      return !!getChallengeId();
    }

    function getChallengeId() {
      if (!_map) {
        return null;
      }
      let waypoint = _.find(_map.waypoints, (wp) => {
        return wp.state === CHALLENGE_STATE.IN_PROGRESS;
      });
      if (!waypoint) {
        return null;
      }
      return waypoint.challengeId;
    }

    function register() {
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
      }).then(() => {
        MessageService.showInfo({ text: MESSAGE_CODES.GAME_START });
        start();
        $state.go('map');
      }, (e) => {
        console.error('ERROR (startGame): ', e);
      });
    }

    function getMap() {
      if (!_map) {
        return _mapDefer.promise;;
      }
      _mapDefer = $q.defer();
      _mapDefer.resolve(_map);
      return _mapDefer.promise;
    }

    function startChallenge(id) {
      return $http.post('/api/game/challenge/' + id + '/start');
    }

    function completeChallenge(response) {
      $http.post('/api/game/challenge/' + response.challengeId + '/complete', response).then(() => {
        MessageService.showInfo({ text: MESSAGE_CODES.CHALLENGE_SUCCESSFULLY_COMPLETED });
        $state.go('map');
      }, (e) => {
        console.error('ERROR (completeChallenge): ', e);
      });
    }

    function start() {
      // startTracking(); - this shoud be here for testing we call it on init
      startMapPoller();
    }

    function startTracking() {
      console.log('start tracking');
      if (!isCompatible()) {
        MessageService.showError({ text: MESSAGE_CODES.DEVICE_NOT_COMPATIBLE });
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
      }, (e) => {
        console.error('ERROR (posWatcher): ', err);
      }, _posOpts);
    }

    function startMapPoller() {
      loadMap();
      _mapWatcher = $interval(() => {
        loadMap();
      }, 2000);
    }

    function sendLocation(pos) {
      $http.post('/api/game/location', pos).then((rsp) => {
      }, (e) => {
        console.error('ERROR (sendLocation): ', e);
      });
    }

    function loadMap() {
      if (!hasGame()) {
        return;
      }
      $http.get('/api/game/map').then((rsp) => {
        _map = rsp.data;
        _mapDefer.resolve(_map);

        if (_map.state === TEAM_STATE.COMPLETED) {
          MessageService.showInfo({ text: MESSAGE_CODES.TEAM_COMPLETED });
          // TODO: reset auth data
          _authData.gameId = null;
          $state.go('main');
          return;
        }

        if (hasChallenge()) {
          $state.go('challenge', { id: getChallengeId() });
          return;
        }

        $rootScope.$emit('mapRefresh', _map);
        $state.go('map');
      }, (e) => {
        console.error('ERROR (loadMap): ', e);
      });
    }

    function stopTracking() {
      if (!isCompatible() || !_posWatcher) {
        return;
      }
      navigator.geolocation.clearWatch(_posWatcher);
    }

    // TODO: to be removed
    function getCurrentPos() {
      console.log('get current pos');
      if (!isCompatible()) {
        MessageService.showError({ text: MESSAGE_CODES.DEVICE_NOT_COMPATIBLE });
        _posDefer = $q.defer();
        _posDefer.reject();
        return _posDefer.promise;
      }
      if (_currentPos) {
        _posDefer = $q.defer();
        _posDefer.resolve(_currentPos);
      }
      return _posDefer.promise;
    }
  }
})();