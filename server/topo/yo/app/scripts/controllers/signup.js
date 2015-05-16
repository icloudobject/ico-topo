define(['angular'], function(angular) {
    'use strict';

    /**
     * @ngdoc function
     * @name icosvrWebApp.controller:SignupCtrl
     * @description
     * # SignupCtrl
     * Controller of the icosvrWebApp
     */
    angular.module('icosvrWebApp.controllers.SignupCtrl', []).controller(
        'SignupCtrl', function($scope, $log, $window, $modalInstance, tenant) {

        $scope.formalUser = {
            username: "",
            password: "",
            email: ""
        };
        $scope.serverErrors = null;
        $scope.httpError = false;
        $scope.success = false;

        $scope.submitted = false;
        $scope.submitting = false;

        $scope.signup = function() {
            $scope.submitted = true;
            if($scope.signupForm.$valid) {
                $scope.submitting = true;
                var promise = tenant.signup($scope.formalUser);
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
