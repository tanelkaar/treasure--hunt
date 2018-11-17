(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .factory('MemberService', MemberService)
    .factory('authInterceptor', authInterceptor)
    .config(config);

  function config($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
  }

  function authInterceptor($cookies, MemberService) {
    return {
      response: (rsp) => {
        if (rsp.config.url.startsWith('/api/') && $cookies.get('auth-token')) {
          MemberService.init($cookies.get('auth-token'));
        }
        return rsp;
      }
    };
  }

  function MemberService(jwtHelper) {
    let _auth;
    let _authToken;

    let service = {
      init: init,
      isRegistered: isRegistered,
      getMemberId: getMemberId,
      getGameId: getGameId,
      getTeamId: getTeamId,
      hasGame: hasGame,
      hasTeam: hasTeam
    };
    return service;

    function init(token) {
      if (!token) {
        _authToken = null;
        _auth = null;
        return;
      }
      if (_authToken === token) {
        return;
      }
      _authToken = token;
      _auth = jwtHelper.decodeToken(_authToken);
      console.log('AUTH: ', _auth);
    }

    function isRegistered() {
      return !!_auth;
    }

    function getMemberId() {
      return _auth ? _auth.memberId : null;
    }

    function getGameId() {
      return _auth ? _auth.gameId : null;
    }

    function getTeamId() {
      return _auth ? _auth.teamId : null;
    }

    function hasGame() {
      return !!getGameId();
    }

    function hasTeam() {
      return !!getTeamId();
    }
  }
})();