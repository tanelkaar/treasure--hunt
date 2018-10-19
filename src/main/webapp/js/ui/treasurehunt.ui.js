(function() {
	'use strict'

	angular
		.module('treasurehunt.ui', [])
		.config(config);
	
	function config($stateProvider, $urlRouterProvider) {
		$stateProvider.state('main', {
			url: '/main',
			views: {
				'': {
					templateUrl: 'main.html',
					controller: 'MainCtrl as ctrl'
				}
			}
		});
		$stateProvider.state('map', {
			url : '/map',
			views: {
				'': {
					templateUrl: 'map.html',
					controller: 'MapCtrl as ctrl'
				}
			}
		});
		$urlRouterProvider.otherwise('/main');
	}
})();