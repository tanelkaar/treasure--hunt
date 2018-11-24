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
      scope: {
        fileName: '='
      },
      require: 'ngModel',
      link: link
    }
    return dir;

    function link(scope, elem, attrs, ngModel) {
      elem.on('change', (event) => {
        let reader = new FileReader();
        reader.onload = (loadEvent) => {
          ngModel.$setViewValue(loadEvent.target.result);
        }
        let file = event.target.files[0];
        scope.fileName = file.name;
        reader.readAsDataURL(file);
      });
    }
  }

  function ChallengeCtrl(challenge, $state, GameService, MessageService, CHALLENE_TYPE, ANSWER_TYPE) {
    console.log('challenge ctrl');
    let ctrl = {
      response: {
        challengeId: challenge.id
      },
      challenge: challenge,
      challenges: [
        {
          id: 1,
          type: CHALLENE_TYPE.QUESTION,
          text: 'What is on the picture?',
          image: 'https://drive.google.com/uc?id=1z8i7Hurg0oq3deTziNbjoGg1iAKkkmr0',
          answerType: ANSWER_TYPE.TEXT
        },
        {
          id: 2,
          type: CHALLENE_TYPE.QUESTION,
          text: 'Which came first: the chicken or the egg?',
          options: [{ id: 1, name: 'the chiken' }, { id: 2, name: 'the egg' }],
          answerType: ANSWER_TYPE.SINGLE_CHOICE
        },
        {
          id: 3,
          type: CHALLENE_TYPE.QUESTION,
          text: 'Kessee laulab köögis?',
          video: 'https://www.youtube.com/embed/FIH5gF1z4SY?rel=0',
          options: [{ id: 1, name: 'kukk' }, { id: 2, name: 'kana' }, { id: 3, name: 'justament' }],
          answerType: ANSWER_TYPE.MULTI_CHOICE
        },
        {
          id: 4,
          type: CHALLENE_TYPE.TASK,
          text: 'Take a selfie!',
          answerType: ANSWER_TYPE.IMAGE
        }
      ],
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
      _.each(ctrl.challenge.options, (opt) => {
        if (option.id != opt.id) {
          opt.selected = false;
        }
      });
    }

    function complete() {
      if (_.contains([ANSWER_TYPE.SINGLE_CHOICE, ANSWER_TYPE.MULTI_CHOICE], ctrl.challenge.answerType)) {
        ctrl.response.options = _.filter(ctrl.challenge.options, (opt) => {
          return opt.selected;
        }).map((opt) => {
          return opt.id;
        });
      }
      GameService.completeChallenge(ctrl.response);
    }
  }
})();