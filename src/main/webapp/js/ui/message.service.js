(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
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

  function errorInterceptor($q, MessageService) {
    return {
      responseError: function (rsp) {
        if (rsp.data) {
          MessageService.showError({ code: rsp.data.errorCode });
        }
        return $q.reject(rsp);
      }
    };
  }

  function MessageService(ngToast) {
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
      console.log('MESSAGE: ', msg);
      ngToast.create({
        className: msg.class,
        //dismissOnTimeout: false,
        content: msg.code || msg.text
      });
    }
  }
})();