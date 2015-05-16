define([ 'angular', 'components/wfresourcegrid/wfresourcegrid' ], function(angular, wfresourcegrid) {
	'use strict';

	/**
	 * @ngdoc function
	 * @name icosvrWebApp.controller:TopoCtrl
	 * @description # TopoCtrl Controller of the icosvrWebApp
	 */
	angular.module('icosvrWebApp.controllers.OptimizeCtrl', [ 'wfresourcegrid' ]).controller('OptimizeCtrl',
			function($scope, $log) {

				$log.info('from optimize controller');

			});

});
