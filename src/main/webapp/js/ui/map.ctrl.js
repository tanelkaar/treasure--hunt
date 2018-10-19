(function() {
	'use strict'

	angular
		.module('treasurehunt.ui')
		.controller('MapCtrl', MapCtrl);
	
	function MapCtrl($state, NgMap) {
		let ctrl = {
			map: {},
		};
		init();
		return angular.extend(this, ctrl);

		function init() {
			NgMap.getMap().then(function(map) {
				ctrl.map =  map;
			});
		}
	}
})();