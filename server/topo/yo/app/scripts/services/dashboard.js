define(['angular'], function(angular) {
    'use strict';

    /**
     * @ngdoc service
     * @name icosvrWebApp.Dashboard
     * @description
     * # Dashboard
     * Service in the icosvrWebApp.
     */
    angular.module('icosvrWebApp.services.Dashboard', []).factory(
        'dashboard', function($http, $cacheFactory, $log, apiBase) {

        // DashboardService class
        function DashboardService () {

            var self = this;

            self.allData = null;

            self.refreshAllData = function() {
                return $http.get(apiBase + '/dashboard')
                    .then(function(response) {
                        self.allData = response.data;
                        // $log.debug('*********' + JSON.stringify(self.allData));
                        return response;
                    });
            };

        }

        return new DashboardService();

    });
});
