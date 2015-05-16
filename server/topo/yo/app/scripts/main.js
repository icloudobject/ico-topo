/*jshint unused: vars */
require.config({
	paths: {
		angular: '../../bower_components/angular/angular',
		'angular-animate': '../../bower_components/angular-animate/angular-animate',
		'angular-ui-grid': '../../bower_components/angular-ui-grid/ui-grid',
		'angular-aria': '../../bower_components/angular-aria/angular-aria',
		'angular-cookies': '../../bower_components/angular-cookies/angular-cookies',
		'angular-messages': '../../bower_components/angular-messages/angular-messages',
		'angular-resource': '../../bower_components/angular-resource/angular-resource',
		'angular-route': '../../bower_components/angular-route/angular-route',
		'angular-sanitize': '../../bower_components/angular-sanitize/angular-sanitize',
		'angular-touch': '../../bower_components/angular-touch/angular-touch',
		'angular-mocks': '../../bower_components/angular-mocks/angular-mocks',
		'angular-scenario': '../../bower_components/angular-scenario/angular-scenario',
		jquery: '../../bower_components/jquery/dist/jquery',
		'angular-bootstrap': '../../bower_components/angular-bootstrap/ui-bootstrap-tpls',
		'angular-ui-utils': '../../bower_components/angular-ui-utils/ui-utils',
		hammer: '../../bower_components/hammerjs/hammer.min',
		'angular-material': '../../bower_components/angular-material/angular-material',
		tooltipster: '../../bower_components/tooltipster/js/jquery.tooltipster.min',
		d3: '../../bower_components/d3/d3',
		'angular-dynamic-locale': '../../bower_components/angular-dynamic-locale/src/tmhDynamicLocale',
		'angular-translate': '../../bower_components/angular-translate/angular-translate',
		'angular-translate-loader-static-files': '../../bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files',
		'angular-translate-storage-cookie': '../../bower_components/angular-translate-storage-cookie/angular-translate-storage-cookie',
		chartjs: '../../bower_components/Chart.js/Chart.min',
		'angular-chartjs': '../../bower_components/angular-chart.js/dist/angular-chart',
		'angular-ui-chart': '../../bower_components/angular-ui-chart/src/chart',
		jqplot: '../../bower_components/jqplot/jquery.jqplot.min',
		'angular-chart': '../../bower_components/angular-chart.js/dist/angular-chart',
		hammerjs: '../../bower_components/hammerjs/hammer',
		'blockUI':'utils/blockUI'
	},
	shim: {
		'blockUI':['jquery'],
		hammer: {
			exports: 'Hammer'
		},
		d3: {
			exports: 'd3'
		},
		tooltipster: [
			'jquery'
		],
		'angular-material': {
			deps: [
				'hammer',
				'angular',
				'angular-animate',
				'angular-aria'
			],
			exports: 'ngMaterial'
		},
		angular: {
			deps: [
				'jquery'
			],
			exports: 'angular'
		},
		'angular-animate': [
			'angular'
		],
		'angular-aria': [
			'angular'
		],
		'angular-cookies': [
			'angular'
		],
		'angular-messages': [
			'angular'
		],
		'angular-mocks': {
			deps: [
				'angular'
			],
			exports: 'angular.mock'
		},
		'angular-resource': [
			'angular'
		],
		'angular-route': [
			'angular'
		],
		'angular-sanitize': [
			'angular'
		],
		'angular-touch': [
			'angular'
		],
		'angular-bootstrap': [
			'angular'
		],
		'angular-ui-utils': [
			'angular'
		],
		'angular-dynamic-locale': [
			'angular'
		],
		'angular-translate': [
			'angular'
		],
		'angular-ui-grid': [
			'angular'
		],
		'angular-translate-loader-static-files': [
			'angular-translate'
		],
		'angular-translate-storage-cookie': [
			'angular-translate'
		],
		chartjs: {
			exports: 'Chart'
		},
		'angular-chartjs': [
			'angular',
			'chartjs'
		],
		jqplot: {
			exports: 'jqPlot'
		},
		'angular-ui-chart': [
			'angular',
			'jqplot'
		]
	},
	priority: [
		'angular'
	],
	packages: [

	]
});

// http://code.angularjs.org/1.2.1/docs/guide/bootstrap#overview_deferred-bootstrap
window.name = 'NG_DEFER_BOOTSTRAP!';

require([ 'jquery', 'angular', 'app', 'hammer', 'tooltipster', 'd3', 'angular-animate', 'angular-aria', 
          'angular-cookies', 'angular-messages', 'angular-resource', 'angular-route', 'angular-sanitize',
          'angular-touch', 'angular-bootstrap', 'angular-ui-utils', 'angular-dynamic-locale',
          'angular-translate', 'angular-translate-loader-static-files', 'angular-translate-storage-cookie',
          'angular-chartjs', 'angular-ui-grid', 'angular-ui-chart' ],
		function($, angular, app, hammer, translate) {
	'use strict';
	// Ferry: fix hammerjs issue exports issue in requireJs
	window.Hammer = hammer;
	/* jshint ignore:start */
	var $html = angular.element(document.getElementsByTagName('html')[0]);
	/* jshint ignore:end */
	angular.element().ready(function() {
		angular.resumeBootstrap([ app.name ]);
	});
});
