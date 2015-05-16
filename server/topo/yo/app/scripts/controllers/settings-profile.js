define(['angular'], function(angular) {
    'use strict';

    /**
     * @ngdoc function
     * @name icosvrWebApp.controller:SettingsProfileCtrl
     * @description
     * # SettingsProfileCtrl
     * Controller of the icosvrWebApp
     */
    angular.module('icosvrWebApp.controllers.SettingsProfileCtrl', []).controller(
        'SettingsProfileCtrl', function($scope, $log, $window, $modalInstance, tenant) {

        $scope.profile = {
            displayName: tenant.loginUser.displayName,
            email: tenant.loginUser.email,
            company: tenant.loginUser.company
        };
        $scope.serverErrors = null;
        $scope.httpError = false;
        $scope.success = false;

        $scope.submitted = false;
        $scope.submitting = false;

        $scope.updateProfile = function() {
            $scope.submitted = true;
            if($scope.profileForm.$valid) {
                $scope.submitting = true;
                var promise = tenant.updateProfile($scope.profile);
                promise.then(success, error);
            }
            else {
                $window.alert('Please fix input errors and try again.');
            }
        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        var success = function(response) {
            var flag = response.data.success;
            if(flag !== undefined && flag === false) {
                $log.debug('Rejected: ' + JSON.stringify(response));
                $scope.serverErrors = response.data.fieldErrors;
                $scope.submitting = false;
                return;
            }
            tenant.getUserInfo().then(function() {
                $scope.success = true;
                $scope.submitting = false;
            }, error);
        };

        var error = function(httpError) {
            $log.debug('Error: ' + JSON.stringify(httpError));
            $scope.httpError = true;
            $scope.submitting = false;
        };

    });
});
