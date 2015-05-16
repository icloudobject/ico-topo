define([ 'angular' ], function(angular) {
	'use strict';

	/**
	 * @ngdoc directive
	 * @name icosvrWebApp.directive:icoFooter
	 * @description # icoFooter
	 */
	angular.module('icosvrWebApp.directives.IcoFooter', []).directive('icoFooter', function() {

		return {
			restrict : 'E',
			templateUrl : 'views/footer.html',
			controller : 'FooterCtrl'
		};
	});

});
