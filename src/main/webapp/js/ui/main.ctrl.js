(function() {
	'use strict'

	angular
		.module('treasurehunt.ui')
		.controller('MainCtrl', MainCtrl);
	
	function MainCtrl($state) {
		let ctrl = {
			start: start
		}
		return angular.extend(this, ctrl);
		
		function start() {
			$state.go('map');
		}
	}
})();