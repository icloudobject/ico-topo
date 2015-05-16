/*jshint unused: vars */
define(['angular', 'angular-mocks', 'app'], function(angular, mocks, app) {
  'use strict';

  describe('Service: apiBase', function () {

    // load the service's module
    beforeEach(module('icosvrWebApp.services.ApiBase'));

    // instantiate service
    var apiBase;
    beforeEach(inject(function (_apiBase_) {
      apiBase = _apiBase_;
    }));

    it('should do something', function () {
      expect(!!apiBase).toBe(true);
    });

  });
});
