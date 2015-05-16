define(['angular'], function(angular) {
    'use strict';

    /**
     * @ngdoc function
     * @name icosvrWebApp.controller:SettingsPasswordCtrl
     * @description
     * # SettingsPasswordCtrl
     * Controller of the icosvrWebApp
     */
    angular.module('icosvrWebApp.controllers.SettingsPasswordCtrl', []).controller(
        'SettingsPasswordCtrl', function($scope, $log, $window, $modalInstance, tenant) {

        $scope.passCombo = {
            currentPassword: '',
            newPassword: '',
            newPasswordConfirm: ''
        };
        $scope.serverErrors = null;
        $scope.httpError = false;
        $scope.success = false;

        $scope.submitted = false;
        $scope.submitting = false;

        $scope.changePassword = function() {
            $scope.submitted = true;
            if($scope.passwordForm.$valid) {
                $scope.submitting = true;
                var promise = tenant.changePassword($scope.passCombo);
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
            resetPassForm();
            $scope.success = true;
            $scope.submitting = false;
        };

        var error = function(httpError) {
            $log.debug('Error: ' + JSON.stringify(httpError));
            $scope.httpError = true;
            $scope.submitting = false;
        };

        var resetPassForm = function() {
            $scope.passCombo = {
                currentPassword: '',
                newPassword: '',
                newPasswordConfirm: ''
            };
            $scope.passwordForm.$setPristine();
        };

    });
});
