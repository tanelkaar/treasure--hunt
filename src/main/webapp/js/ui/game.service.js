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

  function gameAuthInterceptor($q, $cookies, $injector) {
    let interceptor = {
      response: (rsp) => {
        readToken(rsp);
        return rsp;
      },
      responseError: (rsp) => {
        readToken(rsp);
        return $q.reject(rsp);
      }
    };
    return interceptor;

    function readToken(rsp) {
      if (rsp.config.url.startsWith('/api/game')) {
        $injector.get('GameService').readToken($cookies.get('game-token'));
      }
    }
  }

  function GameService($rootScope, $q, $http, $state, $interval, $cookies, jwtHelper, MessageService, TEAM_STATE, CHALLENGE_STATE, MESSAGE_CODES) {
    let _token;
    let _tokenJwt;

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
      readToken: readToken,
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
    init();
    return service;
    
    function init() {
      startTracking(); // for testing
      readToken($cookies.get('game-token'));
    }

    function readToken(tokenJwt) {
      if (!tokenJwt) {
        _tokenJwt = null;
        _token = null;
        return;
      }
      if (_tokenJwt === tokenJwt) {
        return;
      }
      _tokenJwt = tokenJwt;
      _token = jwtHelper.decodeToken(_tokenJwt);
      console.log('AUTH: ', $state.current.name, _token);
    }

    function isMember() {
      return !!getMemberId();
    }

    function getMemberId() {
      return _token ? _token.memberId : null;
    }

    function hasGame() {
      return !!getGameId();
    }

    function getGameId() {
      return _token ? _token.gameId : null; 
    }

    function getTeamId() {
      return _token ? _token.teamId : null; 
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
      return $http.post('/api/register');
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
        gameId: gameId || getGameId(),
        teamId: teamId || getTeamId()
      }).then(() => {
        MessageService.showInfo({ text: MESSAGE_CODES.GAME_START });
        start();
      }, handleError);
    }

    function getMap() {
      if (!_map) {
        return _mapDefer.promise;;
      }
      _mapDefer = $q.defer();
      _mapDefer.resolve(_map);
      return _mapDefer.promise;
    }

    function startChallenge() {
      return $http.post('/api/game/challenge/start');
    }

    function completeChallenge(response) {
      $http.post('/api/game/challenge/complete', response).then((rsp) => {
        MessageService.showInfo({ text: MESSAGE_CODES.CHALLENGE_COMPLETED });
        refreshMap(rsp);
      }, handleError);
    }

    // private
    function start() {
      startTracking();
      loadMap();
      _mapWatcher = $interval(() => {
        loadMap();
      }, 3000);
      $state.go('map');
    }

    function exit() {
      stopTracking();
      if (_mapWatcher) {
        $interval.cancel(_mapWatcher);
      }
      //_token = null;
      //_tokenJwt = null;
      //$cookies.remove('game-token');
      $state.go('main');
    }

    function sendLocation(pos) {
      if (!hasGame()) {
        return;
      }
      $http.post('/api/game/location', pos).then(refreshMap, handleError);
    }

    function loadMap() {
      $http.get('/api/game/map').then(refreshMap, handleError);
    }

    function refreshMap(rsp) {
      _map = rsp.data;
      _mapDefer.resolve(_map);
      $rootScope.$emit('mapRefresh', _map);
      
      if (_map.state === TEAM_STATE.COMPLETED) {
        MessageService.showInfo({ text: MESSAGE_CODES.GAME_OVER });
        exit();
        return;
      }
      if (hasChallenge()) {
        $state.go('challenge');
        return;
      }
      $state.go('map');
    }

    function isCompatible() {
      return !!navigator.geolocation;
    }

    function startTracking() {
      if (!isCompatible()) {
        MessageService.showError({ text: MESSAGE_CODES.DEVICE_NOT_COMPATIBLE });
        return;
      }
      if (_posWatcher) {
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
        console.error('ERROR (posWatcher): ', e);
      }, _posOpts);
    }

    function stopTracking() {
      if (!isCompatible() || !_posWatcher) {
        return;
      }
      console.log('stop watcher');
      navigator.geolocation.clearWatch(_posWatcher);
      _posWatcher = null;
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

    function handleError(error) {
      console.error('error: ', error);
      exit();
    }
  }
})();