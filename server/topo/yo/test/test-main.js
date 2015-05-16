var tests = [];
for (var file in window.__karma__.files) {
  if (window.__karma__.files.hasOwnProperty(file)) {
    // Removed "Spec" naming from files
    if (/Spec\.js$/.test(file)) {
      tests.push(file);
    }
  }
}

requirejs.config({
    // Karma serves files from '/base'
    baseUrl: '/base/app/scripts',

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
		'angular-busy': '../../bower_components/angular-busy/dist/angular-busy'
	},

    shim: {
        'angular' : {'exports' : 'angular'},
        'angular-route': ['angular'],
        'angular-cookies': ['angular'],
        'angular-sanitize': ['angular'],
        'angular-resource': ['angular'],
        'angular-animate': ['angular'],
        'angular-touch': ['angular'],
        'angular-mocks': {
          deps:['angular'],
          'exports':'angular.mock'
        }
    },

    // ask Require.js to load these files (all our tests)
    deps: tests,

    // start test run, once Require.js is done
    callback: window.__karma__.start
});
