(function() {
	'use strict'

	angular
		.module('treasurehunt.ui')
		.controller('ChallengeCtrl', ChallengeCtrl);
	
	function ChallengeCtrl($state) {
		let ctrl = {
			finish: finish
		}
		return ctrl = angular.extend(this, ctrl);
		
		function finish() {
			$state.go('map');
		}
	}
})();