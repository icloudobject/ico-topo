define([ 'angular' ], function(angular) {
	'use strict';

	/**
	 * @ngdoc directive
	 * @name icosvrWebApp.directive:icoFocus
	 * @description # icoFocus Make DOM element focus state available to the app code
	 */
	angular.module('icosvrWebApp.directives.IcoFocus', []).directive('icoFocus', function() {

		return {
			restrict : 'A',
			require : 'ngModel',
			link : function(scope, element, attrs, controller) {
				controller.$focused = false;
				element.bind('focus', function(e) {
					scope.$apply(function() {
						controller.$focused = true;
					});
				}).bind('blur', function(e) {
					scope.$apply(function() {
						controller.$focused = false;
					});
				});
			}
		};
	});

});
