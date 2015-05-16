/*jshint unused: vars */
define(['angular', 'angular-mocks', 'app'], function(angular, mocks, app) {
  'use strict';

  describe('Controller: SettingsPasswordCtrl', function () {

    // load the controller's module
    beforeEach(module('icosvrWebApp.controllers.SettingsPasswordCtrl'));

    var SettingsPasswordCtrl,
      scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
      scope = $rootScope.$new();
      SettingsPasswordCtrl = $controller('SettingsPasswordCtrl', {
        $scope: scope
      });
    }));

    it('should attach a list of awesomeThings to the scope', function () {
      expect(scope.awesomeThings.length).toBe(3);
    });
  });
});
