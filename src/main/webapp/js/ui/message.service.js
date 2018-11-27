(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .constant('MESSAGE_CODES', {
      'GAME_START': 'Kivi kotti!',
      'UNEXPECTED_ERROR': 'Ootamatu viga!',
      'DEVICE_NOT_COMPATIBLE': 'Seade ei toeta positsioneerimist!',
      'INVALID_INPUT': 'Ebakorrektne sisend!',
      'GAME_EXISTS': 'Sellise nimega mäng on juba olemas!',
      'TEAM_EXISTS': 'Sellise nimega meeskond on juba olemas!',
      'INVALID_MEMBER': 'Ebakorretne mängu osaline!',
      'INVALID_CHALLENGE': 'Selline ülesanne puudub!',
      'CHALLENGE_COMPLETED': 'Ülesanne on lahendatud!',
      'GAME_OVER': 'Meeskond on mängu lõpetanud!',
      'SCORE_SAVED': 'Punktid edukalt salvestatud!'
    })
    .factory('MessageService', MessageService)
    .factory('errorInterceptor', errorInterceptor)
    .config(config);

  function config($httpProvider, ngToastProvider) {
    $httpProvider.interceptors.push('errorInterceptor');
    ngToastProvider.configure({
      verticalPosition: 'bottom',
      horizontalPosition: 'left',
      combineDuplications: true,
      //additionalClasses: 'container-fluid',
      maxNumber: 3
    });
  }

  function errorInterceptor($q, MessageService, MESSAGE_CODES) {
    return {
      responseError: function (rsp) {
        if (rsp.data) {
          let code = rsp.data.errorCode;
          MessageService.showError({ text: MESSAGE_CODES[code] || MESSAGE_CODES.UNEXPECTED_ERROR });
        }
        return $q.reject(rsp);
      }
    };
  }

  function MessageService(ngToast, MESSAGE_CODES) {
    let service = {
      showSuccess: showSuccess,
      showInfo: showInfo,
      showError: showError
    };
    return service;

    function showSuccess(msg) {
      msg.class = 'success';
      showMessage(msg);
    }
    
    function showInfo(msg) {
      msg.class = 'info';
      showMessage(msg);
    }
    
    function showError(msg) {
      msg.class = 'danger';
      showMessage(msg);
    }
    
    function showMessage(msg) {
      if (!msg || (!msg.text && !msg.code)) {
        return;
      }
      let text = msg.code ? MESSAGE_CODES[msg.code] : msg.text;
      ngToast.create({
        className: msg.class,
        //dismissOnTimeout: false,
        content: text
      });
    }
  }
})();