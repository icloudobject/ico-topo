define([ 'angular' ], function(angular) {
    'use strict';

    /**
	 * @ngdoc function
	 * @name icosvrWebApp.controller:HeaderCtrl
	 * @description # HeaderCtrl Controller of the icosvrWebApp
	 */
	angular.module('icosvrWebApp.controllers.HeaderCtrl', [])
        .controller('HeaderCtrl', function($scope, $location, $log, $modal, tenant) {

            $scope.loginUser = tenant.loginUser;
            $scope.$watch(function() {
                return tenant.loginUser;
            }, function(newVal) {
                $scope.loginUser = newVal;
            });

             // Trial button
            $scope.initTrialPage = function() {
                $location.path('/trial');
            };

            // Login button
            $scope.openLoginModal = function(size) {
                var modalInstance = $modal.open({
                    templateUrl : 'views/login.html',
                    controller : 'LoginCtrl',
                    size : size
                });
                modalInstance.result.then(
                    // resolved: close
                    function() {},
                    // rejected: dismiss
                    function() {}
                );
            };

            // Edit Profile link
            $scope.openProfileModal = function(size) {
                var modalInstance = $modal.open({
                    templateUrl : 'views/settings-profile.html',
                    controller : 'SettingsProfileCtrl',
                    size : size
                });
                modalInstance.result.then(
                    // resolved: close
                    function() {},
                    // rejected: dismiss
                    function() {}
                );
            };

            // Change Password link
            $scope.openPasswordModal = function(size) {
                var modalInstance = $modal.open({
                    templateUrl : 'views/settings-password.html',
                    controller : 'SettingsPasswordCtrl',
                    size : size
                });
                modalInstance.result.then(
                    // resolved: close
                    function() {},
                    // rejected: dismiss
                    function() {}
                );
            };

            // Install Agent link
            $scope.openAgentModal = function(size) {
                var modalInstance = $modal.open({
                    templateUrl : 'views/settings-agent.html',
                    controller : 'SettingsAgentCtrl',
                    size : size
                });
                modalInstance.result.then(
                    // resolved: close
                    function() {},
                    // rejected: dismiss
                    function() {}
                );
            };

            // Change Cloud link
            $scope.openCloudModal = function(size) {
                var modalInstance = $modal.open({
                    templateUrl : 'views/settings-cloud.html',
                    controller : 'SettingsCloudCtrl',
                    size : size
                });
                modalInstance.result.then(
                    // resolved: close
                    function() {},
                    // rejected: dismiss
                    function() {}
                );
            };

            // Formal sign up link
            $scope.openSignupModal = function(size) {
                var modalInstance = $modal.open({
                    templateUrl : 'views/signup.html',
                    controller : 'SignupCtrl',
                    size : size
                });
                modalInstance.result.then(
                    // resolved: close
                    function() {},
                    // rejected: dismiss
                    function() {}
                );
            };

        }
    );
});
