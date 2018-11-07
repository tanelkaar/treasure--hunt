(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .factory('GameService', GameService);

    function GameService($q, $http) {
        let _memberId;
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
            isRunning: isRunning,
            startGame: startGame,
            getMap: getMap,
            hasTeam: hasTeam,
            getTeams: getTeams,
            selectTeam: selectTeam,
            getCurrentPos: getCurrentPos,
            startChallenge: startChallenge,
            completeChallenge: completeChallenge
        };
        startTracking();
        return service;

        function isCompatible() {
            return !!navigator.geolocation;
        }

        function isRunning() {
          return $http.get('/api/game/is-running');
        }

        function startGame() {
          return getCurrentPos().then((pos) => {
            return $http.post('/api/game/start', {
              name: 'T1000',
              start: pos
            });
          });
        }

        function getMap() {
          return $http.get('/api/game/member/' + _memberId + '/map');
        }

        function hasTeam() {
          console.log('HAS TEAM: ', !!_memberId);
            return !!_memberId;
        }

        function getTeams() {
            console.log('get teams');
            return $http.get('/api/game/teams');
        }

        function selectTeam(team) {
            let defer = $q.defer();
            $http.post('/api/game/select-team', team).then((rsp) => {
              defer.resolve(_memberId = rsp.data);
            });
            return defer.promise;
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

        function startChallenge(id) {
          return getCurrentPos().then((pos) => {
            return $http.post('/api/game/member/' + _memberId + '/challenge/' + id, pos);
          });
        }

        function completeChallenge(challenge) {
            // TODO
        }
    }
})();