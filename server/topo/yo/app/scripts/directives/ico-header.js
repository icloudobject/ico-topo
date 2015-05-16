define([ 'angular' ], function(angular) {
	'use strict';

	/**
	 * @ngdoc directive
	 * @name icosvrWebApp.directive:icoHeader
	 * @description # icoHeader
	 */
	angular.module('icosvrWebApp.directives.IcoHeader', []).directive('icoHeader', function() {

		return {
			restrict : 'E',
			templateUrl : 'views/header.html',
			controller : 'HeaderCtrl'
		};

	});

});
