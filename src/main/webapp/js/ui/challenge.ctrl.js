(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .filter('trustUrl', trustUrlFilter)
    .directive('fileRead', fileRead)
    .controller('ChallengeCtrl', ChallengeCtrl);

  function trustUrlFilter($sce) {
    return (url) => {
      return $sce.trustAsResourceUrl(url)
    }
  }

  function fileRead() {
    let dir = {
      restrict: 'A',
      require: 'ngModel',
      link: link
    }
    return dir;

    function link(scope, elem, attrs, ngModel) {
      elem.on('change', (event) => {
        let file = event.target.files[0];
        ngModel.$setViewValue(file);
      });
    }
  }

  function ChallengeCtrl(challenge, $q, imgur, GameService, MessageService, MESSAGE_CODES, ANSWER_TYPE) {
    let ctrl = {
      response: {
        challengeId: challenge.id
      },
      challenge: challenge,
      selectOption: selectOption,
      hasSelectedOption: hasSelectedOption,
      complete: complete
    }
    return ctrl = angular.extend(this, ctrl);

    function hasSelectedOption() {
      return !!_.find(ctrl.challenge.options, (opt) => {
        return opt.selected;
      });
    }

    function selectOption(option) {
      if (ctrl.challenge.answerType === ANSWER_TYPE.MULTI_CHOICE) {
        return;
      }
      _.each(ctrl.challenge.options, (opt) => {
        if (option.id != opt.id) {
          opt.selected = false;
        }
      });
    }

    function complete() {
      prepare().then(() => {
        GameService.completeChallenge(ctrl.response);
      });
    }

    function prepare() {
      if (_.contains([ANSWER_TYPE.SINGLE_CHOICE, ANSWER_TYPE.MULTI_CHOICE], ctrl.challenge.answerType)) {
        ctrl.response.options = _.filter(ctrl.challenge.options, (opt) => {
          return opt.selected;
        }).map((opt) => {
          return opt.id;
        });
      }

      let defer = $q.defer();
      if (ctrl.image) {
        MessageService.showInfo({ text: MESSAGE_CODES.UPLOADING_IMAGE });

        imgur.setAPIKey('Bearer 72517f3b6ab122a8549bd46a09b404dcbf17d9df');
        imgur.upload(ctrl.image).then((image) => {
          ctrl.response.image = image.link;
          defer.resolve();
        }, (e) => {
          MessageService.showError({ text: MESSAGE_CODES.IMAGE_UPLOAD_FAILED });
          defer.reject();
        });
      } else {
        defer.resolve();
      }
      return defer.promise;
    }
  }
})();