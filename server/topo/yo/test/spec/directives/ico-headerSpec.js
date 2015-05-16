/*jshint unused: vars */
define(['angular', 'angular-mocks', 'app'], function(angular, mocks, app) {
  'use strict';

  describe('Directive: icoHeader', function () {

    // load the directive's module
    beforeEach(module('icosvrWebApp.directives.IcoHeader'));

    var element,
      scope;

    beforeEach(inject(function ($rootScope) {
      scope = $rootScope.$new();
    }));

    it('should make hidden element visible', inject(function ($compile) {
      element = angular.element('<ico-header></ico-header>');
      element = $compile(element)(scope);
      expect(element.text()).toBe('this is the icoHeader directive');
    }));
  });
});
