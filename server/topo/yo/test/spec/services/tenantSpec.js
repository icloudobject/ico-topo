/*jshint unused: vars */
define(['angular', 'angular-mocks', 'app'], function(angular, mocks, app) {
  'use strict';

  describe('Service: Tenant', function () {

    // load the service's module
    beforeEach(module('icosvrWebApp.services.Tenant'));

    // instantiate service
    var Tenant;
    beforeEach(inject(function (_Tenant_) {
      Tenant = _Tenant_;
    }));

    it('should do something', function () {
      expect(!!Tenant).toBe(true);
    });

  });
});
