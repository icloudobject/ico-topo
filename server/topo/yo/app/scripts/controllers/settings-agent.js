define(['angular'], function(angular) {
    'use strict';

    /**
     * @ngdoc function
     * @name icosvrWebApp.controller:SettingsAgentCtrl
     * @description
     * # SettingsAgentCtrl
     * Controller of the icosvrWebApp
     */
    angular.module('icosvrWebApp.controllers.SettingsAgentCtrl', []).controller(
        'SettingsAgentCtrl', function($scope, $log, $modalInstance, tenant) {

        $scope.agentInfo = null;

        tenant.getAgentInfo().then(function(response) {
            $scope.agentInfo = response.data;
        }, function(error) {
            $log.debug('init agent info failed: ' + JSON.stringify(error));
        });

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

    });
});
