define([ 'angular' ], function(angular) {
	'use strict';

	/**
	 * @ngdoc service
	 * @name icosvrWebApp.apiBase
	 * @description # apiBase Remote API base URL.
	 */
	angular.module('icosvrWebApp.services.ApiBase', []).constant('apiBase', '/api');

});
