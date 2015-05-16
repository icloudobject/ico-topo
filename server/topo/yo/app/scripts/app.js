/*jshint unused: vars */
define([ 'angular', 'controllers/home', 'controllers/terms', 'controllers/privacy', 'controllers/login', 
         'directives/ico-header', 'controllers/header', 'directives/ico-footer', 'controllers/footer', 
         'controllers/trial', 'controllers/trial-new', 'controllers/dashboard', 'controllers/topo', 
         'controllers/optimize', 'directives/ico-focus', 
         'services/api-base', 'services/tenant', 'services/dashboard', 'services/topo','services/cost',
         'components/dashboardgrid/dashboardGridDirective', 'components/dashboardgrid/dashboardGridController',
         'controllers/settings-profile', 'controllers/settings-password',
         'controllers/settings-agent', 'controllers/settings-cloud', 'controllers/signup','blockUI']/*deps*/, 
		function(angular, HomeCtrl, TermsCtrl, PrivacyCtrl, LoginCtrl, 
				IcoHeaderDirective, HeaderCtrl, IcoFooterDirective, FooterCtrl, 
				TrialCtrl, TrialNewCtrl, DashboardCtrl, TopoCtrl, OptimizeCtrl, IcoFocusDirective, 
				ApiBaseConstant, TenantService, DashboardService, TopoService,CostService,
				dashboardGridDirective, dashboardGridCtrl,
				SettingsProfileCtrl, SettingsPasswordCtrl,
				SettingsAgentCtrl, SettingsCloudCtrl, SignupCtrl)/*invoke*/{

	'use strict';

	/**
	 * @ngdoc overview
	 * @name icosvrWebApp
	 * @description # icosvrWebApp
	 *
	 * Main module of the application.
	 */
	return angular.module( 'icosvrWebApp',
			['icosvrWebApp.controllers.HomeCtrl', 'icosvrWebApp.controllers.TermsCtrl',
			 'icosvrWebApp.controllers.PrivacyCtrl', 'icosvrWebApp.controllers.LoginCtrl',
			 'icosvrWebApp.directives.IcoHeader', 'icosvrWebApp.controllers.HeaderCtrl',
			 'icosvrWebApp.directives.IcoFooter', 'icosvrWebApp.controllers.FooterCtrl',
			 'icosvrWebApp.controllers.TrialCtrl', 'icosvrWebApp.controllers.TrialNewCtrl',
			 'icosvrWebApp.controllers.DashboardCtrl', 'icosvrWebApp.controllers.TopoCtrl',
			 'icosvrWebApp.controllers.OptimizeCtrl', 'icosvrWebApp.controllers.SignupCtrl', 
			 'icosvrWebApp.directives.IcoFocus', 'icosvrWebApp.services.ApiBase',
			 'icosvrWebApp.services.Tenant', 'icosvrWebApp.services.Dashboard',
			 'icosvrWebApp.services.Topo', 'icosvrWebApp.services.Cost',
			 'icosvrWebApp.directives.dashboardGridDirective', 'icosvrWebApp.controllers.dashboardGridCtrl',
			 'icosvrWebApp.controllers.SettingsProfileCtrl', 'icosvrWebApp.controllers.SettingsPasswordCtrl',
			 'icosvrWebApp.controllers.SettingsAgentCtrl', 'icosvrWebApp.controllers.SettingsCloudCtrl',
/*angJSDeps*/
			  'ngAnimate', 'ngAria', 'ngCookies', 'ngMessages', 'ngResource', 'ngRoute', 'ngSanitize', 'ngTouch',
			  'ui.bootstrap', 'ui.utils', 'pascalprecht.translate', 'tmh.dynamicLocale', 'chart.js', 'ui.chart' ])

		.config(function($routeProvider, $translateProvider, tmhDynamicLocaleProvider, $locationProvider) {
			// routings and views
			$routeProvider.when('/', {
				templateUrl : 'views/home.html',
				controller : 'HomeCtrl'
			}).when('/terms', {
				templateUrl : 'views/terms.html',
				controller : 'TermsCtrl'
			}).when('/privacy', {
				templateUrl : 'views/privacy.html',
				controller : 'PrivacyCtrl'
			}).when('/trial', {
				templateUrl : 'views/trial.html',
				controller : 'TrialCtrl'
			}).when('/dashboard', {
				templateUrl : 'views/dashboard.html',
				controller : 'DashboardCtrl'
			}).when('/optimize', {
				templateUrl : 'views/optimize.html',
				controller : 'OptimizeCtrl'
			}).when('/topo', {
				templateUrl : 'views/topo.html',
				controller : 'TopoCtrl'
			}).otherwise({
				redirectTo : '/'
			});

			// use the HTML5 History API
			// 20150118 - cyrus: disabled as webapp serves agent, etc as well
			//$locationProvider.html5Mode(true);

			// i18n and translation
			$translateProvider.useStaticFilesLoader({
				prefix: '/i18n/',
				suffix: '.json'
			});
			$translateProvider.preferredLanguage('en-us');
			$translateProvider.useCookieStorage();
            tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js');
            tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');

	});

});
