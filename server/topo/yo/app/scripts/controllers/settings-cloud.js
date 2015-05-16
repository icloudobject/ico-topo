define(['angular'], function(angular) {
    'use strict';

    /**
     * @ngdoc function
     * @name icosvrWebApp.controller:SettingsCloudCtrl
     * @description
     * # SettingsCloudCtrl
     * Controller of the icosvrWebApp
     */
    angular.module('icosvrWebApp.controllers.SettingsCloudCtrl', []).controller(
    'SettingsCloudCtrl', function($scope, $log, $window, $modalInstance, tenant) {

        // load existing cloud accounts
        $scope.clouds = [];
        $scope.listHttpError = false;
        $scope.listClouds = function() {
            var success = function(response) {
                //$log.debug('Resp: ' + JSON.stringify(response));
                for(var i=0; i < response.data.length; i++) {
                    var res = response.data[i];
                    var c = {};
                    c.credKey = res.accessKey;
                    c.credSecret = res.accessSecret;
                    c.active = (res.active===null?true:res.active);
                    c.cloudName = res.cloudName;
                    $scope.clouds.push(c);
                }
            };
            var error = function(httpError) {
                $log.debug('Error: ' + JSON.stringify(httpError));
                $scope.listHttpError = true;
            };
            tenant.listClouds().then(success, error);
        };
        $scope.listClouds();

        // add new cloud account
        $scope.newCloud = {
            cloudName: 'aws',
            credKey: '',
            credSecret: '',
            active: true
        };
        $scope.addFormHttpError = false;
        $scope.addFormServerErrors = null;
        $scope.addFormSuccess = false;
        $scope.addFormSubmitted = false;
        $scope.addFormSubmitting = false;
        $scope.addCloud = function() {
            $scope.addFormSubmitted = true;
            var resetForm = function() {
                $scope.newCloud = {
                    cloudName: 'aws',
                    credKey: '',
                    credSecret: '',
                    active: true
                };
                $scope.newCloudForm.$setPristine();
            };
            var success = function(response) {
                var flag = response.data.success;
                if(flag !== undefined && flag === false) {
                    $log.debug('Rejected: ' + JSON.stringify(response));
                    $scope.serverErrors = response.data.fieldErrors;
                    return;
                }
                resetForm();
                $scope.addFormSuccess = true;
                $scope.addFormSubmitting = false;
            };
            var error = function(httpError) {
                $log.debug('Error: ' + JSON.stringify(httpError));
                $scope.addFormHttpError = true;
                $scope.addFormSubmitting = false;
            };
            if($scope.newCloudForm.$valid) {
                $scope.addFormSubmitting = true;
                var promise = tenant.addCloud($scope.newCloud);
                promise.then(success, error).then(
                    function() {
                        $scope.listClouds();
                    }
                );
            }
            else {
                $window.alert('Please fix input errors and try again.');
            }

        };

        // update existing cloud accounts
        $scope.httpError = false;
        $scope.serverErrors = null;
        $scope.success = false;
        $scope.submitted = false;
        $scope.submitting = false;

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };


    });
});
