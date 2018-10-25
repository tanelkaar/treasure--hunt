(function () {
    'use strict'

    angular
        .module('treasurehunt.ui')
        .factory('TreasureHuntService', TreasureHuntService);

    function TreasureHuntService($q) {
        let _team;
        let _teams = [{id: 1, name: 'team1'}, {id: 2, name: 'team2'}];
        let _challenges = [];
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
            hasTeam: hasTeam,
            getTeams: getTeams,
            createTeam: createTeam,
            selectTeam: selectTeam,
            getCurrentPos: getCurrentPos,
            getChallenges: getChallenges,
            getChallenge: getChallenge,
            resolveChallenge: resolveChallenge
        };
        return service;

        function isCompatible() {
            return !!navigator.geolocation;
        }

        function hasTeam() {
            return !_.isEmpty(_team);
        }

        function getTeams() {
            console.log('get teams');
            let defer = $q.defer();
            defer.resolve(_teams);
            return defer.promise;
        }

        function createTeam(team) {
            console.log('create team');
            let defer = $q.defer();
            if (_.isEmpty(team)) {
                defer.reject();
            } else {
                team.id = _teams.length + 1;
                _teams.push(team);
                defer.resolve(_team = team);
            }
            return defer.promise;
        }

        function selectTeam(team) {
            startTracking();
            let defer = $q.defer();
            defer.resolve(_team = team);
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

        function getChallenges() {
            console.log('get challenges');
            let defer = $q.defer();
            if (!_.isEmpty(_challenges)) {
                defer.resolve(_challenges);
            } else {
                getCurrentPos().then((pos) => {
                    for (let i = 1; i <= 5; i++) {
                        _challenges.push({
                            id: 'challenge_' + i,
                            pos: [getRandomPos(pos.lat), getRandomPos(pos.lng)],
                            visible: true,
                            visited: false
                        });
                    }
                    defer.resolve(_challenges);
                });
            }
            return defer.promise;
        }

        function getChallenge(id) {
            let defer = $q.defer();
            defer.resolve(_.find(_challenges, (challenge) => {
                return challenge.id === id;
            }));
            return defer.promise;
        }

        function resolveChallenge(challenge) {
            let defer = $q.defer();
            defer.resolve(_.find(_challenges, (c) => {
                return challenge.id === c.id;
            }).visited = true);
            return defer.promise;
        }

        function getRandomPos(pos) {
            return pos + ((parseInt(Math.random() * 100) % 2 == 0 ? -1 : 1) * (Math.random() / 500))
        }
    }
})();