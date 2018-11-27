(function () {
  'use strict'

  angular
    .module('treasurehunt.ui')
    .controller('SummaryCtrl', SummaryCtrl);

  function SummaryCtrl(games, $interval, AdminService, MessageService, MESSAGE_CODES) {
    let _trail = null;
    let _times = null;

    let ctrl = {
      games: games,
      game: null,
      team: null,
      trail: [],
      loadGame: loadGame,
      saveScore: saveScore,
      selectTeam: selectTeam,
      getScore: getScore,

      isOptionSelected: isOptionSelected,
      drawTrail: drawTrail,
      changeRange: changeRange,
      drawingTrail: false
    };
    return ctrl = angular.extend(this, ctrl);

    function loadGame(id) {
      AdminService.getGame(id).then((rsp) => {
        ctrl.game = rsp.data;
        _.each(ctrl.game.teams, (team) => {
          if (team.trail.length > 0) {
            team.time = getTime(team.trail[0].timestamp, team.trail[team.trail.length - 1].timestamp);
          }
        });
      });
    }

    function getScore(team) {
      let score = 0;
      _.each(team.completedChallenges, (challenge) => {
        score += challenge.challengeResponse.score || 0;
      });
      return score;
    }

    function saveScore() {
      AdminService.saveGame(ctrl.game).then((rsp) => {
        MessageService.showInfo({ text: MESSAGE_CODES.SCORE_SAVED });
      });
    }

    function selectTeam(team) {
      ctrl.nav = 'RESPONSES';
      ctrl.team = team;

      _trail = [];
      _times = [];

      ctrl.trailIdx = 0;
      ctrl.trailTime = '00:00:00.000';
      _.each(ctrl.team.trail, (trail) => {
        _trail.push([trail.coordinates.lat, trail.coordinates.lng]);
        _times.push(trail.timestamp);
      }); 
      ctrl.trail = [_trail[0]];
    }

    function isOptionSelected(value, options) {
      return _.find(options, (optValue) => {
        return optValue === value;
      });
    }

    function changeRange() {
      ctrl.trail = _trail.slice(0, ctrl.trailIdx);
      ctrl.trailTime = getTime(_times[0], _times[ctrl.trailIdx]);
    }

    function drawTrail() {
      if (ctrl.trailIdx >= _trail.length) {
        ctrl.trailIdx = 0;
        ctrl.trail = [];
      }
      ctrl.drawingTrail = true;
      let trailWatcher = $interval(() => {
        if (ctrl.trailIdx >= _trail.length) {
          ctrl.drawingTrail = false;
          $interval.cancel(trailWatcher);
          return;
        }
        let idx = ctrl.trailIdx++;
        ctrl.trail.push(_trail[idx]);
        ctrl.trailTime = getTime(_times[0], _times[idx]);
      }, 100);
    }

    function getTime(start, end) {
      return moment.utc(moment(end).diff(start)).format("HH:mm:ss.SSS");
    }
  }
})();