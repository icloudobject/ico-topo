define(['angular'], function(angular) {
    'use strict';

    /**
     * @ngdoc function
     * @name icosvrWebApp.controller:DashboardCtrl
     * @description # DashboardCtrl Controller of the icosvrWebApp
     */
    angular.module('icosvrWebApp.controllers.DashboardCtrl', []).controller(
        'DashboardCtrl', function($scope, $interval, $log, dashboard) {

        	$scope.dataReady = false;

        	$scope.agentInfo = null;
            
            var checkDataReadiness = function() {
                $scope.dataReady = (dashboard.allData.instanceSummary.agentDataLagged != 0 
                		|| dashboard.allData.instanceSummary.agentDataOk != 0);
                return $scope.dataReady;
            };
            
            var prepareAgentInstallInfo = function() {
                tenant.getAgentInfo().then(function(response) {
                    $scope.agentInfo = response.data;
                }, function(error) {
                    $log.debug('init agent info failed: ' + JSON.stringify(error));
                });
            };

            var prepareInstanceChartData = function() {
                $scope.instanceTotal = dashboard.allData.instanceSummary.all;
                $scope.data = [
                    dashboard.allData.instanceSummary.agentNoData,
                    dashboard.allData.instanceSummary.agentDataLagged,
                    dashboard.allData.instanceSummary.agentDataOk
                ];
                $scope.labels = ['No Agent', 'Data Lagged', 'Data OK'];
            };

            var prepareSavingChartData = function() {
                $scope.totalSaving = dashboard.allData.savingSummary.totalSaving;
                $scope.totalCost = dashboard.allData.savingSummary.totalCost;
                $scope.data = [
                    dashboard.allData.savingSummary.unused,
                    dashboard.allData.savingSummary.low,
                    dashboard.allData.savingSummary.medium,
                    dashboard.allData.savingSummary.high,
                    dashboard.allData.savingSummary.overload,
                    dashboard.allData.savingSummary.uncategorized
                ];
                $scope.labels = [
                    'Unused', 'Low', 'Medium',
                    'High', 'Overload', 'Uncategorized'
                ];
            };

            var prepareCpuChartData = function() {
                $scope.cpuTotal = dashboard.allData.cpumemSummary.cpuTotal;
                $scope.cpuData = [
                    dashboard.allData.cpumemSummary.cpuUsed,
                    dashboard.allData.cpumemSummary.cpuIdle
                ];
                $scope.cpuLabels = ['CPU Used', 'CPU Idle'];

            };

            var prepareMemChartData = function() {
                $scope.memTotal = dashboard.allData.cpumemSummary.memoryTotal;
                $scope.memData = [
                    dashboard.allData.cpumemSummary.memoryUsed,
                    dashboard.allData.cpumemSummary.memoryAvail
                ];
                $scope.memLabels = ['Mem Used', 'Mem Avail'];
            };

            var refreshData = function() {
                dashboard.refreshAllData().then(
                    function() {
                    	checkDataReadiness();
                    	if(!$scope.dataReady) {
                    		prepareAgentInstallInfo();
                    		return;
                    	}
                        prepareCpuChartData();
                        prepareMemChartData();
                        prepareSavingChartData();
                        prepareInstanceChartData();
                    });
            };

            refreshData();
            $interval(refreshData, 10000);
            $log.debug('dashboard data refresh job started');

        });

});
