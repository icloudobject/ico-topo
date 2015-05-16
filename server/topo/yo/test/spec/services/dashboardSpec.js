/*jshint unused: vars */
define(['angular', 'angular-mocks', 'app'], function(angular, mocks, app) {
  'use strict';

  describe('Service: Dashboard', function () {

    // load the service's module
    beforeEach(module('icosvrWebApp.services.Dashboard'));

    // instantiate service
    var Dashboard;
    beforeEach(inject(function (_Dashboard_) {
      Dashboard = _Dashboard_;
    }));

    it('should do something', function () {
      expect(!!Dashboard).toBe(true);
    });

  });
});
